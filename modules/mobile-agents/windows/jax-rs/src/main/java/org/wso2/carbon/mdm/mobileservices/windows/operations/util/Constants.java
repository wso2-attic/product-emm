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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

/**
 * Constant values used in syncml generator.
 */
public class Constants {
	public static final String SYNCML_ROOT_ELEMENT_NAME = "SyncML";
	public static final String XMLNS_SYNCML = "SYNCML:SYNCML1.2";
	public static final String UTF_8 = "UTF-8";
	public static final String YES = "yes";

	public static final String EXECUTE = "Exec";
	public static final String COMMAND_ID = "CmdID";
	public static final String GET = "Get";
	public static final String ITEM = "Item";
	public static final String SOURCE = "Source";
	public static final String LOC_URI = "LocURI";
	public static final String LOC_NAME = "LocName";
	public static final String MESSAGE_REFERENCE = "MsgRef";
	public static final String COMMAND_REFERENCE = "CmdRef";
	public static final String COMMAND = "Cmd";
	public static final String TARGET_REFERENCE = "TargetRef";
	public static final String DATA = "Data";
	public static final String STATUS = "Status";
	public static final String SYNC_BODY = "SyncBody";
	public static final String SYNC_HDR = "SyncHdr";
	public static final String VER_DTD = "VerDTD";
	public static final String VER_PROTOCOL = "VerProto";
	public static final String SESSION_ID = "SessionID";
	public static final String MESSAGE_ID = "msgId";
	public static final String TARGET = "Target";
	public static final String VER_DTD_VALUE = "1.2";
	public static final String VER_PROTOCOL_VALUE = "DM/1.2";
	public static final String ALERT = "Alert";
	public static final String FINAL = "Final";
	public static final String REPLACE = "Replace";

	/**
	 * SynclML service related constants
	 */
	public final class SyncMLResponseCodes {
		public static final int AUTHENTICATION_ACCEPTED = 212;
		public static final int ACCEPTED = 200;
	}

}
