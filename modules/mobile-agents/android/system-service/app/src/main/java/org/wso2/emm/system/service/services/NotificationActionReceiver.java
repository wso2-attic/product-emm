package org.wso2.emm.system.service.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.api.OTAServerManager;
import org.wso2.emm.system.service.utils.CommonUtils;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;

import java.net.MalformedURLException;

public class NotificationActionReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationActionReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.DEFAULT_NOTIFICATION_CODE);
        String action = intent.getAction();

        try {
            OTAServerManager otaServerManager = new OTAServerManager(context);
            if (Constants.FIRMWARE_INSTALL_ACTION.equals(action)) {
                otaServerManager.startInstallUpgradePackage();
                Log.d(TAG, "Installing firmware upon user's confirmation.");
            } else if (Constants.FIRMWARE_CANCEL_INSTALL_ACTION.equals(action)) {
                String message = "Firmware upgrade has been canceled by the user.";
                otaServerManager.sendBroadcast(Constants.Operation.FIRMWARE_INSTALLATION_CANCELED,
                        Constants.Status.CANCELED, message);
                CommonUtils.callAgentApp(context, Constants.Operation.FIRMWARE_INSTALLATION_CANCELED, Preference.getInt(
                        context, context.getResources().getString(R.string.operation_id)), message);
                Log.d(TAG, message);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error in getting OTA Server Manager. ", e);
        }
    }

}
