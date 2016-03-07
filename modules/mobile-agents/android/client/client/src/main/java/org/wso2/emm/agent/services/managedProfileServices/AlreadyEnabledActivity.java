package org.wso2.emm.agent.services.managedProfileServices;

import android.app.Activity;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

import org.wso2.emm.agent.R;
import org.wso2.emm.agent.utils.Constants;

/**
 * Created by pasinduj on 3/1/16.
 */
public class AlreadyEnabledActivity extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managed_profile_enabled);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_sherlock_bar);
        getSupportActionBar().setTitle(Constants.EMPTY_STRING);
    }

}