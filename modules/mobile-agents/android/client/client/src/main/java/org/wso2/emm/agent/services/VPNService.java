/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.emm.agent.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;
import org.wso2.emm.agent.AlertActivity;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.utils.Preference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class VPNService extends VpnService implements Handler.Callback, Runnable {
    private static final String TAG = "VPNService";
    private static final int CONNECTION_RETRY_COUNT = 10;
    private static final int CONNECTION_WAIT_TIMOUT = 3000;
    private static final int HANDSHAKE_BUFFER = 1024;
    private static final int DEFAULT_PACKET_SIZE = 32767;
    private static final int REQUEST_CODE = 0;
    private static final int DEFAULT_INTENT_FLAG = 0;
    private static final int DEFAULT_TIMER = 100;
    private static final int DEFAULT_TIMER_NEGATIVE = -100;
    private static final int DEFAULT_TIMER_MIN = -15000;
    private static final int DEFAULT_TIMER_MAX = 20000;
    private static final int WRITER_RETRY_COUNT = 3;
    private static final char MTU = 'm';
    private static final char ADDRESS = 'a';
    private static final char ROUTE = 'r';
    private static final char DNS = 'd';
    private static final char SEARCH_DOMAIN = 's';
    private static final String COLON = ":";
    private String serverAddress;
    private String serverPort;
    private String dnsServer;
    private byte[] sharedSecret;
    private Handler handler;
    private Thread vpnThread;
    private ParcelFileDescriptor fileDescriptor;
    private String vpnParameters;
    private PendingIntent configureIntent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The handler is only used to show messages.
        if (handler == null) {
            handler = new Handler(this);
        }
        // Stop the previous session by interrupting the thread.
        if (vpnThread != null) {
            vpnThread.interrupt();
        }
        // Extract information from the intent.
        String prefix = getPackageName();
        serverAddress = intent.getStringExtra(prefix + getResources().getString(R.string.address));
        Preference.putString(this, getResources().getString(R.string.address), serverAddress);

        if(intent.hasExtra(prefix + getResources().getString(R.string.port))) {
            serverPort = intent.getStringExtra(prefix + getResources().getString(R.string.port));
            Preference.putString(this, getResources().getString(R.string.port), serverPort);
        }

        if(intent.hasExtra(prefix + getResources().getString(R.string.secret))) {
            sharedSecret = intent.getStringExtra(prefix + getResources().getString(R.string.secret)).getBytes();
            Preference.putString(this, getResources().getString(R.string.secret), intent.getStringExtra(prefix +
                                                                            getResources().getString(R.string.secret)));
        }

        if(intent.hasExtra(prefix + getResources().getString(R.string.dns))) {
            dnsServer = intent.getStringExtra(prefix + getResources().getString(R.string.dns));
            Preference.putString(this, getResources().getString(R.string.dns), dnsServer);
        }

        Intent pendingIntent = new Intent(this, AlertActivity.class);
        configureIntent = PendingIntent.getActivity(this, REQUEST_CODE, pendingIntent, DEFAULT_INTENT_FLAG);

        // Start a new session by creating a new thread.
        vpnThread = new Thread(this, TAG);
        vpnThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (vpnThread != null) {
            vpnThread.interrupt();
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message != null) {
            Toast.makeText(this, message.what, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public synchronized void run() {
        try {
            Log.d(TAG, "Starting");
            // If anything needs to be obtained using the network, get it now.
            // This greatly reduces the complexity of seamless handover, which
            // tries to recreate the tunnel without shutting down everything.
            // In this mode, all we need to know is the server address.
            InetSocketAddress server = new InetSocketAddress(
                    serverAddress, Integer.parseInt(serverPort));
            // We try to create the tunnel for several times.
            for (int attempt = 0; attempt < CONNECTION_RETRY_COUNT; ++attempt) {
                handler.sendEmptyMessage(R.string.connecting);
                // Reset the counter if we were connected.
                if (run(server)) {
                    attempt = 0;
                }
                // Sleep for a while. This also checks if we got interrupted.
                Thread.sleep(CONNECTION_WAIT_TIMOUT);
            }
            Log.d(TAG, "Giving up");
        } catch (InterruptedException e) {
            Log.e(TAG, "VPN thread interrupted " + e);
        } finally {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                Log.e(TAG, "FileDescriptor close failed " + e);
            }
            fileDescriptor = null;
            vpnParameters = null;
            handler.sendEmptyMessage(R.string.disconnected);
            Log.d(TAG, "Exiting");
        }
    }

    private boolean run(InetSocketAddress server) throws InterruptedException {
        DatagramChannel tunnel = null;
        boolean connected = false;
        try {
            // Create a DatagramChannel as the VPN tunnel.
            tunnel = DatagramChannel.open();
            // Protect the tunnel before connecting to avoid loopback.
            if (!protect(tunnel.socket())) {
                throw new IllegalStateException("Cannot protect the tunnel");
            }
            // Connect to the server.
            tunnel.connect(server);
            // Here we put the tunnel into non-blocking mode.
            tunnel.configureBlocking(false);
            // Authenticate and configure the virtual network interface.
            if(sharedSecret != null) {
                handshake(tunnel);
            } else {
                establishVPN();
            }
            // Now we are connected. Set the flag and show the message.
            connected = true;
            handler.sendEmptyMessage(R.string.connected);
            // Packets to be sent are queued in this input stream.
            FileInputStream in = new FileInputStream(fileDescriptor.getFileDescriptor());
            // Packets received need to be written to this output stream.
            FileOutputStream out = new FileOutputStream(fileDescriptor.getFileDescriptor());
            // Allocate the buffer for a single packet.
            ByteBuffer packet = ByteBuffer.allocate(DEFAULT_PACKET_SIZE);
            // We use a timer to determine the status of the tunnel. It
            // works on both sides. A positive value means sending, and
            // any other means receiving. We start with receiving.
            int timer = 0;
            // We keep forwarding packets till something goes wrong.
            while (true) {
                // Assume that we did not make any progress in this iteration.
                boolean idle = true;
                // Read the outgoing packet from the input stream.
                int length = in.read(packet.array());
                if (length > 0) {
                    // Write the outgoing packet to the tunnel.
                    packet.limit(length);
                    tunnel.write(packet);
                    packet.clear();
                    // There might be more outgoing packets.
                    idle = false;
                    // If we were receiving, switch to sending.
                    if (timer < 1) {
                        timer = 1;
                    }
                }
                // Read the incoming packet from the tunnel.
                length = tunnel.read(packet);
                if (length > 0) {
                    // Ignore control messages, which start with zero.
                    if (packet.get(0) != 0) {
                        // Write the incoming packet to the output stream.
                        out.write(packet.array(), 0, length);
                    }
                    packet.clear();
                    // There might be more incoming packets.
                    idle = false;
                    // If we were sending, switch to receiving.
                    if (timer > 0) {
                        timer = 0;
                    }
                }
                // If we are idle or waiting for the network, sleep for a
                // fraction of time to avoid busy looping.
                if (idle) {
                    Thread.sleep(CONNECTION_WAIT_TIMOUT);
                    // since everything is operated in non-blocking mode.
                    timer += (timer > 0) ? DEFAULT_TIMER : DEFAULT_TIMER_NEGATIVE;
                    // We are receiving for a long time but not sending.
                    if (timer < DEFAULT_TIMER_MIN) {
                        // Send empty control messages.
                        packet.put((byte) 0).limit(1);
                        for (int i = 0; i < WRITER_RETRY_COUNT; ++i) {
                            packet.position(0);
                            tunnel.write(packet);
                        }
                        packet.clear();
                        // Switch to sending.
                        timer = 1;
                    }
                    // We are sending for a long time but not receiving.
                    if (timer > DEFAULT_TIMER_MAX) {
                        throw new IllegalStateException("Timed out");
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Thread interrupted " + e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Tunnel read/write failed " + e);
        } finally {
            try {
                if (tunnel != null) {
                    tunnel.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Tunnel close failed " + e);
            }
        }
        return connected;
    }

    private void handshake(DatagramChannel tunnel) {
        // To build a secured tunnel, we should perform mutual authentication
        // and exchange session keys for encryption. We send the shared secret and wait
        // for the server to send the parameters.
        // Allocate the buffer for handshaking.
        ByteBuffer packet = ByteBuffer.allocate(HANDSHAKE_BUFFER);
        // Control messages always start with zero.
        packet.put((byte) 0).put(sharedSecret).flip();
        // Send the secret several times in case of packet loss.
        for (int i = 0; i < CONNECTION_RETRY_COUNT; ++i) {
            packet.position(0);
            try {
                tunnel.write(packet);
            } catch (IOException e) {
                Log.e(TAG, "Tunnel write failed " + e);
            }
        }
        packet.clear();
        // Wait for the parameters within a limited time.
        for (int i = 0; i < CONNECTION_RETRY_COUNT; ++i) {
            try {
                Thread.sleep(CONNECTION_WAIT_TIMOUT);
                int length = tunnel.read(packet);
                if (length > 0 && packet.get(0) == 0) {
                    configure(new String(packet.array(), 1, length - 1).trim());
                    return;
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted " + e);
            } catch (IOException e) {
                Log.e(TAG, "Tunnel read failed " + e);
            }
        }
    }

    private void establishVPN() {
        Builder builder = new Builder();
        String address;
        if(serverPort != null) {
            address = serverAddress + COLON + serverPort;
        } else {
            address = serverAddress;
        }

        builder.addAddress(address, DEFAULT_INTENT_FLAG);
        if(dnsServer != null) {
            builder.addDnsServer(dnsServer);
        }

        fileDescriptor = builder.setSession(serverAddress)
                .setConfigureIntent(configureIntent)
                .establish();
    }

    private void configure(String parameters) {
        if (fileDescriptor != null && parameters.equals(vpnParameters)) {
            Log.d(TAG, "Using the previous interface");
            return;
        }
        // Configure a builder while parsing the parameters.
        Builder builder = new Builder();

        for (String parameter : parameters.split(" ")) {
            String[] fields = parameter.split(",");
            switch (fields[0].charAt(0)) {
                    case MTU:
                        builder.setMtu(Short.parseShort(fields[1]));
                        break;
                    case ADDRESS:
                        builder.addAddress(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case ROUTE:
                        builder.addRoute(fields[1], Integer.parseInt(fields[2]));
                        break;
                    case DNS:
                        builder.addDnsServer(fields[1]);
                        break;
                    case SEARCH_DOMAIN:
                        builder.addSearchDomain(fields[1]);
                        break;
                    default:
                        break;
            }
        }
        // Close the old interface since the parameters have been changed.
        try {
            fileDescriptor.close();
        } catch (IOException e) {
            Log.e(TAG, "File descriptor is already closed " + e);
        }
        // Create a new interface using the builder and save the parameters.
        fileDescriptor = builder.setSession(serverAddress)
                .setConfigureIntent(configureIntent)
                .establish();
        vpnParameters = parameters;
        Log.d(TAG, "New interface: " + parameters);
    }
}
