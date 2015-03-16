/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * /
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations;

import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents the header details of a syncml.
 */
public class SyncmlHeader {
	private String sessionID;
	private String msgId;
	private Target target;
	private Source source;

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public void buildSyncmlHeaderElement(Document doc, Element rootElement) {
		Element syncHdr = doc.createElement(Constants.SYNC_HDR);
		rootElement.appendChild(syncHdr);
		Element verDTD = doc.createElement(Constants.VER_DTD);
		verDTD.appendChild(doc.createTextNode(Constants.VER_DTD_VALUE));
		syncHdr.appendChild(verDTD);

		Element verProtocol = doc.createElement(Constants.VER_PROTOCOL);
		verProtocol.appendChild(doc.createTextNode(Constants.VER_PROTOCOL_VALUE));
		syncHdr.appendChild(verProtocol);

		if (getSessionID() != null) {
			Element sessionId = doc.createElement(Constants.SESSION_ID);
			sessionId.appendChild(doc.createTextNode(getSessionID()));
			syncHdr.appendChild(sessionId);
		}

		if (getMsgId() != null) {
			Element msgId = doc.createElement(Constants.MESSAGE_ID);
			msgId.appendChild(doc.createTextNode(getMsgId()));
			syncHdr.appendChild(msgId);
		}

		if (getTarget() != null) {
			target.buildTargetElement(doc, syncHdr);
		}

	}
}
