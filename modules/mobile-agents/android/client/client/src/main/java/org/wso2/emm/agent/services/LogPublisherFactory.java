package org.wso2.emm.agent.services;

import android.content.Context;
import org.wso2.emm.agent.events.publisher.DataPublisher;
import org.wso2.emm.agent.events.publisher.HttpDataPublisher;
import org.wso2.emm.agent.events.publisher.SplunkLogPublisher;
import org.wso2.emm.agent.utils.Constants;

/**
 * This class produce the matching Log Manager according to the preferred publisher.
 */
public class LogPublisherFactory {
    private Context context;

    public LogPublisherFactory(Context context) {
        this.context = context;
    }

    public DataPublisher getLogPulisher() {
        if (Constants.LogPublisher.DAS_PUBLISHER.equals(Constants.LogPublisher.LOG_PUBLISHER_IN_USE)) {
            return new HttpDataPublisher();
        } else if (Constants.LogPublisher.SPLUNK_PUBLISHER.equals(Constants.LogPublisher.LOG_PUBLISHER_IN_USE)) {
            return new SplunkLogPublisher(context);
        } else {
            return null;
        }
    }
}
