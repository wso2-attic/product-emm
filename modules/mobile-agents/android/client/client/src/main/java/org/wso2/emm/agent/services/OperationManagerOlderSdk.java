package org.wso2.emm.agent.services;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.ServerDetails;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;

import java.util.Map;

public class OperationManagerOlderSdk extends OperationManager{
    Context context;
    private static final String TAG = "OperationManagerOldSdk";

    public OperationManagerOlderSdk(Context context){
        super(context);
        this.context = context;

    }
    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {

    }

    @Override
    public void getLocationInfo(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void getApplicationList(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void lockDevice(Operation operation) {

    }

    @Override
    public void wipeDevice(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void clearPassword(Operation operation) {

    }

    @Override
    public void displayNotification(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void configureWifi(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void disableCamera(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void installAppBundle(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void uninstallApplication(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void encryptStorage(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void ringDevice(Operation operation) {

    }

    @Override
    public void muteDevice(Operation operation) {

    }

    @Override
    public void manageWebClip(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void setPasswordPolicy(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void installGooglePlayApp(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void triggerGooglePlayApp(String packageName) {

    }

    @Override
    public void changeLockCode(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void setPolicyBundle(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void monitorPolicy(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void revokePolicy(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void enterpriseWipe(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getResultBuilder().build(operation);

        CommonUtils.disableAdmin(context);

        Intent intent = new Intent(context, ServerDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Started enterprise wipe");
        }

    }

    @Override
    public void blacklistApps(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void disenrollDevice(Operation operation) {

    }

    @Override
    public void upgradeFirmware(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void rebootDevice(Operation operation) throws AndroidAgentException {

    }

    @Override
    public void executeShellCommand(Operation operation) throws AndroidAgentException {

    }
}
