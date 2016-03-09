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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the receiving SyncML payload and generates the SyncML document object from it.
 */
public class SyncmlParser {

    private static String commandId;
    private static String messageReference;
    private static String commandReference;
    private static final String SYNC_HEADER = "SyncHdr";
    private static final String SYNC_BODY = "SyncBody";

    private enum SyncMLHeaderParameter {
        MSG_ID("MsgID"),
        SESSION_ID("SessionID"),
        TARGET("Target"),
        SOURCE("Source"),
        CRED("Cred");
        private final String parameterName;

        SyncMLHeaderParameter(final String parameterName) {
            this.parameterName = parameterName;
        }

        public String getValue() {
            return this.parameterName;
        }
    }

    private enum SycMLCommandType {
        ALERT("Alert"),
        REPLACE("Replace"),
        STATUS("Status"),
        RESULTS("Results");
        private final String commandName;

        SycMLCommandType(final String commandName) {
            this.commandName = commandName;
        }

        public String getValue() {
            return this.commandName;
        }
    }


    /**
     * Parses the raw SyncML payload and generates a SyncmlDocument object using the parsed XML contents.
     *
     * @param syncmlPayload - Received SyncML XML payload
     * @return - SyncmlDocument object generated from the received payload
     */
    public static SyncmlDocument parseSyncmlPayload(Document syncmlPayload) {
        SyncmlDocument syncmlDocument = new SyncmlDocument();
        if (syncmlPayload.getElementsByTagName(SYNC_HEADER) == null) {
            throw new IllegalStateException();
        }
        NodeList syncHeaderList = syncmlPayload.getElementsByTagName(SYNC_HEADER);
        Node syncHeader = syncHeaderList.item(0);
        SyncmlHeader header = generateSyncmlHeader(syncHeader);
        if (syncmlPayload.getElementsByTagName(SYNC_BODY) == null) {
            throw new IllegalStateException();
        }
        NodeList syncBodyList = syncmlPayload.getElementsByTagName(SYNC_BODY);
        Node syncBody = syncBodyList.item(0);
        SyncmlBody body = generateSyncmlBody(syncBody);

        syncmlDocument.setHeader(header);
        syncmlDocument.setBody(body);
        return syncmlDocument;
    }

    /**
     * Generates SyncmlHeader object by extracting properties of passed XML node.
     *
     * @param syncHeader - XML node which represents SyncML header
     * @return - SyncmlHeader object
     */
    private static SyncmlHeader generateSyncmlHeader(Node syncHeader) {

        String sessionID = null;
        String messageID = null;
        TargetTag target = null;
        SourceTag source = null;
        CredentialTag credential = null;
        SyncmlHeader header = new SyncmlHeader();

        NodeList headerElements = syncHeader.getChildNodes();
        for (int i = 0; i < headerElements.getLength(); i++) {
            Node node = headerElements.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();

                if (SyncMLHeaderParameter.MSG_ID.getValue().equals(nodeName)) {
                    if (node.getTextContent().trim() == null) {
                        throw new IllegalStateException();
                    } else {
                        messageID = node.getTextContent().trim();
                    }
                } else if (SyncMLHeaderParameter.SESSION_ID.getValue().equals(nodeName)) {
                    if (node.getTextContent().trim() == null) {
                        throw new IllegalStateException();
                    } else {
                        sessionID = node.getTextContent().trim();
                    }
                } else if (SyncMLHeaderParameter.TARGET.getValue().equals(nodeName)) {
                    if (node.getTextContent().trim() == null) {
                        throw new IllegalStateException();
                    } else {
                        target = generateTarget(node);
                    }
                } else if (SyncMLHeaderParameter.SOURCE.getValue().equals(nodeName)) {
                    if (node.getTextContent().trim() == null) {
                        throw new IllegalStateException();
                    } else {
                        source = generateSource(node);
                    }
                } else if (SyncMLHeaderParameter.CRED.getValue().equals(nodeName)) {
                    if (node.getTextContent().trim() == null) {
                        throw new IllegalStateException();
                    } else {
                        credential = generateCredential(node);
                    }
                }
            }
        }
        header.setMsgID(Integer.valueOf(messageID));
        // Syncml message contains a sessionID which is Hexadecimal value.Hexadecimal sessionID parse as a integer value.
        header.setSessionId(Integer.valueOf(sessionID, 16));
        header.setTarget(target);
        header.setSource(source);
        header.setCredential(credential);
        return header;
    }

    /**
     * Generates SyncmlBody object by extracting properties of passed XML node.
     *
     * @param syncBody - XML node which represents SyncML body
     * @return - SyncmlBody object
     */
    private static SyncmlBody generateSyncmlBody(Node syncBody) {

        AlertTag alert = null;
        ReplaceTag replace = null;
        ResultsTag results = null;
        List<StatusTag> status = new ArrayList<>();
        NodeList bodyElements = syncBody.getChildNodes();

        for (int i = 0; i < bodyElements.getLength(); i++) {
            Node node = bodyElements.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = node.getNodeName();

                if (SycMLCommandType.ALERT.getValue().equals(nodeName)) {
                    alert = generateAlert(node);
                } else if (SycMLCommandType.REPLACE.getValue().equals(nodeName)) {
                    replace = generateReplace(node);
                } else if (SycMLCommandType.STATUS.getValue().equals(nodeName)) {
                    status.add(generateStatus(node));
                } else if (SycMLCommandType.RESULTS.getValue().equals(nodeName)) {
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
     *
     * @param node - XML node which represents Source
     * @return - Source object
     */
    private static SourceTag generateSource(Node node) {

        SourceTag source = new SourceTag();
        Node sourceURIItem = node.getChildNodes().item(0);
        Node sourceNameItem = node.getChildNodes().item(1);
        String sourceURI = null;
        String sourceName = null;

        if (sourceURIItem != null) {
            sourceURI = sourceURIItem.getTextContent().trim();
        }
        if (sourceNameItem != null) {
            sourceName = sourceNameItem.getTextContent().trim();
        }
        source.setLocURI(sourceURI);
        source.setLocName(sourceName);
        return source;
    }

    /**
     * Generates Target object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Target
     * @return - Target object
     */
    private static TargetTag generateTarget(Node node) {

        TargetTag target = new TargetTag();
        Node targetURIItem = node.getChildNodes().item(0);
        Node targetNameItem = node.getChildNodes().item(1);
        String targetURI = null;
        String targetName = null;

        if (targetURIItem != null) {
            targetURI = targetURIItem.getTextContent().trim();
        }
        if (targetNameItem != null) {
            targetName = targetNameItem.getTextContent().trim();
        }
        target.setLocURI(targetURI);
        target.setLocName(targetName);
        return target;
    }

    /**
     * Generates Results object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Results
     * @return - Results object
     */
    private static ResultsTag generateResults(Node node) {

        ResultsTag results = new ResultsTag();
        List<ItemTag> item = new ArrayList<>();

        if (node.getNodeType() == Node.ELEMENT_NODE) {

            NodeList nodelist = node.getChildNodes();

            for (int i = 0; i < nodelist.getLength(); i++) {
                String nodeName = nodelist.item(i).getNodeName();

                switch (nodeName) {
                    case Constants.COMMAND_ID:
                        commandId = node.getChildNodes().item(i).getTextContent().trim();
                        break;
                    case Constants.MESSAGE_REFERENCE:
                        messageReference = node.getChildNodes().item(i).getTextContent().trim();
                        break;
                    case Constants.COMMAND_REFERENCE:
                        commandReference = node.getChildNodes().item(i).getTextContent().trim();
                        break;
                    case Constants.ITEM:
                        item.add(generateItem(node.getChildNodes().item(i)));
                        break;
                }
            }
            results.setCommandId(Integer.valueOf(commandId));
            results.setMessageReference(Integer.valueOf(messageReference));
            results.setCommandReference(Integer.valueOf(commandReference));
            results.setItem(item);
        }
        return results;
    }

    /**
     * Generates Status object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Status
     * @return - Status object
     */
    private static StatusTag generateStatus(Node node) {

        StatusTag status = new StatusTag();
        for (int x = 0; x < node.getChildNodes().getLength(); x++) {
            String nodeName = node.getChildNodes().item(x).getNodeName();
            switch (nodeName) {
                case PluginConstants.SyncML.SYNCML_CMD_ID:
                    String commandId = node.getChildNodes().item(x).getTextContent().trim();
                    status.setCommandId(Integer.valueOf(commandId));
                    break;
                case PluginConstants.SyncML.SYNCML_MESSAGE_REF:
                    String messageReference = node.getChildNodes().item(x).getTextContent().trim();
                    status.setMessageReference(Integer.valueOf(messageReference));
                    break;
                case PluginConstants.SyncML.SYNCML_CMD_REF:
                    String commandReference = node.getChildNodes().item(x).getTextContent().trim();
                    status.setCommandReference(Integer.valueOf(commandReference));
                    break;
                case PluginConstants.SyncML.SYNCML_CMD:
                    String command = node.getChildNodes().item(x).getTextContent().trim();
                    status.setCommand(command);
                    break;
                case PluginConstants.SyncML.SYNCML_CHAL:
                    NodeList childNodes = node.getChildNodes().item(x).getChildNodes();
                    MetaTag meta = new MetaTag();
                    ChallengeTag challengeTag = new ChallengeTag();
                    String format = childNodes.item(0).getFirstChild().getTextContent();
                    meta.setFormat(format);
                    String type = childNodes.item(0).getFirstChild().getNextSibling().getTextContent();
                    meta.setType(type);
                    String nonce = childNodes.item(0).getFirstChild().getNextSibling().getNextSibling().getTextContent();
                    meta.setNextNonce(nonce);
                    challengeTag.setMeta(meta);
                    status.setChallenge(challengeTag);
                    break;
                case PluginConstants.SyncML.SYNCML_DATA:
                    String data = node.getChildNodes().item(x).getTextContent().trim();
                    status.setData(data);
                    break;
                case PluginConstants.SyncML.SYNCML_TARGET_REF:
                    String targetReference = node.getChildNodes().item(x).getTextContent().trim();
                    status.setTargetReference(targetReference);
                    break;
            }
        }
        return status;
    }

    /**
     * Generates Replace object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Replace
     * @return - Replace object
     */
    private static ReplaceTag generateReplace(Node node) {

        ReplaceTag replace = new ReplaceTag();
        String commandId = node.getChildNodes().item(0).getTextContent().trim();
        List<ItemTag> items = new ArrayList<>();
        for (int i = 0; i < node.getChildNodes().getLength() - 1; i++) {
            items.add(generateItem(node.getChildNodes().item(i + 1)));
        }
        replace.setCommandId(Integer.valueOf(commandId));
        replace.setItems(items);
        return replace;
    }

    /**
     * Generates Alert object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Alert
     * @return - Alert object
     */
    private static AlertTag generateAlert(Node node) {
        AlertTag alert = new AlertTag();
        String commandID = node.getChildNodes().item(0).getTextContent().trim();
        String data = node.getChildNodes().item(1).getTextContent().trim();
        alert.setCommandId(Integer.valueOf(commandID));
        alert.setData(data);
        return alert;
    }

    /**
     * Generates Item object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Item
     * @return - Item object
     */
    private static ItemTag generateItem(Node node) {
        ItemTag item = new ItemTag();
        SourceTag source = new SourceTag();
        String data;
        String nodeName;
        String childNodeName;
        String locUri;
        for (int x = 0; x < node.getChildNodes().getLength(); x++) {
            Node itemNode;
            itemNode = node.getChildNodes().item(x);
            if (itemNode.getNodeName() != null) {
                nodeName = node.getChildNodes().item(x).getNodeName();
            } else {
                throw new IllegalStateException();
            }
            if (PluginConstants.SyncML.SYNCML_SOURCE.equals(nodeName)) {
                if (itemNode.getChildNodes().item(x).getNodeName() != null) {
                    childNodeName = itemNode.getChildNodes().item(x).getNodeName();
                } else {
                    throw new IllegalStateException();
                }
                if ((PluginConstants.SyncML.SYNCML_LOCATION_URI.equals(childNodeName))) {
                    if (itemNode.getChildNodes().item(x).getTextContent().trim() != null) {
                        locUri = itemNode.getChildNodes().item(x).getTextContent().trim();
                    } else {
                        throw new IllegalStateException();
                    }
                    source.setLocURI(locUri);
                    item.setSource(source);
                }
            } else if (PluginConstants.SyncML.SYNCML_DATA.equals(nodeName)) {
                if (itemNode.getTextContent().trim() != null) {
                    data = itemNode.getTextContent().trim();
                } else {
                    throw new IllegalStateException();
                }
                item.setData(data);
            }
        }
        return item;
    }

    /**
     * Generates Credential object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents Credential
     * @return - Credential object
     */
    private static CredentialTag generateCredential(Node node) {
        CredentialTag credential = new CredentialTag();
        MetaTag meta = generateMeta(node.getChildNodes().item(0));
        String data = node.getChildNodes().item(1).getTextContent().trim();
        credential.setMeta(meta);
        credential.setData(data);
        return credential;
    }

    /**
     * Generates MetaTag object by extracting properties of passed XML node.
     *
     * @param node - XML node which represents MetaTag
     * @return - MetaTag object
     */
    private static MetaTag generateMeta(Node node) {
        MetaTag meta = new MetaTag();
        String format = node.getChildNodes().item(0).getTextContent().trim();
        String type = node.getChildNodes().item(1).getTextContent().trim();
        meta.setFormat(format);
        meta.setType(type);
        return meta;
    }
}
