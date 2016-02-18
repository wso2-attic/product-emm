/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

/**
 * This class performs one time operations.
 */
public class ContextInitializer implements ServletContextListener {

    /**
     * This method loads wap-provisioning file and sets wap-provisioning file as attribute in servlet context.
     *
     * @param servletContextEvent - Uses when servlet communicating with servlet container.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        File wapProvisioningFile = new File(getClass().getClassLoader().getResource(
                PluginConstants.CertificateEnrolment.WAP_PROVISIONING_XML).getFile());
        servletContext.setAttribute(PluginConstants.CONTEXT_WAP_PROVISIONING_FILE, wapProvisioningFile);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
