/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.apache.ws.security.WSPasswordCallback;

/**
 * Check security credentials of receiving SOAP message and verify. This handler class
 * is used at XCEP and WSTEP stages of the enrollment.
 */
public class PasswordCallbackHandler implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks)
			throws IOException, UnsupportedCallbackException {
		WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];

		//Temporarily using fix credentials for device enrollment. This should be later
		//associated with a user DB
		if ("test@wso2.com".equals(passwordCallback.getIdentifier())) {
			passwordCallback.setPassword("testpassword");
		}
	}
}
