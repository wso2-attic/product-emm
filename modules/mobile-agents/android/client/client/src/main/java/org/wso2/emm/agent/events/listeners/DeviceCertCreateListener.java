package org.wso2.emm.agent.events.listeners;

import java.io.InputStream;

/**
 * Created by dilan on 5/24/16.
 */
public interface DeviceCertCreateListener {

    public void onDeviceCertCreated(InputStream certificate);
}
