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
 *
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.core.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationCode.*;
import static org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationCode.Command.*;

/**
 * Used to generate a reply to a receiving syncml from a device.
 */
public class OperationReply {
	private SyncmlDocument syncmlDocument;
	private SyncmlDocument replySyncmlDocument;
	private static final int HEADER_STATUS_ID = 0;
	private static final int HEADER_COMMAND_REFERENCE_ID = 1;
	private static final String HEADER_COMMAND_TEXT = "SyncHdr";
	private static final String ALERT_COMMAND_TEXT = "Alert";
	private static final String REPLACE_COMMAND_TEXT = "Replace";
	private static final String GET_COMMAND_TEXT = "Get";
	private static final String EXEC_COMMAND_TEXT = "Exec";
	private List<Operation> operations;
	private static Log log = LogFactory.getLog(OperationReply.class);

	public OperationReply(SyncmlDocument syncmlDocument, List<Operation> operations) {
		this.syncmlDocument = syncmlDocument;
		replySyncmlDocument = new SyncmlDocument();
		this.operations = operations;
	}

	public SyncmlDocument generateReply() throws WindowsOperationException {
		generateHeader();
		generateBody();
		return replySyncmlDocument;
	}

	private void generateHeader() {
		SyncmlHeader sourceHeader = syncmlDocument.getHeader();
		SyncmlHeader header = new SyncmlHeader();
		header.setMsgID(sourceHeader.getMsgID());
		header.setSessionId(sourceHeader.getSessionId());
		Target target = new Target();
		target.setLocURI(sourceHeader.getSource().getLocURI());
		header.setTarget(target);

		Source source = new Source();
		source.setLocURI(sourceHeader.getTarget().getLocURI());
		header.setSource(source);
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
		}
		replySyncmlDocument.setBody(syncmlBody);
	}

	private SyncmlBody generateStatuses() {
		SyncmlBody sourceSyncmlBody = syncmlDocument.getBody();
		SyncmlHeader sourceHeader = syncmlDocument.getHeader();
		SyncmlBody syncmlBodyReply = new SyncmlBody();
		List<Status> status = new ArrayList<Status>();
		Status headerStatus =
				new Status(HEADER_COMMAND_REFERENCE_ID, sourceHeader.getMsgID(), HEADER_STATUS_ID,
				           HEADER_COMMAND_TEXT, sourceHeader.getSource().getLocURI(),
				           String.valueOf(Constants.SyncMLResponseCodes.AUTHENTICATION_ACCEPTED));
		status.add(headerStatus);
		if (sourceSyncmlBody.getAlert() != null) {
			Status alertStatus = new Status(sourceSyncmlBody.getAlert().getCommandId(),
			                                HEADER_COMMAND_REFERENCE_ID,
			                                sourceSyncmlBody.getAlert().getCommandId(),
			                                ALERT_COMMAND_TEXT, null,
			                                String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
			status.add(alertStatus);
		}
		if (sourceSyncmlBody.getReplace() != null) {
			Status replaceStatus = new Status(sourceSyncmlBody.getReplace().getCommandId(),
			                                  HEADER_COMMAND_REFERENCE_ID,
			                                  sourceSyncmlBody.getReplace().getCommandId(),
			                                  REPLACE_COMMAND_TEXT, null,
			                                  String.valueOf(
					                                  Constants.SyncMLResponseCodes.ACCEPTED)
			);
			status.add(replaceStatus);
		}
		if (sourceSyncmlBody.getExec() != null) {
			Status replaceStatus = new Status(sourceSyncmlBody.getExec().getCommandId(),
			                                  HEADER_COMMAND_REFERENCE_ID,
			                                  sourceSyncmlBody.getExec().getCommandId(),
			                                  GET_COMMAND_TEXT, null,
			                                  String.valueOf(
					                                  Constants.SyncMLResponseCodes.ACCEPTED)
			);
			status.add(replaceStatus);
		}
		if (sourceSyncmlBody.getGet() != null) {
			Status execStatus = new Status(sourceSyncmlBody.getGet().getCommandId(),
			                               HEADER_COMMAND_REFERENCE_ID,
			                               sourceSyncmlBody.getGet().getCommandId(),
			                               EXEC_COMMAND_TEXT, null,
			                               String.valueOf(Constants.SyncMLResponseCodes.ACCEPTED));
			status.add(execStatus);
		}

		syncmlBodyReply.setStatus(status);
		return syncmlBodyReply;
	}

	private void appendOperations(SyncmlBody syncmlBody) throws WindowsOperationException {
		Get getElement=new Get();
		List<Item> itemsGet = new ArrayList<Item>();

		Exec execElement=new Exec();
		List<Item> itemsExec= new ArrayList<Item>();

		if (operations != null) {
			for (int x = 0; x < operations.size(); x++) {
				Operation operation = operations.get(x);
				Operation.Type type = operation.getType();
				switch (type) {
					case CONFIG:
						;
						break;
					case MESSAGE:
						;
						break;
					case INFO:
						Item item=appendGetInfo(operation);
						itemsGet.add(item);
						break;
					case COMMAND:
						;
						break;
					default:
						throw new WindowsOperationException("Operation with no type found");

				}
			}
		}
		getElement.setCommandId(75);
		getElement.setItems(itemsGet);
		syncmlBody.setGet(getElement);
	}

//	private Item appendExecuteCommand(Operation operation) {
//		String operationCode = operation.getCode();
//
//		switch (operationCode) {
//			case DEV_ID:
//				;
//				break;
//		}
//		return null;
//	}

	private Item appendGetInfo(Operation operation) {
		Item item=new Item();
		String operationCode = operation.getCode();
		for (Info info : Info.values()) {
			if(operationCode != null && operationCode.equals(info.name())){
				Target target= new Target();
				target.setLocURI(info.getCode());
				item.setTarget(target);
			}
		}
		return item;
	}

}
