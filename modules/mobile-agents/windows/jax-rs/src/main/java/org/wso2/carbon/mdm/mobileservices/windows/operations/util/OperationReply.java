/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlMessageFormatException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ItemTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.AddTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.TargetTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.MetaTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ReplaceTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.DeleteTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.AtomicTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SequenceTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.StatusTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ExecuteTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.GetTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SourceTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlBody;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlHeader;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.operations.WindowsOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.operations.CredentialTag;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.PasscodePolicy;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Wifi;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;
import static org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationCode.*;

/**
 * Used to generate a reply to a receiving syncml from a device.
 */
public class OperationReply {

    private SyncmlDocument syncmlDocument;
    private SyncmlDocument replySyncmlDocument;
    private int headerCommandId = 1;
    private static final int HEADER_STATUS_ID = 0;
    private static final String RESULTS_COMMAND_TEXT = "Results";
    private static final String HEADER_COMMAND_TEXT = "SyncHdr";
    private static final String ALERT_COMMAND_TEXT = "Alert";
    private static final String REPLACE_COMMAND_TEXT = "Replace";
    private static final String GET_COMMAND_TEXT = "Get";
    private static final String EXEC_COMMAND_TEXT = "Exec";
    private List<? extends Operation> operations;
    Gson gson = new Gson();

    public OperationReply(SyncmlDocument syncmlDocument, List<? extends Operation> operations) {
        this.syncmlDocument = syncmlDocument;
        replySyncmlDocument = new SyncmlDocument();
        this.operations = operations;
    }

    public OperationReply(SyncmlDocument syncmlDocument) {
        this.syncmlDocument = syncmlDocument;
        replySyncmlDocument = new SyncmlDocument();
    }

    public SyncmlDocument generateReply() throws SyncmlMessageFormatException, SyncmlOperationException {
        generateHeader();
        generateBody();
        return replySyncmlDocument;
    }

    private void generateHeader() throws SyncmlMessageFormatException {
        String nextNonceValue = Constants.INITIAL_NONCE;
        SyncmlHeader sourceHeader = syncmlDocument.getHeader();
        SyncmlHeader header = new SyncmlHeader();
        header.setMsgID(sourceHeader.getMsgID());
        header.setHexadecimalSessionId(Integer.toHexString(sourceHeader.getSessionId()));
        TargetTag target = new TargetTag();
        target.setLocURI(sourceHeader.getSource().getLocURI());
        header.setTarget(target);

        SourceTag source = new SourceTag();
        source.setLocURI(sourceHeader.getTarget().getLocURI());
        header.setSource(source);

        CredentialTag cred = new CredentialTag();
        if (sourceHeader.getCredential() == null) {
            MetaTag meta = new MetaTag();
            meta.setFormat(Constants.CRED_FORMAT);
            meta.setType(Constants.CRED_TYPE);
            cred.setMeta(meta);
        } else {
            cred.setMeta(sourceHeader.getCredential().getMeta());
        }
        SyncmlBody sourcebody = syncmlDocument.getBody();
        List<StatusTag> statuses = sourcebody.getStatus();

        for (StatusTag status : statuses) {
            if (HEADER_COMMAND_TEXT.equals(status.getCommand()) &&
                    status.getChallenge() != null) {
                nextNonceValue = status.getChallenge().getMeta().getNextNonce();
            }
        }
        cred.setData(SyncmlCredentialUtil.generateCredData(nextNonceValue));
        header.setCredential(cred);

        replySyncmlDocument.setHeader(header);
    }

    private void generateBody() throws SyncmlMessageFormatException, SyncmlOperationException {
        SyncmlBody syncmlBody = generateStatuses();
        try {
            appendOperations(syncmlBody);
        } catch (PolicyManagementException e) {
            throw new SyncmlOperationException("Error occurred while retrieving policy operations.", e);
        } catch (FeatureManagementException e) {
            throw new SyncmlOperationException("Error occurred while retrieving effective policy operations.", e);
        } catch (JSONException e) {
            throw new SyncmlMessageFormatException("Error Occurred while parsing operation object.", e);
        }
        replySyncmlDocument.setBody(syncmlBody);
    }

    private SyncmlBody generateStatuses() {
        SyncmlBody sourceSyncmlBody = syncmlDocument.getBody();
        SyncmlHeader sourceHeader = syncmlDocument.getHeader();
        StatusTag headerStatus;
        SyncmlBody syncmlBodyReply = new SyncmlBody();
        List<StatusTag> statuses = new ArrayList<>();
        List<StatusTag> sourceStatuses = sourceSyncmlBody.getStatus();
        if (sourceStatuses.isEmpty()) {
            headerStatus =
                    new StatusTag(headerCommandId, sourceHeader.getMsgID(), HEADER_STATUS_ID,
                            HEADER_COMMAND_TEXT, sourceHeader.getSource().getLocURI(),
                            String.valueOf(Constants.SyncMLResponseCodes.AUTHENTICATION_ACCEPTED));
            statuses.add(headerStatus);
        } else {
            for (StatusTag sourceStatus : sourceStatuses) {
                if (sourceStatus.getChallenge() != null && HEADER_COMMAND_TEXT.equals(sourceStatus.getCommand())) {

                    headerStatus =
                            new StatusTag(headerCommandId, sourceHeader.getMsgID(), HEADER_STATUS_ID,
                                    HEADER_COMMAND_TEXT, sourceHeader.getSource().getLocURI(),
                                    String.valueOf(Constants.SyncMLResponseCodes.AUTHENTICATION_ACCEPTED));
                    statuses.add(headerStatus);
                }
            }
        }
        if (sourceSyncmlBody.getResults() != null) {
            int ResultCommandId = ++headerCommandId;
            StatusTag resultStatus = new StatusTag(ResultCommandId, sourceHeader.getMsgID(),
                    sourceSyncmlBody.getResults().getCommandId(), RESULTS_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
            statuses.add(resultStatus);
        }
        if (sourceSyncmlBody.getAlert() != null) {
            int alertCommandId = ++headerCommandId;
            StatusTag alertStatus = new StatusTag(alertCommandId,
                    sourceHeader.getMsgID(),
                    sourceSyncmlBody.getAlert().getCommandId(),
                    ALERT_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
            statuses.add(alertStatus);
        }
        if (sourceSyncmlBody.getReplace() != null) {
            int replaceCommandId = ++headerCommandId;
            StatusTag replaceStatus = new StatusTag(replaceCommandId, sourceHeader.getMsgID(),
                    sourceSyncmlBody.getReplace().getCommandId(), REPLACE_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED)
            );
            statuses.add(replaceStatus);
        }
        if (sourceSyncmlBody.getExec() != null) {
            List<ExecuteTag> Executes = sourceSyncmlBody.getExec();
            for (ExecuteTag exec : Executes) {
                int execCommandId = ++headerCommandId;
                StatusTag execStatus = new StatusTag(execCommandId, sourceHeader.getMsgID(),
                        exec.getCommandId(), EXEC_COMMAND_TEXT, null, String.valueOf(
                        Constants.SyncMLResponseCodes.ACCEPTED));
                statuses.add(execStatus);
            }
        }
        if (sourceSyncmlBody.getGet() != null) {
            int getCommandId = ++headerCommandId;
            StatusTag execStatus = new StatusTag(getCommandId, sourceHeader.getMsgID(), sourceSyncmlBody
                    .getGet().getCommandId(), GET_COMMAND_TEXT, null, String.valueOf(
                    Constants.SyncMLResponseCodes.ACCEPTED));
            statuses.add(execStatus);
        }
        syncmlBodyReply.setStatus(statuses);
        return syncmlBodyReply;
    }

    private void appendOperations(SyncmlBody syncmlBody) throws PolicyManagementException,
            FeatureManagementException, JSONException, SyncmlOperationException {
        GetTag getElement = new GetTag();
        List<ItemTag> getElements = new ArrayList<>();
        List<ExecuteTag> executeElements = new ArrayList<>();
        AtomicTag atomicTagElement = new AtomicTag();
        List<AddTag> addElements = new ArrayList<>();
        ReplaceTag replaceElement = new ReplaceTag();
        List<ItemTag> replaceItems = new ArrayList<>();
        SequenceTag monitorSequence = new SequenceTag();

        if (operations != null) {
            for (Operation operation : operations) {
                Operation.Type type = operation.getType();
                switch (type) {
                    case POLICY:
                        if (this.syncmlDocument.getBody().getAlert() != null) {
                            if ((Constants.INITIAL_ALERT_DATA.equals(this.syncmlDocument.getBody()
                                    .getAlert().getData()))) {
                                SequenceTag policySequence = new SequenceTag();
                                policySequence = buildSequence(operation, policySequence);
                                syncmlBody.setSequence(policySequence);
                            }
                        }
                        break;
                    case CONFIG:
                        List<AddTag> addConfigurations = appendAddConfiguration(operation);
                        for (AddTag addConfiguration : addConfigurations) {
                            addElements.add(addConfiguration);
                        }
                        break;
                    case MESSAGE:

                        break;
                    case INFO:
                        ItemTag itemGet = appendGetInfo(operation);
                        getElements.add(itemGet);
                        break;
                    case COMMAND:
                        ExecuteTag execElement;
                        if ((PluginConstants.OperationCodes.DEVICE_LOCK.equals(operation.getCode()))) {
                            execElement = executeCommand(operation);
                            executeElements.add(execElement);
                        }
                        if ((PluginConstants.OperationCodes.DEVICE_RING.equals(operation.getCode()))) {
                            execElement = executeCommand(operation);
                            executeElements.add(execElement);
                        }
                        if ((PluginConstants.OperationCodes.DISENROLL.equals(operation.getCode()))) {
                            execElement = executeCommand(operation);
                            executeElements.add(execElement);
                        }
                        if ((PluginConstants.OperationCodes.WIPE_DATA.equals(operation.getCode()))) {
                            execElement = executeCommand(operation);
                            executeElements.add(execElement);
                        }
                        if ((PluginConstants.OperationCodes.LOCK_RESET.equals(operation.getCode()))) {
                            SequenceTag sequenceElement = new SequenceTag();
                            SequenceTag sequence = buildSequence(operation, sequenceElement);
                            syncmlBody.setSequence(sequence);
                        }
                        if ((PluginConstants.OperationCodes.MONITOR.equals(operation.getCode()))) {
                            GetTag monitorGetElement = new GetTag();
                            List<ItemTag> monitorItems;
                            List<ProfileFeature> profileFeatures;

                            if (this.syncmlDocument.getBody().getAlert() != null) {
                                if (Constants.INITIAL_ALERT_DATA.equals(this.syncmlDocument.getBody().
                                        getAlert().getData())) {

                                    monitorSequence.setCommandId(operation.getId());
                                    DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                                            syncmlDocument.getHeader().getSource().getLocURI());
                                    try {
                                        profileFeatures = WindowsAPIUtils.getPolicyManagerService().
                                                getEffectiveFeatures(deviceIdentifier);
                                    } catch (FeatureManagementException e) {
                                        throw new SyncmlOperationException("Error in getting effective policy.", e);
                                    }
                                    monitorItems = buildMonitorOperation(profileFeatures);
                                    if (!monitorItems.isEmpty()) {
                                        monitorGetElement.setCommandId(operation.getId());
                                        monitorGetElement.setItems(monitorItems);
                                    }
                                    monitorSequence.setGet(monitorGetElement);
                                    syncmlBody.setSequence(monitorSequence);
                                }
                            }
                        }
                        break;
                }
            }
        }
        if (!replaceItems.isEmpty()) {
            replaceElement.setCommandId(Constants.SyncmlMessageCodes.replaceCommandId);
            replaceElement.setItems(replaceItems);
        }
        if (!getElements.isEmpty()) {
            getElement.setCommandId(Constants.SyncmlMessageCodes.elementCommandId);
            getElement.setItems(getElements);
        }
        if (!addElements.isEmpty()) {
            atomicTagElement.setCommandId(Constants.SyncmlMessageCodes.atomicCommandId);
            atomicTagElement.setAdds(addElements);
        }
        syncmlBody.setGet(getElement);
        syncmlBody.setExec(executeElements);
        syncmlBody.setAtomicTag(atomicTagElement);
        syncmlBody.setReplace(replaceElement);
    }

    private ItemTag appendExecInfo(Operation operation) {
        ItemTag item = new ItemTag();
        String operationCode = operation.getCode();
        for (Command command : Command.values()) {
            if (operationCode != null && operationCode.equals(command.name())) {
                TargetTag target = new TargetTag();
                target.setLocURI(command.getCode());
                if ((PluginConstants
                        .OperationCodes.DISENROLL.equals(operation.getCode()))) {
                    MetaTag meta = new MetaTag();
                    meta.setFormat(Constants.META_FORMAT_CHARACTER);
                    item.setMeta(meta);
                    item.setData(Constants.PROVIDER_ID);
                }
                item.setTarget(target);
            }
        }
        return item;
    }

    private ItemTag appendGetInfo(Operation operation) {
        ItemTag item = new ItemTag();
        String operationCode = operation.getCode();
        for (Info info : Info.values()) {
            if (operationCode != null && operationCode.equals(info.name())) {
                TargetTag target = new TargetTag();
                target.setLocURI(info.getCode());
                item.setTarget(target);
            }
        }
        if ((operationCode != null) &&
                PluginConstants.OperationCodes.LOCK_RESET.equals(operationCode)) {
            operation.setCode(PluginConstants.OperationCodes.PIN_CODE);
            for (Info getInfo : Info.values()) {
                if (operation.getCode().equals(getInfo.name())) {
                    TargetTag target = new TargetTag();
                    target.setLocURI(getInfo.getCode());
                    item.setTarget(target);
                }
            }
        }
        return item;
    }

    private ItemTag appendReplaceInfo(Operation operation) throws JSONException {
        String policyAllowData = "1";
        String policyDisallowData = "0";
        ItemTag item = new ItemTag();
        TargetTag target = new TargetTag();
        String operationCode = operation.getCode();
        JSONObject payload = new JSONObject(operation.getPayLoad().toString());
        for (Command command : Command.values()) {

            if (operationCode != null && operationCode.equals(command.name())) {
                target.setLocURI(command.getCode());

                if ((PluginConstants.OperationCodes.CAMERA.equals(operation.getCode()))) {

                    if (payload.getBoolean("enabled")) {
                        MetaTag meta = new MetaTag();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyAllowData);
                    } else {
                        MetaTag meta = new MetaTag();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyDisallowData);
                    }
                }
                if ((PluginConstants.OperationCodes.ENCRYPT_STORAGE.
                        equals(operation.getCode()))) {

                    if (payload.getBoolean("encrypted")) {
                        MetaTag meta = new MetaTag();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyAllowData);
                    } else {
                        MetaTag meta = new MetaTag();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyDisallowData);
                    }
                }
            }
        }
        return item;
    }

    private List<AddTag> appendAddInfo(Operation operation) throws WindowsOperationException {

        List<AddTag> addList = new ArrayList<>();
        Gson gson = new Gson();

        if ((PluginConstants.OperationCodes.PASSCODE_POLICY.equals(operation.getCode()))) {

            PasscodePolicy passcodeObject = gson.fromJson((String) operation.getPayLoad(), PasscodePolicy.class);

            for (Configure configure : Configure.values()) {

                if (operation.getCode() != null && PluginConstants.OperationCodes.PASSWORD_MAX_FAIL_ATTEMPTS.
                        equals(configure.name())) {
                    AddTag add = generatePasscodePolicyData(configure, passcodeObject.getMaxFailedAttempts());
                    addList.add(add);
                }
                if (operation.getCode() != null && (PluginConstants.OperationCodes.DEVICE_PASSWORD_ENABLE.
                        equals(configure.name()) || PluginConstants.OperationCodes.SIMPLE_PASSWORD.
                        equals(configure.name()) || PluginConstants.OperationCodes.ALPHANUMERIC_PASSWORD.
                        equals(configure.name()))) {
                    AddTag add = generatePasscodeBooleanData(operation, configure);
                    addList.add(add);
                }
                if (operation.getCode() != null && PluginConstants.OperationCodes.MIN_PASSWORD_LENGTH.
                        equals(configure.name())) {
                    AddTag add = generatePasscodePolicyData(configure, passcodeObject.getMinLength());
                    addList.add(add);
                }
                if (operation.getCode() != null && PluginConstants.OperationCodes.PASSWORD_EXPIRE.
                        equals(configure.name())) {
                    AddTag add = generatePasscodePolicyData(configure, passcodeObject.getMaxPINAgeInDays());
                    addList.add(add);
                }
                if (operation.getCode() != null && PluginConstants.OperationCodes.PASSWORD_HISTORY.
                        equals(configure.name())) {
                    int pinHistory = passcodeObject.getPinHistory();
                    AddTag add = generatePasscodePolicyData(configure, pinHistory);
                    addList.add(add);
                }
                if (operation.getCode() != null && PluginConstants.OperationCodes.MAX_PASSWORD_INACTIVE_TIME.
                        equals(configure.name())) {
                    AddTag add = generatePasscodePolicyData(configure, passcodeObject.getMaxInactiveTime());
                    addList.add(add);
                }
                if (operation.getCode() != null && PluginConstants.OperationCodes.MIN_PASSWORD_COMPLEX_CHARACTERS.
                        equals(configure.name())) {
                    int complexChars = passcodeObject.getMinComplexChars();
                    AddTag add = generatePasscodePolicyData(configure, complexChars);
                    addList.add(add);
                }
            }
        }
        return addList;
    }

    private List<AddTag> appendAddConfiguration(Operation operation) {

        List<AddTag> addList = new ArrayList<>();
        Gson gson = new Gson();

        if (SyncmlCommandType.WIFI.getValue().equals(operation.getCode())) {
            AddTag add = new AddTag();
            String operationCode = operation.getCode();
            Wifi wifiObject = gson.fromJson((String) operation.getPayLoad(), Wifi.class);
            String data = "&lt;?xml version=&quot;1.0&quot;?&gt;&lt;WLANProfile" +
                    "xmlns=&quot;http://www.microsoft.com/networking/WLAN/profile/v1&quot;&gt;&lt;name&gt;" +
                    wifiObject.getNetworkName() + "&lt;/name&gt;&lt;SSIDConfig&gt;&lt;SSID&gt;&lt;name&gt;" +
                    wifiObject.getSsid() + "&lt;/name&gt;&lt;/SSID&gt;&lt;/SSIDConfig&gt;&lt;connectionType&gt;" +
                    wifiObject.getConnectionType() + "&lt;/connectionType&gt;&lt;connectionMode&gt;" +
                    wifiObject.getConnectionMode() + "&lt;/connectionMode&gt;&lt;MSM&gt;&lt;security&gt;&lt;" +
                    "authEncryption&gt;&lt;authentication&gt;" + wifiObject.getAuthentication() +
                    "&lt;/authentication&gt;&lt;encryption&gt;" + wifiObject.getEncryption() +
                    "&lt;/encryption&gt;&lt;/authEncryption&gt;&lt;sharedKey&gt;&lt;keyType&gt;" +
                    wifiObject.getKeyType() + "&lt;/keyType&gt;&lt;protected&gt;" + wifiObject.getProtection() +
                    "&lt;/protected&gt;&lt;keyMaterial&gt;" + wifiObject.getKeyMaterial() +
                    "&lt;/keyMaterial&gt;&lt;/sharedKey&gt;&lt;/security&gt;&lt;/MSM&gt;&lt;/WLANProfile&gt;";

            MetaTag meta = new MetaTag();
            meta.setFormat(Constants.META_FORMAT_CHARACTER);
            List<ItemTag> items = new ArrayList<>();

            for (Configure configure : Configure.values()) {
                if (operationCode != null && operationCode.equals(configure.name())) {
                    TargetTag target = new TargetTag();
                    target.setLocURI(configure.getCode());
                    items.get(0).setTarget(target);
                }
            }
            items.get(0).setMeta(meta);
            items.get(0).setData(data);

            add.setCommandId(301);
            add.setItems(items);
            addList.add(add);
            return addList;
        }
        return null;
    }

    public ExecuteTag executeCommand(Operation operation) {
        ExecuteTag execElement = new ExecuteTag();
        execElement.setCommandId(operation.getId());
        List<ItemTag> itemsExec = new ArrayList<>();
        ItemTag itemExec = appendExecInfo(operation);
        itemsExec.add(itemExec);
        execElement.setItems(itemsExec);
        return execElement;
    }

    public SequenceTag buildSequence(Operation operation, SequenceTag sequenceElement) throws
            JSONException, SyncmlOperationException {

        sequenceElement.setCommandId(operation.getId());
        List<ReplaceTag> replaceItems = new ArrayList<>();

        if ((PluginConstants.OperationCodes.LOCK_RESET.equals(operation.getCode()))) {
            ExecuteTag execElement = executeCommand(operation);
            GetTag getElements = new GetTag();
            getElements.setCommandId(operation.getId());
            List<ItemTag> getItems = new ArrayList<>();
            ItemTag itemGets = appendGetInfo(operation);
            getItems.add(itemGets);
            getElements.setItems(getItems);

            sequenceElement.setExec(execElement);
            sequenceElement.setGet(getElements);
            return sequenceElement;

        } else if ((PluginConstants.OperationCodes.POLICY_BUNDLE.equals(operation.getCode()))) {
            List<? extends Operation> policyOperations;
            try {
                policyOperations = (List<? extends Operation>) operation.getPayLoad();
            } catch (ClassCastException e) {
                throw new ClassCastException();
            }
            for (Operation policy : policyOperations) {

                if (PluginConstants.OperationCodes.CAMERA.equals(policy.getCode())) {
                    ReplaceTag replaceCameraConfig = new ReplaceTag();
                    ItemTag cameraItem;
                    List<ItemTag> cameraItems = new ArrayList<>();

                    try {
                        cameraItem = appendReplaceInfo(policy);
                        cameraItems.add(cameraItem);
                    } catch (JSONException e) {
                        throw new SyncmlOperationException("Error occurred while parsing payload object to json.", e);
                    }
                    replaceCameraConfig.setCommandId(operation.getId());
                    replaceCameraConfig.setItems(cameraItems);
                    replaceItems.add(replaceCameraConfig);
                }
                if ((PluginConstants.OperationCodes.ENCRYPT_STORAGE.equals(policy.getCode()))) {

                    ReplaceTag replaceStorageConfig = new ReplaceTag();
                    ItemTag storageItem;
                    List<ItemTag> storageItems = new ArrayList<>();
                    try {
                        storageItem = appendReplaceInfo(policy);
                        storageItems.add(storageItem);
                    } catch (JSONException e) {
                        throw new SyncmlOperationException("Error occurred while parsing payload object to json.", e);
                    }
                    replaceStorageConfig.setCommandId(operation.getId());
                    replaceStorageConfig.setItems(storageItems);
                    replaceItems.add(replaceStorageConfig);

                }
                if ((PluginConstants.OperationCodes.PASSCODE_POLICY.equals(policy.getCode()))) {
                    AtomicTag atomicTagElement = new AtomicTag();
                    List<AddTag> addConfig;
                    DeleteTag deleteTag = new DeleteTag();
                    try {
                        addConfig = appendAddInfo(policy);
                        atomicTagElement.setAdds(addConfig);
                        atomicTagElement.setCommandId(operation.getId());
                        List<ItemTag> deleteTagItems = buildDeletePasscodeData(policy);
                        deleteTag.setCommandId(operation.getId());
                        deleteTag.setItems(deleteTagItems);
                        sequenceElement.setDeleteTag(deleteTag);
                        sequenceElement.setAtomicTag(atomicTagElement);
                    } catch (WindowsOperationException e) {
                        throw new SyncmlOperationException("Error occurred while generating operation payload.", e);
                    }
                }
            }
            if (!replaceItems.isEmpty()) {
                sequenceElement.setReplaces(replaceItems);
            }
            return sequenceElement;

        } else {
            return null;
        }
    }

    public List<ItemTag> buildMonitorOperation(List<ProfileFeature> effectiveMonitoringFeature) {
        List<ItemTag> monitorItems = new ArrayList<>();
        Operation monitorOperation;
        for (ProfileFeature profileFeature : effectiveMonitoringFeature) {

            if ((PluginConstants.OperationCodes.CAMERA.equals
                    (profileFeature.getFeatureCode()))) {
                String cameraStatus = PluginConstants
                        .OperationCodes.CAMERA_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(cameraStatus);
                ItemTag item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
            if (PluginConstants.OperationCodes.ENCRYPT_STORAGE.equals
                    (profileFeature.getFeatureCode())) {
                String encryptStorageStatus = PluginConstants
                        .OperationCodes.ENCRYPT_STORAGE_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(encryptStorageStatus);
                ItemTag item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
            if ((PluginConstants.OperationCodes.PASSCODE_POLICY.equals
                    (profileFeature.getFeatureCode()))) {
                String passcodeStatus = PluginConstants
                        .OperationCodes.DEVICE_PASSWORD_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(passcodeStatus);
                ItemTag item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
        }
        return monitorItems;
    }


    public List<ItemTag> buildDeletePasscodeData(Operation operation) {
        List<ItemTag> deleteTagItems = new ArrayList<>();
        ItemTag itemTag = new ItemTag();
        TargetTag target = new TargetTag();
        if ((PluginConstants.OperationCodes.PASSCODE_POLICY.equals(operation.getCode()))) {
            operation.setCode(PluginConstants.OperationCodes.DEVICE_PASSCODE_DELETE);
            for (Command command : Command.values()) {
                if (operation.getCode() != null && operation.getCode().equals(command.name())) {
                    target.setLocURI(command.getCode());
                    itemTag.setTarget(target);
                    deleteTagItems.add(itemTag);
                }

            }
        }
        return deleteTagItems;
    }

    public AddTag generatePasscodePolicyData(Configure configure, int policyData) {
        String attempt = String.valueOf(policyData);
        AddTag add = new AddTag();
        List<ItemTag> itemList = new ArrayList<>();
        ItemTag item = new ItemTag();
        TargetTag target = new TargetTag();
        target.setLocURI(configure.getCode());
        MetaTag meta = new MetaTag();
        meta.setFormat(Constants.META_FORMAT_INT);
        item.setTarget(target);
        item.setMeta(meta);
        item.setData(attempt);
        itemList.add(item);
        add.setCommandId(Constants.SyncmlMessageCodes.addCommandId);
        add.setItems(itemList);
        return add;
    }

    public AddTag generatePasscodeBooleanData(Operation operation, Configure configure) {
        TargetTag target = new TargetTag();
        MetaTag meta = new MetaTag();
        AddTag addTag = null;

        PasscodePolicy passcodePolicy = gson.fromJson((String) operation.getPayLoad(), PasscodePolicy.class);
        if (operation.getCode() != null && (PluginConstants.OperationCodes.DEVICE_PASSWORD_ENABLE.
                equals(configure.name()))) {
            if (passcodePolicy.isEnablePassword()) {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.NEGATIVE_CSP_DATA);
            } else {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.POSITIVE_CSP_DATA);
            }
        }
        if (PluginConstants.OperationCodes.ALPHANUMERIC_PASSWORD.
                equals(configure.name())) {
            if (passcodePolicy.isRequireAlphanumeric()) {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.POSITIVE_CSP_DATA);
            } else {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.NEGATIVE_CSP_DATA);
            }
        }
        if (PluginConstants.OperationCodes.SIMPLE_PASSWORD.
                equals(configure.name())) {
            if (passcodePolicy.isAllowSimple()) {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.POSITIVE_CSP_DATA);
            } else {
                target.setLocURI(configure.getCode());
                meta.setFormat(Constants.META_FORMAT_INT);
                addTag = TagUtil.buildAddTag(operation, Constants.SyncMLResponseCodes.NEGATIVE_CSP_DATA);
            }
        }
        return addTag;
    }

}
