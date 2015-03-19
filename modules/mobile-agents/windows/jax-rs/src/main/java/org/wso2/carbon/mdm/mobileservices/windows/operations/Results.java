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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;

/**
 * Results sent for the requests made to the device.
 */
public class Results {
	int commandId = -1;
	int messageReference = -1;
	int commandReference = -1;
	Item item;

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public int getMessageReference() {
		return messageReference;
	}

	public void setMessageReference(int messageReference) {
		this.messageReference = messageReference;
	}

	public int getCommandReference() {
		return commandReference;
	}

	public void setCommandReference(int commandReference) {
		this.commandReference = commandReference;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void buildResultElement(Document doc, Element rootElement) {
		Element get = doc.createElement(Constants.RESULTS);
		rootElement.appendChild(get);
		if (getCommandId() != -1) {
			Element commandId = doc.createElement(Constants.COMMAND_ID);
			commandId.appendChild(doc.createTextNode(String.valueOf(getCommandId())));
			get.appendChild(commandId);
		}
		if (getMessageReference() != -1) {
			Element messageReference = doc.createElement(Constants.MESSAGE_REFERENCE);
			messageReference.appendChild(doc.createTextNode(String.valueOf(getMessageReference())));
			get.appendChild(messageReference);
		}
		if (getCommandReference() != -1) {
			Element messageReference = doc.createElement(Constants.COMMAND_REFERENCE);
			messageReference.appendChild(doc.createTextNode(String.valueOf(getCommandReference())));
			get.appendChild(messageReference);
		}
		if (getItem() != null) {
			getItem().buildItemElement(doc, get);
		}
	}
}
