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
package org.wso2.app.catalog.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.wso2.app.catalog.AppCatalogException;
import org.wso2.app.catalog.R;
import org.wso2.app.catalog.api.ApplicationManager;
import org.wso2.app.catalog.beans.Application;
import org.wso2.app.catalog.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter used in Applications view to generate applications list.
 */
public class ApplicationAdapter extends ArrayAdapter<Application> implements Filterable {

    private Activity activity;
    private int resource;
    private List<Application> applications;
    private List<Application> fullAppList;
    private ApplicationManager applicationManager;
    private static final String TAG = ApplicationAdapter.class.getName();
    private final int TAG_BTN_INSTALL = 0;
    private final int TAG_BTN_UNINSTALL = 1;

    public ApplicationAdapter(Activity activity, int resource, List<Application> objects) {
        super(activity, resource, objects);

        this.activity = activity;
        this.resource = resource;
        this.applications = objects;
        this.fullAppList = objects;
        this.applicationManager = new ApplicationManager(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final AppHolder holder;

        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new AppHolder();
            holder.imgAppIcon = (ImageView) row.findViewById(R.id.imgAppIcon);
            holder.txtAppName = (TextView) row.findViewById(R.id.txtAppName);
            holder.txtProvider = (TextView) row.findViewById(R.id.txtProvider);
            holder.txtRating = (TextView) row.findViewById(R.id.txtRating);
            holder.btnInstall = (TextView) row.findViewById(R.id.btnInstall);

            row.setTag(holder);
        } else {
            holder = (AppHolder) row.getTag();
        }

        final Application data = applications.get(position);

        if (data.getIcon() != null) {
            Picasso.with(activity).load(data.getIcon()).placeholder(R.drawable.app_icon).into(holder.imgAppIcon);
        }

        holder.txtAppName.setText(data.getName());
        holder.txtProvider.setText(data.getCategory());
        if (Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(data.getAppType().trim())) {
            holder.txtRating.setText(activity.getResources().getString(R.string.app_type_web_clip));
        } else {
            holder.txtRating.setText(activity.getResources().getString(R.string.app_type_enterprise));
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int iTag = (Integer) view.getTag();

                switch (iTag) {
                    case TAG_BTN_INSTALL:
                        if (Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(data.getAppType().trim())) {
                            try {
                                applicationManager.manageWebAppBookmark(data.getAppUrl(), data.getName(), activity.getResources().
                                        getString(R.string.operation_install));
                            } catch (AppCatalogException e) {
                                Log.e(TAG, "Cannot create Webclip due to invalid operation type." + e);
                            }
                        } else {
                            applicationManager.installApp(data.getAppUrl(), data.getPackageName());
                        }
                        break;
                    case TAG_BTN_UNINSTALL:
                        if (Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(data.getAppType().trim())) {
                            try {
                                applicationManager.manageWebAppBookmark(data.getAppUrl(), data.getName(), activity.getResources().
                                        getString(R.string.operation_uninstall));
                            } catch (AppCatalogException e) {
                                Log.e(TAG, "Cannot remove Webclip due to invalid operation type." + e);
                            }
                        } else {
                            applicationManager.uninstallApplication(data.getPackageName());
                        }
                        break;
                }
            }
        };

        if (applicationManager.isPackageInstalled(data.getPackageName())) {
            holder.btnInstall.setBackgroundColor(Color.parseColor(Constants.UNINSTALL_BUTTON_COLOR));
            holder.btnInstall.setText(activity.getResources().getString(R.string.action_uninstall));
            holder.btnInstall.setTag(TAG_BTN_UNINSTALL);
            holder.btnInstall.setOnClickListener(onClickListener);
        } else {
            holder.btnInstall.setBackgroundColor(Color.parseColor(Constants.INSTALL_BUTTON_COLOR));
            holder.btnInstall.setText(activity.getResources().getString(R.string.action_install));
            holder.btnInstall.setTag(TAG_BTN_INSTALL);
            holder.btnInstall.setOnClickListener(onClickListener);
        }

        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                applications = (ArrayList<Application>) results.values;
                if (applications.size() > 0) {
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Application> filteredApplications = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < fullAppList.size(); i++) {
                    String dataNames = fullAppList.get(i).getName();
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
                        filteredApplications.add(fullAppList.get(i));
                    }
                }

                results.count = filteredApplications.size();
                results.values = filteredApplications;

                return results;
            }
        };

        return filter;
    }

    static class AppHolder {
        ImageView imgAppIcon;
        TextView txtAppName;
        TextView txtProvider;
        TextView txtRating;
        TextView btnInstall;
    }

}