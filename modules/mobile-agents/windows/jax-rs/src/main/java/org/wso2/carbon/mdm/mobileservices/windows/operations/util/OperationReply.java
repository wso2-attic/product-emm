/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.common.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.PasscodePolicy;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Wifi;
import org.wso2.carbon.policy.mgt.common.FeatureManagementException;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;
import static org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationCode.*;

/**
 * Used to generate a reply to a receiving syncml from a device.
 */
public class OperationReply {

    private static Log log = LogFactory.getLog(OperationReply.class);

    private SyncmlDocument syncmlDocument;
    private SyncmlDocument replySyncmlDocument;
    private int headerCommandId = 1;
    private String policyAllowData = "1";
    private String policyDisallowData = "0";
    private static final int HEADER_STATUS_ID = 0;
    private static final String RESULTS_COMMAND_TEXT = "Results";
    private static final String HEADER_COMMAND_TEXT = "SyncHdr";
    private static final String ALERT_COMMAND_TEXT = "Alert";
    private static final String REPLACE_COMMAND_TEXT = "Replace";
    private static final String GET_COMMAND_TEXT = "Get";
    private static final String EXEC_COMMAND_TEXT = "Exec";
    private List<? extends Operation> operations;

    public OperationReply(SyncmlDocument syncmlDocument, List<? extends Operation> operations) {
        this.syncmlDocument = syncmlDocument;
        replySyncmlDocument = new SyncmlDocument();
        this.operations = operations;
    }

    public OperationReply(SyncmlDocument syncmlDocument) {
        this.syncmlDocument = syncmlDocument;
        replySyncmlDocument = new SyncmlDocument();
    }

    public SyncmlDocument generateReply() throws WindowsOperationException {
        generateHeader();
        generateBody();
        return replySyncmlDocument;
    }

    private void generateHeader() {
        String nextnonceValue = Constants.INITIAL_NONCE;
        SyncmlHeader sourceHeader = syncmlDocument.getHeader();
        SyncmlHeader header = new SyncmlHeader();
        header.setMsgID(sourceHeader.getMsgID());
        header.setHexadecimalSessionId(Integer.toHexString(sourceHeader.getSessionId()));
        Target target = new Target();
        target.setLocURI(sourceHeader.getSource().getLocURI());
        header.setTarget(target);

        Source source = new Source();
        source.setLocURI(sourceHeader.getTarget().getLocURI());
        header.setSource(source);

        Credential cred = new Credential();
        if (sourceHeader.getCredential() == null) {
            Meta meta = new Meta();
            meta.setFormat(Constants.CRED_FORMAT);
            meta.setType(Constants.CRED_TYPE);
            cred.setMeta(meta);
        } else {
            cred.setMeta(sourceHeader.getCredential().getMeta());
        }
        SyncmlBody sourcebody = syncmlDocument.getBody();
        List<Status> ststusList = sourcebody.getStatus();

        for (int i = 0; i < ststusList.size(); i++) {
            if (HEADER_COMMAND_TEXT.equals(ststusList.get(i).getCommand()) &&
                    ststusList.get(i).getChallenge() != null) {
                nextnonceValue = ststusList.get(i).getChallenge().getMeta().getNextNonce();
            }
        }
        cred.setData(new SyncmlCredinitials().generateCredData(nextnonceValue));
        header.setCredential(cred);

        replySyncmlDocument.setHeader(header);
    }

    private void generateBody() throws WindowsOperationException {
        SyncmlBody syncmlBody = generateStatuses();
        try {
            appendOperations(syncmlBody);
        } catch (WindowsOperationException e) {
            String message = "Error while generating operation of the syncml message.";
            log.error(message);
            throw new WindowsOperationException(message);
        } catch (PolicyManagementException e) {
            String message = "Error while retrieving policy operations.";
            log.error(message);
        } catch (FeatureManagementException e) {
            String message = "Error while retrieving effective policy operations.";
            log.error(message);
        }
        replySyncmlDocument.setBody(syncmlBody);
    }

    private SyncmlBody generateStatuses() {
        SyncmlBody sourceSyncmlBody = syncmlDocument.getBody();
        SyncmlHeader sourceHeader = syncmlDocument.getHeader();
        Status headerStatus;
        SyncmlBody syncmlBodyReply = new SyncmlBody();
        List<Status> status = new ArrayList<Status>();
        List<Status> sourceStatus = sourceSyncmlBody.getStatus();
        if (sourceStatus.size() == 0) {
            headerStatus =
                    new Status(headerCommandId, sourceHeader.getMsgID(), HEADER_STATUS_ID,
                            HEADER_COMMAND_TEXT, sourceHeader.getSource().getLocURI(),
                            String.valueOf(Constants.SyncMLResponseCodes.AUTHENTICATION_ACCEPTED));
            status.add(headerStatus);
        } else {

            for (int i = 0; i < sourceStatus.size(); i++) {
                Status st = sourceStatus.get(i);
                if (st.getChallenge() != null && HEADER_COMMAND_TEXT.equals(st.getCommand())) {

                    headerStatus =
                            new Status(headerCommandId, sourceHeader.getMsgID(), HEADER_STATUS_ID,
                                    HEADER_COMMAND_TEXT, sourceHeader.getSource().getLocURI(),
                                    String.valueOf(Constants.SyncMLResponseCodes.AUTHENTICATION_ACCEPTED));
                    status.add(headerStatus);
                }
            }
        }
        if (sourceSyncmlBody.getResults() != null) {
            int ResultCommandId = ++headerCommandId;
            Status resultStatus = new Status(ResultCommandId, sourceHeader.getMsgID(),
                    sourceSyncmlBody.getResults().getCommandId(), RESULTS_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
            status.add(resultStatus);
        }
        if (sourceSyncmlBody.getAlert() != null) {
            int alertCommandId = ++headerCommandId;
            Status alertStatus = new Status(alertCommandId,
                    sourceHeader.getMsgID(),
                    sourceSyncmlBody.getAlert().getCommandId(),
                    ALERT_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
            status.add(alertStatus);
        }
        if (sourceSyncmlBody.getReplace() != null) {
            int replaceCommandId = ++headerCommandId;
            Status replaceStatus = new Status(replaceCommandId, sourceHeader.getMsgID(),
                    sourceSyncmlBody.getReplace().getCommandId(), REPLACE_COMMAND_TEXT, null,
                    String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED)
            );
            status.add(replaceStatus);
        }
        if (sourceSyncmlBody.getExec() != null) {
            for (Iterator<Exec> execIterator = sourceSyncmlBody.getExec().iterator(); execIterator.hasNext(); ) {
                int execCommandId = ++headerCommandId;
                Exec exec = execIterator.next();
                Status execStatus = new Status(execCommandId, sourceHeader.getMsgID(),
                        exec.getCommandId(), GET_COMMAND_TEXT, null,
                        String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
                status.add(execStatus);
            }
        }
        if (sourceSyncmlBody.getGet() != null) {
            int getCommandId = ++headerCommandId;
            Status execStatus = new Status(getCommandId, sourceHeader.getMsgID(), sourceSyncmlBody.getGet().getCommandId(),
                    EXEC_COMMAND_TEXT, null, String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
            status.add(execStatus);
        }
        syncmlBodyReply.setStatus(status);
        return syncmlBodyReply;
    }

    private void appendOperations(SyncmlBody syncmlBody) throws WindowsOperationException, PolicyManagementException, FeatureManagementException {
        Get getElement = new Get();
        List<Item> itemsGet = new ArrayList<Item>();
        List<Exec> execList = new ArrayList<>();
        Atomic atomicElement = new Atomic();
        List<Add> addsAtomic = new ArrayList<Add>();
        Replace replaceElement = new Replace();
        List<Item> replaceItem = new ArrayList<>();
        Sequence monitorSequence = new Sequence();

        if (operations != null) {
            for (int x = 0; x < operations.size(); x++) {
                Operation operation = operations.get(x);
                Operation.Type type = operation.getType();
                switch (type) {
                    case POLICY:
                        if (this.syncmlDocument.getBody().getAlert() != null) {
                            if (this.syncmlDocument.getBody().getAlert().getData().equals
                                    (Constants.INITIAL_ALERT_DATA)) {
                                Sequence policySequence = new Sequence();
                                policySequence = buildSequence(operation, policySequence);
                                syncmlBody.setSequence(policySequence);
                            }
                        }
                        break;
                    case CONFIG:
                        List<Add> addConfig = appendAddConfiguration(operation);
                        for (Add addConfiguration : addConfig) {
                            addsAtomic.add(addConfiguration);
                        }
                        break;
                    case MESSAGE:

                        break;
                    case INFO:
                        Item itemGet = appendGetInfo(operation);
                        itemsGet.add(itemGet);
                        break;
                    case COMMAND:
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.DEVICE_LOCK)) {
                            Exec execElement = executeCommand(operation);
                            execList.add(execElement);
                        }
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.DEVICE_RING)) {
                            Exec execElement = executeCommand(operation);
                            execList.add(execElement);
                        }
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.DISENROLL)) {
                            Exec execElement = executeCommand(operation);
                            execList.add(execElement);
                        }
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.WIPE_DATA)) {
                            Exec execElement = executeCommand(operation);
                            execList.add(execElement);
                        }
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.LOCK_RESET)) {
                            Sequence sequenceElement = new Sequence();
                            Sequence sequence = buildSequence(operation, sequenceElement);
                            syncmlBody.setSequence(sequence);
                        }
                        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                                .OperationCodes.MONITOR)) {

                            Get monitorGetElement = new Get();
                            List<Item> monitorItems;
                            List<ProfileFeature> profileFeatures;

                            if (this.syncmlDocument.getBody().getAlert() != null) {
                                if (this.syncmlDocument.getBody().getAlert().getData().equals
                                        (Constants.INITIAL_ALERT_DATA)) {

                                    monitorSequence.setCommandId(operation.getId());
                                    DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                                            syncmlDocument.getHeader().getSource().getLocURI());
                                    try {
                                        profileFeatures = WindowsAPIUtils.getPolicyManagerService().
                                                getEffectiveFeatures(deviceIdentifier);
                                    } catch (FeatureManagementException e) {
                                        throw new FeatureManagementException("Error in getting effective policy.", e);
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
//                    default:
//                        throw new WindowsOperationException("Operation with no type found");
                }
            }
        }
        if (!replaceItem.isEmpty()) {
            replaceElement.setCommandId(300);
            replaceElement.setItems(replaceItem);
        }
        if (!itemsGet.isEmpty()) {
            getElement.setCommandId(75);
            getElement.setItems(itemsGet);
        }
        if (!addsAtomic.isEmpty()) {
            atomicElement.setCommandId(400);
            atomicElement.setAdds(addsAtomic);
        }
        syncmlBody.setGet(getElement);
        syncmlBody.setExec(execList);
        syncmlBody.setAtomic(atomicElement);
        syncmlBody.setReplace(replaceElement);

    }

    private Item appendExecInfo(Operation operation) {
        Item item = new Item();
        String operationCode = operation.getCode();
        for (Command command : Command.values()) {
            if (operationCode != null && operationCode.equals(command.name())) {
                Target target = new Target();
                target.setLocURI(command.getCode());
                if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.DISENROLL)) {
                    Meta meta = new Meta();
                    meta.setFormat("chr");
                    item.setMeta(meta);
                    item.setData(Constants.PROVIDER_ID);
                }
                item.setTarget(target);
            }
        }
        return item;
    }

    private Item appendGetInfo(Operation operation) {
        Item item = new Item();
        String operationCode = operation.getCode();
        for (Info info : Info.values()) {
            if (operationCode != null && operationCode.equals(info.name())) {
                Target target = new Target();
                target.setLocURI(info.getCode());
                item.setTarget(target);
            }
        }
        if (operationCode.equals(
                org.wso2.carbon.mdm.mobileservices.windows.common.Constants.OperationCodes.LOCK_RESET)) {
            operation.setCode(org.wso2.carbon.mdm.mobileservices.windows.common.Constants.OperationCodes.PIN_CODE);
            for (Info getInfo : Info.values()) {
                if (operation.getCode().equals(getInfo.name())) {
                    Target target = new Target();
                    target.setLocURI(getInfo.getCode());
                    item.setTarget(target);
                }
            }
        }
        return item;
    }

    private Item appendReplaceInfo(Operation operation) throws JSONException {
        Item item = new Item();
        Target target = new Target();
        String operationCode = operation.getCode();
        JSONObject payload = new JSONObject(operation.getPayLoad().toString());
        for (Command command : Command.values()) {

            if (operationCode != null && operationCode.equals(command.name())) {
                target.setLocURI(command.getCode());

                if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.CAMERA)) {

                    if (payload.getBoolean("enabled")) {
                        Meta meta = new Meta();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyAllowData);
                    } else {
                        Meta meta = new Meta();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyDisallowData);
                    }
                }
                if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.ENCRYPT_STORAGE)) {

                    if (payload.getBoolean("encrypted")) {
                        Meta meta = new Meta();
                        meta.setFormat(Constants.META_FORMAT_INT);
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData(policyAllowData);
                    } else {
                        Meta meta = new Meta();
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

    private List<Add> appendAddInfo(Operation operation) throws WindowsOperationException {

        List<Add> addList = new ArrayList<>();
        Gson gson = new Gson();

        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                .OperationCodes.PASSCODE_POLICY) && operation.getCode() != null) {
            PasscodePolicy passcodeObject = gson.fromJson((String) operation.getPayLoad(), PasscodePolicy.class);

            for (Configure configure : Configure.values()) {
                String maxAttempt = "PASSWORD_MAX_FAIL_ATTEMPTS";
                if (operation.getCode() != null && maxAttempt.equals(configure.name())) {
                    int maxFailedAttempts = passcodeObject.getMaxFailedAttempts();
//                    String attempt = String.valueOf(maxFailedAttempts);
//                    Add add = new Add();
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(attempt);
//                    itemList.add(item);
//                    add.setCommandId(90);
//                    add.setItems(itemList);
                    Add add = generatePasscodePolicyData(configure, maxFailedAttempts);
                    addList.add(add);
                }
                String allowPassword = "DEVICE_PASSWORD_ENABLE";
                if (operation.getCode() != null && allowPassword.equals(configure.name())) {
                    List<Item> itemList = new ArrayList<>();
                    if (passcodeObject.isEnablePassword()) {
                        Add add = new Add();
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("0");

                        itemList.add(item);
                        add.setCommandId(91);
                        add.setItems(itemList);
                        addList.add(add);
                    } else {
                        Add add = new Add();
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("1");

                        itemList.add(item);
                        add.setCommandId(91);
                        add.setItems(itemList);
                        addList.add(add);
                    }
                }
                String passwordLength = "MIN_PASSWORD_LENGTH";
                if (operation.getCode() != null && passwordLength.equals(configure.name())) {
                    int minLength = passcodeObject.getMinLength();
//                    String attempt = String.valueOf(minLength);
//                    Add add = new Add();
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(attempt);
//
//                    itemList.add(item);
//                    add.setCommandId(92);
//                    add.setItems(itemList);
//                    addList.add(add);
                    Add add = generatePasscodePolicyData(configure, minLength);
                    addList.add(add);
                }
                String allowSimplePassword = "SIMPLE_PASSWORD";
                if (operation.getCode() != null && allowSimplePassword.equals(configure.name())) {
                    List<Item> itemList = new ArrayList<>();
                    if (passcodeObject.isAllowSimple()) {
                        Add add = new Add();
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("1");

                        itemList.add(item);
                        add.setCommandId(93);
                        add.setItems(itemList);
                        addList.add(add);
                    } else {
                        Add add = new Add();
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("0");

                        itemList.add(item);
                        add.setCommandId(93);
                        add.setItems(itemList);
                        addList.add(add);
                    }
                }
                String allowAlfaNumeric = "Alphanumeric_PASSWORD";
                if (operation.getCode() != null && allowAlfaNumeric.equals(configure.name())) {
                    Add add = new Add();
                    List<Item> itemList = new ArrayList<>();
                    if (passcodeObject.isRequireAlphanumeric()) {
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("1");

                        itemList.add(item);
                        add.setCommandId(94);
                        add.setItems(itemList);
                        addList.add(add);
                    } else {
                        Item item = new Item();
                        Target target = new Target();
                        target.setLocURI(configure.getCode());
                        Meta meta = new Meta();
                        meta.setFormat("int");
                        item.setTarget(target);
                        item.setMeta(meta);
                        item.setData("0");

                        itemList.add(item);
                        add.setCommandId(94);
                        add.setItems(itemList);
                        addList.add(add);
                    }
                }
                String passwordExpiteTime = "PASSWORD_EXPIRE";
                if (operation.getCode() != null && passwordExpiteTime.equals(configure.name())) {
                    int minAgeDays = passcodeObject.getMaxPINAgeInDays();
//                    String minAgeString = String.valueOf(minAgeDays);
//                    Add add = new Add();
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(minAgeString);
//
//                    itemList.add(item);
//                    add.setCommandId(95);
//                    add.setItems(itemList);
//                    addList.add(add);
                    Add add = generatePasscodePolicyData(configure, minAgeDays);
                    addList.add(add);

                }
                String devicePasswordHistory = "PASSWORD_HISTORY";
                if (operation.getCode() != null && devicePasswordHistory.equals(configure.name())) {
                    int pinHistory = passcodeObject.getPinHistory();
//                    Add add = new Add();
//                    String pinHistoryString = String.valueOf(pinHistory);
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(pinHistoryString);
//
//                    itemList.add(item);
//                    add.setCommandId(96);
//                    add.setItems(itemList);
//                    addList.add(add);
                    Add add = generatePasscodePolicyData(configure, pinHistory);
                    addList.add(add);
                }
                String pinInactiveTime = "MAX_PASSWORD_INACTIVE_TIME";
                if (operation.getCode() != null && pinInactiveTime.equals(configure.name())) {
                    int pinInactive = passcodeObject.getMaxInactiveTime();
//                    String pinInactiveString = String.valueOf(pinInactive);
//                    Add add = new Add();
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(pinInactiveString);
//
//                    itemList.add(item);
//                    add.setCommandId(97);
//                    add.setItems(itemList);
//                    addList.add(add);
                    Add add = generatePasscodePolicyData(configure, pinInactive);
                    addList.add(add);
                }
                String minpasswordComplexCharacters = "MIN_PASSWORD_COMPLEX_CHARACTERS";
                if (operation.getCode() != null && minpasswordComplexCharacters.equals(configure.name())) {
                    int complexChars = passcodeObject.getMinComplexChars();
//                    String complexCharactersString = String.valueOf(complexChars);
//                    Add add = new Add();
//                    List<Item> itemList = new ArrayList<>();
//                    Item item = new Item();
//                    Target target = new Target();
//                    target.setLocURI(configure.getCode());
//                    Meta meta = new Meta();
//                    meta.setFormat("int");
//                    item.setTarget(target);
//                    item.setMeta(meta);
//                    item.setData(complexCharactersString);
//
//                    itemList.add(item);
//                    add.setCommandId(98);
//                    add.setItems(itemList);
//                    addList.add(add);
                    Add add = generatePasscodePolicyData(configure, complexChars);
                    addList.add(add);
                }

            }

        }
        return addList;
    }

    private List<Add> appendAddConfiguration(Operation operation) throws WindowsOperationException {

        List<Add> addList = new ArrayList<Add>();
        Gson gson = new Gson();

        if (SyncmlCommandType.WIFI.getValue().equals(operation.getCode())) {
            Add add = new Add();
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

            Meta meta = new Meta();
            meta.setFormat("chr");
            List<Item> items = new ArrayList<Item>();

            for (Configure configure : Configure.values()) {
                if (operationCode != null && operationCode.equals(configure.name())) {
                    Target target = new Target();
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

    public Exec executeCommand(Operation operation) {
        Exec execElement = new Exec();
        execElement.setCommandId(operation.getId());
        List<Item> itemsExec = new ArrayList<Item>();
        Item itemExec = appendExecInfo(operation);
        itemsExec.add(itemExec);
        execElement.setItems(itemsExec);
        return execElement;
    }

    public Sequence buildSequence(Operation operation, Sequence sequenceElement) throws WindowsOperationException {

        sequenceElement.setCommandId(operation.getId());
        List<Replace> replaceItems = new ArrayList<>();
        if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                .OperationCodes.LOCK_RESET)) {

            Exec execElement = executeCommand(operation);
            Get getElements = new Get();
            getElements.setCommandId(operation.getId());
            List<Item> getItems = new ArrayList<>();
            Item itemGets = appendGetInfo(operation);
            getItems.add(itemGets);
            getElements.setItems(getItems);

            sequenceElement.setExec(execElement);
            sequenceElement.setGet(getElements);
            return sequenceElement;

        } else if (operation.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                .OperationCodes.POLICY_BUNDLE)) {

            List<? extends Operation> operationList = (List<? extends Operation>) operation.getPayLoad();

            for (int y = 0; y < operationList.size(); y++) {
                Operation policy = operationList.get(y);
                if (policy.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.CAMERA)) {

                    Replace replaceCameraConfig = new Replace();
                    Item cameraItem;
                    List<Item> cameraItems = new ArrayList<Item>();
                    try {
                        cameraItem = appendReplaceInfo(policy);
                        cameraItems.add(cameraItem);
                    } catch (JSONException e) {
                        String msg = "Error occurred while parsing payload object to json.";
                        log.error(msg);
                    }
                    replaceCameraConfig.setCommandId(operation.getId());
                    replaceCameraConfig.setItems(cameraItems);
                    replaceItems.add(replaceCameraConfig);
                }
                if (policy.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.ENCRYPT_STORAGE)) {

                    Replace replaceStorageConfig = new Replace();
                    Item storageItem;
                    List<Item> storageItems = new ArrayList<Item>();
                    try {
                        storageItem = appendReplaceInfo(policy);
                        storageItems.add(storageItem);
                    } catch (JSONException e) {
                        throw new WindowsOperationException("Error occurred while parsing payload object to json.", e);
                    }
                    replaceStorageConfig.setCommandId(operation.getId());
                    replaceStorageConfig.setItems(storageItems);
                    replaceItems.add(replaceStorageConfig);

                }
                if (policy.getCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.PASSCODE_POLICY)) {


                    Atomic atomicElement = new Atomic();
                    List<Add> addConfig;
                    try {
                        addConfig = appendAddInfo(policy);
                        atomicElement.setAdds(addConfig);
                        atomicElement.setCommandId(operation.getId());
                        sequenceElement.setAtomic(atomicElement);
                    } catch (WindowsOperationException e) {
                        throw new WindowsOperationException("Error occurred while generating operation payload.", e);
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

    public List<Item> buildMonitorOperation(List<ProfileFeature> effectiveMonitoringFeature) {
        List<Item> monitorItems = new ArrayList<>();
        Operation monitorOperation;
        for (int y = 0; y < effectiveMonitoringFeature.size(); y++) {
            ProfileFeature profileFeature = effectiveMonitoringFeature.get(y);

            if (profileFeature.getFeatureCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                    .OperationCodes.CAMERA)) {
                String cameraStatus = org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.CAMERA_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(cameraStatus);
                Item item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
            if (profileFeature.getFeatureCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                    .OperationCodes.ENCRYPT_STORAGE)) {
                String encryptStorageStatus = org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.ENCRYPT_STORAGE_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(encryptStorageStatus);
                Item item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
            if (profileFeature.getFeatureCode().equals(org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                    .OperationCodes.PASSCODE_POLICY)) {
                String passcodeStatus = org.wso2.carbon.mdm.mobileservices.windows.common.Constants
                        .OperationCodes.DEVICE_PASSWORD_STATUS;

                monitorOperation = new Operation();
                monitorOperation.setCode(passcodeStatus);
                Item item = appendGetInfo(monitorOperation);
                monitorItems.add(item);
            }
        }
        return monitorItems;
    }

    public Add generatePasscodePolicyData(Configure configure,int policyData) {
        String attempt = String.valueOf(policyData);
        Add add = new Add();
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        Target target = new Target();
        target.setLocURI(configure.getCode());
        Meta meta = new Meta();
        meta.setFormat(Constants.META_FORMAT_INT);
        item.setTarget(target);
        item.setMeta(meta);
        item.setData(attempt);
        itemList.add(item);
        add.setCommandId(90);
        add.setItems(itemList);
        return add;
    }
}


