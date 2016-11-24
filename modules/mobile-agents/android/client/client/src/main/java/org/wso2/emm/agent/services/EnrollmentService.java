package org.wso2.emm.agent.services;

import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AlreadyRegisteredActivity;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.EnableDeviceAdminActivity;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.events.EventRegistry;
import org.wso2.emm.agent.proxy.authenticators.AuthenticatorFactory;
import org.wso2.emm.agent.proxy.authenticators.ClientAuthenticator;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.interfaces.AuthenticationCallback;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import java.util.Map;

/**
 * This service handles the auto-enrollment of the device when using Mutual SSL authentication in COPE mode.
 * For this service to function properly, all the Mutual SSL related configurations should be properly performed.
 * Please refer product documentation for more information on Auto Enrollment process.
 */
public class EnrollmentService extends IntentService implements APIResultCallBack, AuthenticationCallback {
    private static final String TAG = EnrollmentService.class.getName();
    private Context context;
    private DeviceInfoPayload deviceInfoBuilder;
    private DeviceInfo info;
    private ComponentName cdmDeviceAdmin;

    public EnrollmentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = this.getApplicationContext();
        info = new DeviceInfo(context);
        cdmDeviceAdmin = new ComponentName(this, AgentDeviceAdminReceiver.class);
        if (Constants.DEFAULT_HOST != null) {
            startEnrollment();
        } else {
            Log.e(TAG, "Auto enrollment failed, default host not set.");
        }

    }

    private void startEnrollment() {
        Log.i(TAG, "EMM auto enrollment initiated.");
        if (CommonUtils.
                isNetworkAvailable(context)) {
            CommonUtils.saveHostDeatils(context, Constants.DEFAULT_HOST);
            //Setting default ownership to COPE
            Preference.putString(context, Constants.DEVICE_TYPE, Constants.OWNERSHIP_COPE);
            AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory();
            ClientAuthenticator authenticator = authenticatorFactory.getClient(
                    org.wso2.emm.agent.proxy.utils.Constants.Authenticator.AUTHENTICATOR_IN_USE,
                    EnrollmentService.this, Constants.AUTHENTICATION_REQUEST_CODE);
            authenticator.doAuthenticate();
        } else {
            Log.e(TAG, "Auto enrollment failed due to network issues.");
        }
    }

    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
        if (requestCode == Constants.CONFIGURATION_REQUEST_CODE) {
            Log.i(TAG, "EMM auto enrollment, configuration response received.");
            manipulateConfigurationResponse(result);
        } else if (Constants.REGISTER_REQUEST_CODE == requestCode) {
            Log.i(TAG, "EMM auto enrollment, registration response received.");
            String responseStatus;
            if (result != null) {
                responseStatus = result.get(Constants.STATUS);
                Preference.putString(context, Constants.PreferenceFlag.REG_ID, info.getDeviceId());
                if (Constants.Status.SUCCESSFUL.equals(responseStatus) || Constants.Status.CREATED.equals(responseStatus)) {
                    Log.i(TAG, "EMM auto enrollment, registration successful.");
                    if (Constants.NOTIFIER_GCM.equals(Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE))) {
                        registerGCM();
                    } else {
                        finishEnrollment();
                    }
                } else {
                    Log.e(TAG, "Auto enrollment failed, server is not available.");
                    startEnrollment();
                }
            } else {
                Log.e(TAG, "Auto enrollment failed, server failed to respond to the enrollment request.");
                startEnrollment();
            }
        } else if (requestCode == Constants.GCM_REGISTRATION_ID_SEND_CODE && result != null) {
            String status = result.get(Constants.STATUS_KEY);
            Log.i(TAG, "EMM auto enrollment, enrollment update response received.");
            if (!(Constants.Status.SUCCESSFUL.equals(status) || Constants.Status.ACCEPT.equals(status))) {
                Log.i(TAG, "EMM auto enrollment, failed to update enrollment.");
                Log.e(TAG, "Auto enrollment failed, server failed to respond to the enrollment request.");
                startEnrollment();
            } else {
                finishEnrollment();
            }
        }
    }

    private void finishEnrollment() {
        Log.i(TAG, "EMM auto enrollment, finishing enrollment process.");
        String registrationId = Preference.getString(context, Constants.PreferenceFlag.REG_ID);
        if (registrationId == null || !registrationId.isEmpty()) {
            registrationId = info.getDeviceId();
            Preference.putString(context, Constants.PreferenceFlag.REG_ID, registrationId);
        }
        Preference.putBoolean(context, Constants.PreferenceFlag.REGISTERED, true);
        Preference.putBoolean(context, Constants.PreferenceFlag.DEVICE_ACTIVE, true);
        if (!isDeviceAdminActive()) {
            startDeviceAdminPrompt();
        }
        startEvents();
        startPolling();
    }

    /**
     * Start device admin activation request.
     */
    private void startDeviceAdminPrompt() {
        Intent intent = new Intent(EnrollmentService.this, EnableDeviceAdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private boolean isDeviceAdminActive() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        return devicePolicyManager.isAdminActive(cdmDeviceAdmin);
    }


    private void startEvents() {
        if(!EventRegistry.eventListeningStarted) {
            Log.i(TAG, "EMM auto enrollment, initiating events.");
            EventRegistry registerEvent = new EventRegistry(this);
            registerEvent.register();
        }
    }

    /**
     * Starts server polling task.
     */
    private void startPolling() {
        String notifier = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
        if(Constants.NOTIFIER_LOCAL.equals(notifier)) {
            Log.i(TAG, "EMM auto enrollment, initiating polling task.");
            LocalNotification.startPolling(context);
        }
    }

    @Override
    public void onAuthenticated(boolean authenticated, int requestCode) {
        Log.i(TAG, "EMM auto enrollment, authentication result received.");
        if (requestCode == Constants.AUTHENTICATION_REQUEST_CODE) {
            if (authenticated) {
                Log.i(TAG, "EMM auto enrollment, authentication successful.");
                getConfigurationsFromServer();
            } else {
                Log.e(TAG, "EMM auto enrollment authentication failed, please check your certificates and try again.");
            }
        }
    }

    private void getConfigurationsFromServer() {
        String ipSaved = Constants.DEFAULT_HOST;
        String prefIP = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);
        if (prefIP != null) {
            ipSaved = prefIP;
        }

        if (!ipSaved.isEmpty()) {
            Log.i(TAG, "EMM auto enrollment, retrieving configurations from server.");
            ServerConfig utils = new ServerConfig();
            utils.setServerIP(ipSaved);
            CommonUtils.callSecuredAPI(EnrollmentService.this,
                                       utils.getAPIServerURL(context) + Constants.CONFIGURATION_ENDPOINT,
                                       org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.GET, null, EnrollmentService.this,
                                       Constants.CONFIGURATION_REQUEST_CODE
            );
        } else {
            Log.e(TAG, "There is no valid IP to contact the server");
        }
    }

    /**
     * Manipulates the Configuration response received from server.
     *
     * @param result the result of the configuration request
     */
    private void manipulateConfigurationResponse(Map<String, String> result) {
        String responseStatus;
        if (result != null) {
            responseStatus = result.get(Constants.STATUS);
            if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
                String configurationResponse = result.get(Constants.RESPONSE);

                if (configurationResponse != null) {
                    try {
                        JSONObject config = new JSONObject(configurationResponse);
                        if (!config.isNull(context.getString(R.string.shared_pref_configuration))) {
                            JSONArray configList = new JSONArray(config.getString(context.getString(R.string.
                                                                                            shared_pref_configuration)));
                            for (int i = 0; i < configList.length(); i++) {
                                JSONObject param = new JSONObject(configList.get(i).toString());
                                if(param.getString(context.getString(R.string.shared_pref_config_key)).trim().equals(
                                        Constants.PreferenceFlag.NOTIFIER_TYPE)){
                                    String type = param.getString(context.getString(R.string.shared_pref_config_value)).trim();
                                    if(type.equals(String.valueOf(Constants.NOTIFIER_CHECK))) {
                                        Preference.putString(context, Constants.PreferenceFlag.NOTIFIER_TYPE,
                                                             Constants.NOTIFIER_GCM);
                                    }else{
                                        Preference.putString(context, Constants.PreferenceFlag.NOTIFIER_TYPE,
                                                             Constants.NOTIFIER_LOCAL);
                                    }
                                } else if(param.getString(context.getString(R.string.shared_pref_config_key)).trim().
                                        equals(context.getString(R.string.shared_pref_frequency)) && !param.getString(
                                        context.getString(R.string.shared_pref_config_value)).trim().isEmpty()){
                                    Preference.putInt(context, getResources().getString(R.string.shared_pref_frequency),
                                                      Integer.valueOf(param.getString(context.getString(
                                                              R.string.shared_pref_config_value)).trim()));
                                } else if(param.getString(context.getString(R.string.shared_pref_config_key)).trim().
                                        equals(context.getString(R.string.shared_pref_gcm))){
                                    Preference.putString(context, getResources().getString(R.string.shared_pref_sender_id),
                                                         param.getString(context.getString(R.string.shared_pref_config_value)).trim());
                                }
                            }
                            String notifierType = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
                            if (notifierType == null || notifierType.isEmpty()) {
                                setDefaultNotifier();
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing configuration response JSON", e);
                        setDefaultNotifier();
                    }
                } else {
                    Log.e(TAG, "Empty configuration response");
                    setDefaultNotifier();
                }
            } else if (Constants.Status.UNAUTHORIZED.equals(responseStatus)) {
                String response = result.get(Constants.RESPONSE);
                Log.e(TAG, "Unauthorized :" + response);
            } else if (Constants.Status.INTERNAL_SERVER_ERROR.equals(responseStatus)) {
                Log.e(TAG, "Empty configuration response.");
                setDefaultNotifier();
            } else {
                Log.e(TAG, "Empty configuration response.");
                setDefaultNotifier();
            }

        } else {
            Log.e(TAG, "Empty configuration response.");
            setDefaultNotifier();
        }
        deviceInfoBuilder = new DeviceInfoPayload(context);
        DeviceInfo deviceInfo = new DeviceInfo(context);
        String deviceIdentifier = deviceInfo.getDeviceId();
        Preference.putString(context, Constants.PreferenceFlag.REG_ID, deviceIdentifier);
        registerDevice();
    }

    private void setDefaultNotifier(){
        Preference.putString(context, Constants.PreferenceFlag.NOTIFIER_TYPE, Constants.NOTIFIER_LOCAL);
        Preference.putInt(context, getResources().getString(R.string.shared_pref_frequency),
                          Constants.DEFAULT_INTERVAL);
    }

    private void registerDevice() {
        Log.i(TAG, "EMM auto enrollment, registration started.");
        String type = Preference.getString(context,
                                           context.getResources().getString(R.string.shared_pref_reg_type));
        String username = Preference.getString(context,
                                               context.getResources().getString(R.string.username));
        try {
            deviceInfoBuilder.build(type, username);
        } catch (AndroidAgentException e) {
            Log.e(TAG, "Error occurred while building the device info payload.", e);
        }

        // Check network connection availability before calling the API.
        if (CommonUtils.isNetworkAvailable(context)) {
            // Call device registration API.
            String ipSaved = Constants.DEFAULT_HOST;
            String prefIP = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);
            if (prefIP != null) {
                ipSaved = prefIP;
            }
            if (!ipSaved.isEmpty()) {
                ServerConfig utils = new ServerConfig();
                utils.setServerIP(ipSaved);

                CommonUtils.callSecuredAPI(EnrollmentService.this,
                                           utils.getAPIServerURL(context) + Constants.REGISTER_ENDPOINT,
                                           org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.POST,
                                           deviceInfoBuilder.getDeviceInfoPayload(),
                                           EnrollmentService.this,
                                           Constants.REGISTER_REQUEST_CODE);
            } else {
                Log.e(TAG, "There is no valid IP to contact the server");
            }
        } else {
            Log.e(TAG, "Can not process enrollment, network not available.");
            startEnrollment();
        }

    }

    /**
     * This will start the GCM flow by registering the device with Google and sending the
     * registration ID to MDM. This is done in a Async task as a network call may be done, and
     * it should be done out side the UI thread. After retrieving the registration Id, it is send
     * to the MDM server so that it can send notifications to the device.
     */
    private void registerGCM() {
        Log.i(TAG, "EMM auto enrollment, GCM registration initiated.");
        String token =  FirebaseInstanceId.getInstance().getToken();
        if(token != null) {
            Preference.putString(context, Constants.GCM_REG_ID, token);
            try {
                sendRegistrationId();
            } catch (AndroidAgentException e) {
                Log.e(TAG, "Error while sending registration Id");
            }
        } else {
            Log.e(TAG, "Registration Id is not available during auto enrollment.");
            try {
                CommonUtils.clearAppData(context);
            } catch (AndroidAgentException e) {
                Log.e(TAG, "Failed to clear app data", e);
            }
        }
    }

    /**
     * This is used to send the registration Id to MDM server so that the server
     * can use it as a reference to identify the device when sending messages to
     * Google server.
     *
     * @throws AndroidAgentException
     */
    public void sendRegistrationId() throws AndroidAgentException {
        Log.i(TAG, "EMM auto enrollment, GCM ID retrieval successful, updating enrollment");
        DeviceInfo deviceInfo = new DeviceInfo(context);
        DeviceInfoPayload deviceInfoPayload = new DeviceInfoPayload(context);
        deviceInfoPayload.build();

        String replyPayload = deviceInfoPayload.getDeviceInfoPayload();
        String ipSaved = Constants.DEFAULT_HOST;
        String prefIP = Preference.getString(context, Constants.PreferenceFlag.IP);
        if (prefIP != null) {
            ipSaved = prefIP;
        }
        if (!ipSaved.isEmpty()) {
            ServerConfig utils = new ServerConfig();
            utils.setServerIP(ipSaved);

            String url = utils.getAPIServerURL(context) + Constants.DEVICE_ENDPOINT + deviceInfo.getDeviceId();

            CommonUtils.callSecuredAPI(context, url, org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.PUT,
                                       replyPayload, EnrollmentService.this, Constants.GCM_REGISTRATION_ID_SEND_CODE);
        } else {
            Log.e(TAG, "There is no valid IP to contact the server");
        }
    }

}
