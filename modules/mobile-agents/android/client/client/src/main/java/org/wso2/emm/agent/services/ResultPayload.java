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
package org.wso2.emm.agent.services;

import org.wso2.emm.agent.beans.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles building of the operation list result payload to be sent to the server.
 */
public class ResultPayload {

	private List<Operation> operationResponses;

	public ResultPayload(){
		this.operationResponses = new ArrayList<Operation>();
	}

	/**
	 * Setup the payload operation list
	 * @param operation
	 */
	public void build(org.wso2.emm.agent.beans.Operation operation) {
		if (operation.getId() > 0) {
			operationResponses.add(operation);
		}
	}

	/**
	 * Return final results payload.
	 * @return - List of operations
	 */
	public List<Operation> getResultPayload(){
		return this.operationResponses;
	}

	/**
	 * Return an operation object for given operation id.
	 * @param id Operation id.
	 * @return returns an object if id matches.
	 */
	public Operation getResult(int id) {
		for (Operation operation : operationResponses) {
			if (operation.getId() == id) {
				return operation;
			}
		}
		return null;
	}
}
