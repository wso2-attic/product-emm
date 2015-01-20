/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.policy.evaluator;

import org.wso2.carbon.policy.evaluator.spi.PDPService;
import org.wso2.carbon.policy.mgt.common.Feature;
import org.wso2.carbon.policy.mgt.common.Policy;

import java.util.List;

public class PDPServiceImpl implements PDPService {
    @Override
    public List<Policy> getEffectivePolicyList(List<Policy> policies, List<String> roles, String deviceType) {
        return null;
    }

    @Override
    public List<Feature> getEffectiveFeatureList(List<Policy> policies, List<FeatureRules> featureRulesList) {
        return null;
    }
}
