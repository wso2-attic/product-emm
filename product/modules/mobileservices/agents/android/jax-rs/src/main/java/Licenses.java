/*
 *
 *  * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * WSO2 Inc. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 * /
 */

import org.wso2.cdmserver.mobileservices.android.common.AndroidAgentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.License;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * License Management related JAX RS APIs
 */

@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Licenses {

    private static Log log = LogFactory.getLog(Licenses.class);

    @GET
    public License getLicense(String deviceType) throws AndroidAgentException {
        return null;
    }

}
