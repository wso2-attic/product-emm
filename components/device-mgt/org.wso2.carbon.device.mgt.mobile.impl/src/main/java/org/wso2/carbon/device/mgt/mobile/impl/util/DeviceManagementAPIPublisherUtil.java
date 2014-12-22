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
package org.wso2.carbon.device.mgt.mobile.impl.util;

import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIStatus;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.usage.publisher.service.APIMGTConfigReaderService;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.impl.config.APIConfig;
import org.wso2.carbon.utils.CarbonUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DeviceManagementAPIPublisherUtil {

    enum HTTPMethod {
        GET, POST, DELETE, PUT, OPTIONS
    }

    private static List<HTTPMethod> httpMethods;

    static {
        httpMethods = new ArrayList<HTTPMethod>();
        httpMethods.add(HTTPMethod.GET);
        httpMethods.add(HTTPMethod.POST);
        httpMethods.add(HTTPMethod.DELETE);
        httpMethods.add(HTTPMethod.PUT);
        httpMethods.add(HTTPMethod.OPTIONS);
    }

    public static void publishAPI(APIConfig config) throws DeviceManagementException {
        APIProvider provider = config.getProvider();
        APIIdentifier id = new APIIdentifier(config.getOwner(), config.getName(), config.getVersion());
        API api = new API(id);
        try {
            api.setContext(config.getContext());
            api.setUrl(config.getVersion());
            api.setUriTemplates(getURITemplates(config.getEndpoint(),
                    APIConstants.AUTH_APPLICATION_OR_USER_LEVEL_TOKEN));
            api.setVisibility(APIConstants.API_GLOBAL_VISIBILITY);
            api.addAvailableTiers(provider.getTiers());
            api.setEndpointSecured(false);
            api.setStatus(APIStatus.PUBLISHED);
            api.setTransports(config.getTransports());

            provider.addAPI(api);
        } catch (APIManagementException e) {
            throw new DeviceManagementException("Error occurred while registering the API", e);
        }
    }

    public static void removeAPI(APIConfig config) throws DeviceManagementException {
        try {
            APIProvider provider = config.getProvider();
            APIIdentifier id = new APIIdentifier(config.getOwner(), config.getName(), config.getVersion());
            provider.deleteAPI(id);
        } catch (APIManagementException e) {
            throw new DeviceManagementException("Error occurred while removing API", e);
        }
    }

    private static Set<URITemplate> getURITemplates(String endpoint, String authType) {
        Set<URITemplate> uriTemplates = new LinkedHashSet<URITemplate>();
        if (APIConstants.AUTH_NO_AUTHENTICATION.equals(authType)) {
            for (HTTPMethod method : httpMethods) {
                URITemplate template = new URITemplate();
                template.setAuthType(APIConstants.AUTH_NO_AUTHENTICATION);
                template.setHTTPVerb(method.toString());
                template.setResourceURI(endpoint);
                template.setUriTemplate("/*");
                uriTemplates.add(template);
            }
        } else {
            for (HTTPMethod method : httpMethods) {
                URITemplate template = new URITemplate();
                if (HTTPMethod.OPTIONS.equals(method)) {
                    template.setAuthType(APIConstants.AUTH_NO_AUTHENTICATION);
                } else {
                    template.setAuthType(APIConstants.AUTH_APPLICATION_OR_USER_LEVEL_TOKEN);
                }
                template.setHTTPVerb(method.toString());
                template.setResourceURI(endpoint);
                template.setUriTemplate("/*");
                uriTemplates.add(template);
            }
        }
        return uriTemplates;
    }

}
