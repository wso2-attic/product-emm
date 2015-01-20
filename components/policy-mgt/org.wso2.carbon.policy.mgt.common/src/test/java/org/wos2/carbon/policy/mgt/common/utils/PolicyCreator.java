/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wos2.carbon.policy.mgt.common.utils;

import org.wso2.carbon.policy.mgt.common.Feature;
import org.wso2.carbon.policy.mgt.common.Policy;

import java.util.ArrayList;
import java.util.List;

public class PolicyCreator {

    private static Policy policy = new Policy();

    public static Policy createPolicy() {

        Feature feature = new Feature();
        feature.setName("Camera");
        feature.setCode("502A");
        feature.setAttribute("disable");

        List<Feature> featureList = new ArrayList<Feature>();
        featureList.add(feature);

        policy.setFeaturesList(featureList);
        policy.setPolicyName("Camera_related_policy");

        return policy;
    }


}
