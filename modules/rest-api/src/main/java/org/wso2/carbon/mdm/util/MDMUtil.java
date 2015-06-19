/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.mdm.util;

import org.wso2.carbon.mdm.beans.ProfileFeature;
import org.wso2.carbon.policy.mgt.common.Profile;

import java.util.ArrayList;
import java.util.List;

public class MDMUtil {

    public static Profile convertProfile(org.wso2.carbon.mdm.beans.Profile mdmProfile) {
        Profile profile = new Profile();
        profile.setTenantId(mdmProfile.getTenantId());
        profile.setCreatedDate(mdmProfile.getCreatedDate());
        profile.setDeviceType(mdmProfile.getDeviceType());

        List<org.wso2.carbon.policy.mgt.common.ProfileFeature> profileFeatures = new ArrayList<org.wso2.carbon.policy
                .mgt.common.ProfileFeature>();
        for(ProfileFeature mdmProfileFeature:mdmProfile.getProfileFeaturesList()){
            profileFeatures.add(convertProfileFeature(mdmProfileFeature));
        }
        profile.setProfileFeaturesList(profileFeatures);
        profile.setProfileId(mdmProfile.getProfileId());
        profile.setProfileName(mdmProfile.getProfileName());
        profile.setUpdatedDate(mdmProfile.getUpdatedDate());
        return profile;
    }

    public static org.wso2.carbon.policy.mgt.common.ProfileFeature convertProfileFeature(ProfileFeature
            mdmProfileFeature){

        org.wso2.carbon.policy.mgt.common.ProfileFeature profileFeature = new org.wso2.carbon.policy.mgt.common
                .ProfileFeature();

        profileFeature.setProfileId(mdmProfileFeature.getProfileId());
        profileFeature.setContent(mdmProfileFeature.getPayLoad());
        profileFeature.setDeviceTypeId(mdmProfileFeature.getDeviceTypeId());
        profileFeature.setFeatureCode(mdmProfileFeature.getFeatureCode());
        profileFeature.setId(mdmProfileFeature.getId());
        return profileFeature;

    }
}
