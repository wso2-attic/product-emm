/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.app.catalog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.wso2.app.catalog.beans.Application;

public class ReadMoreInfoActivity extends Activity {

    private TextView txtAppName;
    private TextView txtDescription;
    private TextView txtNewFeatures;
    private TextView txtVersion;
    private TextView txtUpdatedOn;
    private LinearLayout layoutWhatsNew;
    private Context context;
    private Application application;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_more_info);

        Bundle extras = getIntent().getExtras();
        context = ReadMoreInfoActivity.this.getApplicationContext();

        if (extras != null) {
            if (getIntent().getSerializableExtra(context.getResources().
                    getString(R.string.intent_extra_application)) != null) {
                application = (Application) getIntent().getSerializableExtra(context.getResources().
                        getString(R.string.intent_extra_application));
            }
        }

        txtAppName = (TextView)findViewById(R.id.txtAppName);
        txtDescription = (TextView)findViewById(R.id.txtDescription);
        txtNewFeatures = (TextView)findViewById(R.id.txtNewFeatures);
        txtVersion = (TextView)findViewById(R.id.txtVersion);
        txtUpdatedOn = (TextView)findViewById(R.id.txtUpdatedOn);
        layoutWhatsNew = (LinearLayout)findViewById(R.id.layoutWhatsNew);
        btnBack = (ImageButton)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (application != null) {
            txtAppName.setText(application.getName());
            txtDescription.setText(Html.fromHtml(application.getDescription()));
            txtVersion.setText(application.getVersion());
            txtUpdatedOn.setText(application.getCreatedtime());
            if (application.getRecentChanges() != null) {
                txtNewFeatures.setText(Html.fromHtml(application.getRecentChanges()));
            } else {
                layoutWhatsNew.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
