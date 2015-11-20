package org.wso2.mdm.appmgt.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.appmgt.mobile.interfaces.MDMOperations;
import org.wso2.mdm.appmgt.service.MDMOperationsImpl;

/**
 * @scr.component name="org.wso2.mdm.appmgt.manager" immediate="true"
 */
public class AppMgtMDMService {
    private static Log log = LogFactory.getLog(AppMgtMDMService.class);

    protected void activate(ComponentContext componentContext) {

        BundleContext bundleContext = componentContext.getBundleContext();
        bundleContext.registerService(MDMOperations.class.getName(),
                new MDMOperationsImpl(), null);

    }

}
