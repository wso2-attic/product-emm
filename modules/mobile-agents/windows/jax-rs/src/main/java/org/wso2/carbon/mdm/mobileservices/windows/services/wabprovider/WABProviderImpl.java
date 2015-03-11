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

package org.wso2.carbon.mdm.mobileservices.windows.services.wabprovider;


import javax.ws.rs.core.Response;

public class WABProviderImpl implements WABProvider {


    @Override
    public Response federated(String appru, String hint) {

        String response =
                "<!DOCTYPE>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>Working...</title>\n" +
                        "    <script>\n" +
                        "        function formSubmit() {\n" +
                        "            document.forms[0].submit();\n" +
                        "        }\n" +
                        "        window.onload=formSubmit;\n" +
                        "    </script>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<form method=\"post\" action=\""+appru+"\">\n" +
                        "<p><input type=\"hidden\" name=\"wresult\" value=\""+"123456789123456789"+"\"/></p>\n" +
                        "<input type=\"submit\"/>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>";

        return Response.ok().entity(response).build();
    }
}
