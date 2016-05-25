package org.wso2.emm.agent.events.listeners;

import java.io.InputStream;

/**
 * Created by dilan on 5/24/16.
 */
public interface DeviceCertCreationListener {

    /**
     * This method will be called after the device certificate is created
     * @param certificate - Returns the input stream of the certificate
     */
    public void onDeviceCertCreated(InputStream certificate);
}
