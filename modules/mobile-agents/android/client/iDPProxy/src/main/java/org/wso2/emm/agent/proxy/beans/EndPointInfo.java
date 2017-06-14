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
package org.wso2.emm.agent.proxy.beans;

import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;

import java.util.Map;

/**
 * This class represents end-point information required when invoking APIs.
 */
public class EndPointInfo {
	private HTTP_METHODS httpMethod;
	private String endPoint;
	private String requestParams;
	private Map<String, String> requestParamsMap;
	private boolean isJSONArrayRequest;

	public Map<String, String> getRequestParamsMap() {
		return requestParamsMap;
	}

	public void setRequestParamsMap(Map<String, String> requestParams) {
		this.requestParamsMap = requestParams;
	}

	public String getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(String requestParams) {
		this.requestParams = requestParams;
	}

	public HTTP_METHODS getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HTTP_METHODS httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public boolean isJSONArrayRequest() {
		return isJSONArrayRequest;
	}

	public void setIsJSONArrayRequest(boolean isJSONArrayRequest) {
		this.isJSONArrayRequest = isJSONArrayRequest;
	}

}
