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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.wso2.app.catalog.api.ApplicationManager;
import org.wso2.app.catalog.beans.Application;
import org.wso2.app.catalog.utils.Constants;

public class AppDetailsActivity extends Activity {

    private final int TAG_BTN_INSTALL = 0;
    private final int TAG_BTN_READ_MORE = 1;
    private final int TAG_BTN_BACK = 2;
    private final int TAG_BTN_UNINSTALL = 3;
    private static final String TAG = AppDetailsActivity.class.getName();
    private ApplicationManager applicationManager;
    private Application application;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        context = AppDetailsActivity.this.getApplicationContext();
        applicationManager = new ApplicationManager(context);

        if (getIntent().getSerializableExtra(context.getResources().
                    getString(R.string.intent_extra_application)) != null) {
                application = (Application) getIntent().getSerializableExtra(context.getResources().
                        getString(R.string.intent_extra_application));
        }

        ImageView imgBanner = (ImageView)findViewById(R.id.imgBanner);
        ImageView imgAppIcon = (ImageView)findViewById(R.id.imgAppIcon);
        TextView txtAppName = (TextView)findViewById(R.id.txtAppName);
        TextView txtProvider = (TextView)findViewById(R.id.txtProvider);
        TextView txtDescription = (TextView)findViewById(R.id.txtDescription);
        TextView txtAppHeading = (TextView)findViewById(R.id.txtAppHeading);
        ImageView imgScreenshot1 = (ImageView)findViewById(R.id.imgScreenshot1);
        ImageView imgScreenshot2 = (ImageView)findViewById(R.id.imgScreenshot2);
        ImageView imgScreenshot3 = (ImageView)findViewById(R.id.imgScreenshot3);

        ImageButton btnBack = (ImageButton)findViewById(R.id.btnBack);
        btnBack.setTag(TAG_BTN_BACK);
        btnBack.setOnClickListener(onClickListener);

        Button btnInstall = (Button)findViewById(R.id.btnInstall);
        btnInstall.setTag(TAG_BTN_INSTALL);
        btnInstall.setOnClickListener(onClickListener);

        TextView btnReadMore = (TextView)findViewById(R.id.btnReadMore);
        btnReadMore.setTag(TAG_BTN_READ_MORE);
        btnReadMore.setOnClickListener(onClickListener);

        if (application != null) {
            txtAppName.setText(application.getName());
            txtDescription.setText(application.getDescription());
            txtProvider.setText(application.getCategory());
            txtAppHeading.setText(application.getName());
            Picasso.with(context).load(application.getBanner()).into(imgBanner);
            Picasso.with(context).load(application.getIcon()).into(imgAppIcon);

            if (application.getScreenshots() != null) {
                if (application.getScreenshots().size() > 0 && application.getScreenshots().get(0) != null) {
                    Picasso.with(context).load(application.getScreenshots().get(0)).into(imgScreenshot1);
                }

                if (application.getScreenshots().size() > 1 &&  application.getScreenshots().get(1) != null) {
                    Picasso.with(context).load(application.getScreenshots().get(1)).into(imgScreenshot2);
                }

                if (application.getScreenshots().size() > 2 && application.getScreenshots().get(2) != null) {
                    Picasso.with(context).load(application.getScreenshots().get(2)).into(imgScreenshot3);
                }
            }

            if (applicationManager.isPackageInstalled(application.getPackageName())) {
                btnInstall.setBackgroundColor(Color.parseColor(Constants.UNINSTALL_BUTTON_COLOR));
                btnInstall.setText(context.getResources().getString(R.string.action_uninstall));
                btnInstall.setTag(TAG_BTN_UNINSTALL);
                btnInstall.setOnClickListener(onClickListener);
            } else {
                btnInstall.setBackgroundColor(Color.parseColor(Constants.INSTALL_BUTTON_COLOR));
                btnInstall.setText(context.getResources().getString(R.string.action_install));
                btnInstall.setTag(TAG_BTN_INSTALL);
                btnInstall.setOnClickListener(onClickListener);
            }
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            int iTag = (Integer) view.getTag();

            switch (iTag) {
                case TAG_BTN_INSTALL:
                    if(Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(application.getAppType().trim())) {
                        try {
                            applicationManager.manageWebAppBookmark(application.getAppUrl(), application.getName(),
                                                        context.getResources().getString(R.string.operation_install));
                        } catch (AppCatalogException e) {
                            Log.e(TAG, "Cannot create WebClip due to invalid operation type." + e);
                        }
                    } else {
                        applicationManager.installApp(application.getAppUrl(), application.getPackageName());
                    }
                    break;
                case TAG_BTN_READ_MORE:
                    Intent intent = new Intent(context, ReadMoreInfoActivity.class);
                    intent.putExtra(context.getResources().
                            getString(R.string.intent_extra_application), application);
                    startActivity(intent);
                    break;
                case TAG_BTN_BACK:
                    onBackPressed();
                    break;
                case TAG_BTN_UNINSTALL:
                    if(Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(application.getAppType().trim())) {
                        try {
                            applicationManager.manageWebAppBookmark(application.getAppUrl(), application.getName(),
                                                        context.getResources().getString(R.string.operation_uninstall));
                        } catch (AppCatalogException e) {
                            Log.e(TAG, "Cannot remove Webclip due to invalid operation type." + e);
                        }
                    } else {
                        applicationManager.uninstallApplication(application.getPackageName());
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
