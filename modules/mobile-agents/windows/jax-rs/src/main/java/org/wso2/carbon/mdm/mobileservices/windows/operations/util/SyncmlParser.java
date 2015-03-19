/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the receiving SyncML payload and generates the SyncML document object from it.
 */
public class SyncmlParser {

    private static final String SYNC_HDR = "SyncHdr";
    private static final String SYNC_BODY = "SyncBody";
    private enum SyncMLHeaderParameter {
        MSG_ID("MsgID"),
        SESSION_ID("SessionID"),
        TARGET("Target"),
        SOURCE("Source"),
        CRED("Cred");
        private final String parameterName;
        private SyncMLHeaderParameter(final String parameterName) {
            this.parameterName = parameterName;
        }
        public String getValue() {
            return this.parameterName;
        }
    }
    private enum SycMLCommandType{
        ALERT("Alert"),
        REPLACE("Replace"),
        STATUS("Status"),
        RESULTS("Results");
        private final String commandName;
        private SycMLCommandType(final String commandName){
            this.commandName = commandName;
        }
        public String getValue() {
            return this.commandName;
        }
    }
    private static Log log = LogFactory.getLog(SyncmlParser.class);

    /**
     * Parses the raw SyncML payload and generates a SyncmlDocument object using the parsed XML contents.
     * @param syncmlPayload - Received SyncML XML payload
     * @return - SyncmlDocument object generated from the received payload
     */
    public static SyncmlDocument parseSyncmlPayload(Document syncmlPayload) throws WindowsOperationException {

        NodeList syncHeaderList = syncmlPayload.getElementsByTagName(SYNC_HDR);
        Node syncHeader = syncHeaderList.item(0);
        SyncmlHeader header = generateSyncmlHeader(syncHeader);

        NodeList syncBodyList = syncmlPayload.getElementsByTagName(SYNC_BODY);
        Node syncBody = syncBodyList.item(0);
        SyncmlBody body = generateSyncmlBody(syncBody);

        SyncmlDocument syncmlDocument = new SyncmlDocument();
        syncmlDocument.setHeader(header);
        syncmlDocument.setBody(body);
        return syncmlDocument;
    }

    /**
     * Generates SyncmlHeader object by extracting properties of passed XML node.
     * @param syncHeader - XML node which represents SyncML header
     * @return - SyncmlHeader object
     */
    private static SyncmlHeader generateSyncmlHeader(Node syncHeader) {

        NodeList headerElements = syncHeader.getChildNodes();
        String sessionID = null;
        String messageID = null;
        Target target = null;
        Source source = null;
        Credential credential = null;

        for (int i = 0 ; i < headerElements.getLength() ; i++) {
            Node node = headerElements.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();

                if (SyncMLHeaderParameter.MSG_ID.getValue().equals(nodeName)) {
                    messageID = node.getTextContent().trim();
                }
                else if(SyncMLHeaderParameter.SESSION_ID.getValue().equals(nodeName)){
                    sessionID = node.getTextContent().trim();
                }
                else if (SyncMLHeaderParameter.TARGET.getValue().equals(nodeName)) {
                    target = generateTarget(node);
                }
                else if (SyncMLHeaderParameter.SOURCE.getValue().equals(nodeName)) {
                    source = generateSource(node);
                }
                else if (SyncMLHeaderParameter.CRED.getValue().equals(nodeName)) {
                    credential = generateCredential(node);
                }
            }
        }
        SyncmlHeader header = new SyncmlHeader();
        header.setMsgID(Integer.valueOf(messageID));
        header.setSessionId(Integer.valueOf(sessionID));
        header.setTarget(target);
        header.setSource(source);
        header.setCredential(credential);
        return header;
    }

    /**
     * Generates SyncmlBody object by extracting properties of passed XML node.
     * @param syncBody - XML node which represents SyncML body
     * @return - SyncmlBody object
     */
    private static SyncmlBody generateSyncmlBody(Node syncBody) {

        NodeList bodyElements = syncBody.getChildNodes();
        Alert alert = null;
        Replace replace = null;
        Results results = null;
        List<Status> status = new ArrayList<Status>();

        for (int i = 0 ; i < bodyElements.getLength() ; i++) {
            Node node = bodyElements.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();

                if(SycMLCommandType.ALERT.getValue().equals(nodeName)){
                    alert = generateAlert(node);
                }
                else if(SycMLCommandType.REPLACE.getValue().equals(nodeName)){
                    replace = generateReplace(node);
                }
                else if(SycMLCommandType.STATUS.getValue().equals(nodeName)){
                    status.add(generateStatus(node));
                }
                else if(SycMLCommandType.RESULTS.getValue().equals(nodeName)){
                    results = generateResults(node);
                }
            }
        }
        SyncmlBody body = new SyncmlBody();
        body.setAlert(alert);
        body.setReplace(replace);
        body.setStatus(status);
        body.setResults(results);
        return body;
    }

    /**
     * Generates Source object by extracting properties of passed XML node.
     * @param node - XML node which represents Source
     * @return - Source object
     */
    private static Source generateSource(Node node) {

        Source source = new Source();
        Node sourceURIItem = node.getChildNodes().item(0);
        Node sourceNameItem = node.getChildNodes().item(1);
        String sourceURI = null;
        String sourceName = null;

        if(sourceURIItem != null) {
            sourceURI = sourceURIItem.getTextContent().trim();
        }
        if(sourceNameItem != null) {
            sourceName = sourceNameItem.getTextContent().trim();
        }
        source.setLocURI(sourceURI);
        source.setLocName(sourceName);
        return source;
    }

    /**
     * Generates Target object by extracting properties of passed XML node.
     * @param node - XML node which represents Target
     * @return - Target object
     */
    private static Target generateTarget(Node node) {

        Target target = new Target();
        Node targetURIItem = node.getChildNodes().item(0);
        Node targetNameItem = node.getChildNodes().item(1);
        String targetURI = null;
        String targetName = null;

        if(targetURIItem != null) {
            targetURI = targetURIItem.getTextContent().trim();
        }
        if(targetNameItem != null) {
            targetName = targetNameItem.getTextContent().trim();
        }
        target.setLocURI(targetURI);
        target.setLocName(targetName);
        return target;
    }

    /**
     * Generates Results object by extracting properties of passed XML node.
     * @param node - XML node which represents Results
     * @return - Results object
     */
    private static Results generateResults(Node node) {

        Results results = new Results();

        if(node.getNodeType() == Node.ELEMENT_NODE) {
            String commandID = node.getChildNodes().item(0).getTextContent().trim();
            String messageReference = node.getChildNodes().item(1).getTextContent().trim();
            String commandReference = node.getChildNodes().item(2).getTextContent().trim();
            Item item = generateItem(node.getChildNodes().item(3));

            results.setCommandId(Integer.valueOf(commandID));
            results.setMessageReference(Integer.valueOf(messageReference));
            results.setCommandReference(Integer.valueOf(commandReference));
            results.setItem(item);
        }
        return results;
    }

    /**
     * Generates Status object by extracting properties of passed XML node.
     * @param node - XML node which represents Status
     * @return - Status object
     */
    private static Status generateStatus(Node node) {

        Status status = new Status();
        String commandId = node.getChildNodes().item(0).getTextContent().trim();
        String messageReference = node.getChildNodes().item(1).getTextContent().trim();
        String commandReference = node.getChildNodes().item(2).getTextContent().trim();
        String command = node.getChildNodes().item(3).getTextContent().trim();
        String targetReference = node.getChildNodes().item(4).getTextContent().trim();
        String data = node.getChildNodes().item(5).getTextContent().trim();

        status.setCommandId(Integer.valueOf(commandId));
        status.setMessageReference(Integer.valueOf(messageReference));
        status.setCommandReference(Integer.valueOf(commandReference));
        status.setCommand(command);
        status.setTargetReference(targetReference);
        status.setData(data);
        return status;
    }

    /**
     * Generates Replace object by extracting properties of passed XML node.
     * @param node - XML node which represents Replace
     * @return - Replace object
     */
    private static Replace generateReplace(Node node) {

        Replace replace = new Replace();
        String commandId = node.getChildNodes().item(0).getTextContent().trim();
        List<Item> items = new ArrayList<Item>();
        for(int i=0 ; i<node.getChildNodes().getLength()-1 ; i++){
            items.add(generateItem(node.getChildNodes().item(i+1)));
        }
        replace.setCommandId(Integer.valueOf(commandId));
        replace.setItems(items);
        return replace;
    }

    /**
     * Generates Alert object by extracting properties of passed XML node.
     * @param node - XML node which represents Alert
     * @return - Alert object
     */
    private static Alert generateAlert(Node node) {
        Alert alert = new Alert();
        String commandID = node.getChildNodes().item(0).getTextContent().trim();
        String data = node.getChildNodes().item(1).getTextContent().trim();
        alert.setCommandId(Integer.valueOf(commandID));
        alert.setData(data);
        return alert;
    }

    /**
     * Generates Item object by extracting properties of passed XML node.
     * @param node - XML node which represents Item
     * @return - Item object
     */
    private static Item generateItem(Node node){
        Item item = new Item();
        String data;

        if(SyncMLHeaderParameter.SOURCE.getValue().equals(node.getChildNodes().item(0).getNodeName())){
            Source source = generateSource(node.getChildNodes().item(0));
            item.setSource(source);
        }
        else if(SyncMLHeaderParameter.TARGET.getValue().equals(node.getChildNodes().item(0).getNodeName())){
            Target target = generateTarget(node.getChildNodes().item(0));
            item.setTarget(target);
        }
        Node dataItem = node.getChildNodes().item(1);
        data = dataItem.getTextContent().trim();
        item.setData(data);
        return item;
    }

    /**
     * Generates Credential object by extracting properties of passed XML node.
     * @param node - XML node which represents Credential
     * @return - Credential object
     */
    private static Credential generateCredential(Node node) {
        Credential credential = new Credential();
        Meta meta = generateMeta(node.getChildNodes().item(0));
        String data = node.getChildNodes().item(1).getTextContent().trim();
        credential.setMeta(meta);
        credential.setData(data);
        return credential;
    }

    /**
     * Generates Meta object by extracting properties of passed XML node.
     * @param node - XML node which represents Meta
     * @return - Meta object
     */
    private static Meta generateMeta(Node node){
        Meta meta = new Meta();
        String format = node.getChildNodes().item(0).getTextContent().trim();
        String type = node.getChildNodes().item(1).getTextContent().trim();
        meta.setFormat(format);
        meta.setType(type);
        return meta;
    }
}
