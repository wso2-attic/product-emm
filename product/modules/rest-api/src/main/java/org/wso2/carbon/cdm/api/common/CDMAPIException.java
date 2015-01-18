/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.cdm.api.common;

/**
 * Custom exception class for handling CDM API related exceptions.
 */
public class CDMAPIException extends Exception {

	private static final long serialVersionUID = 7950151650447893900L;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public CDMAPIException(String msg, Exception nestedEx) {
		super(msg, nestedEx);
		setErrorMessage(msg);
	}

	public CDMAPIException(String message, Throwable cause) {
		super(message, cause);
		setErrorMessage(message);
	}

	public CDMAPIException(String msg) {
		super(msg);
		setErrorMessage(msg);
	}

	public CDMAPIException() {
		super();
	}

	public CDMAPIException(Throwable cause) {
		super(cause);
	}
}
