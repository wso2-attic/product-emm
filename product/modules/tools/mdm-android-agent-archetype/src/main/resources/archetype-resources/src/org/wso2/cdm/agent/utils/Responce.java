/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.cdm.agent.utils;

import org.wso2.cdm.agent.R;

/**
 * Describes the errors and their relevant error messages.
 * In order to internationalize the message is the res
 */
public enum Responce {
	  INCOMPATIBLE(false, R.string.device_not_compatible_error),
	  INCOMPATIBLE_OS(false, R.string.device_not_compatible_error_os),
	  COMPATIBLE(true,R.string.device_not_compatible_error_os);

	  private final boolean code;
	  private final int descriptionResourceID;

	  private Responce(boolean code, int description) {
	    this.code = code;
	    this.descriptionResourceID = description;
	  }

	  public int getDescriptionResourceID() {
	     return descriptionResourceID;
	  }

	  public boolean getCode() {
	     return code;
	  }

	}