/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.agent.services;

import org.wso2.mdm.agent.beans.Operation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles building of the operation list result payload to be sent to the server.
 */
public class BuildResultPayload {

	private List<Operation> operationResponses;
	
	public BuildResultPayload(){
		this.operationResponses = new ArrayList<Operation>();
	}

	/**
	 * Setup the payload operation list
	 * @param operation
	 */
	public void build(org.wso2.mdm.agent.beans.Operation operation) {
		operationResponses.add(operation);
	}
	
	
	/**
	 * Return final results payload.
	 * @return - List of operations
	 */
	public List<Operation> getResultPayload(){
		return this.operationResponses;
	}
}
