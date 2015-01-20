/*
*  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.policy.evaluator;

import org.wso2.carbon.policy.evaluator.utils.Constants;
import org.wso2.carbon.policy.mgt.common.Feature;
import org.wso2.carbon.policy.mgt.common.Policy;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for evaluating the policy (Configurations sets) and returning
 * the effective features set.
 */

public class FeatureFilterImpl implements FeatureFilter {

    /**
     * This method returns the effective feature list when policy list and feature aggregation rules are supplied.
     * @param policyList
     * @param featureRulesList
     * @return
     */
    @Override
    public List<Feature> evaluate(List<Policy> policyList, List<FeatureRules> featureRulesList) {
        return evaluateFeatures(extractFeatures(policyList), featureRulesList);
     }

    /**
     * This method extract the features from the given policy list in the order they are provided in the list.
     * @param policyList
     * @return
     */
    public List<Feature> extractFeatures(List<Policy> policyList) {
        List<Feature> featureList = new ArrayList<Feature>();
        for (Policy policy : policyList) {
            featureList.addAll(policy.getFeaturesList());
        }
        return featureList;
    }

    /**
     * This method is responsible for supplying tasks to other methods to evaluate given features.
     * @param featureList
     * @param featureRulesList
     * @return
     */
    public List<Feature> evaluateFeatures(List<Feature> featureList, List<FeatureRules> featureRulesList) {
        List<Feature> effectiveFeatureList = new ArrayList<Feature>();
        for (FeatureRules rule : featureRulesList) {
            String ruleName = rule.getEvaluationCriteria();
            String featureName = rule.getName();
            if (ruleName.equalsIgnoreCase(Constants.DENY_OVERRIDES)) {
                getDenyOverridesFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.PERMIT_OVERRIDES)) {
                getPermitOverridesFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.FIRST_APPLICABLE)) {
                getFirstApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.LAST_APPLICABLE)) {
                getLastApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.ALL_APPLICABLE)) {
                getAllApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.HIGHEST_APPLICABLE)) {
                getHighestApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
            if (ruleName.equalsIgnoreCase(Constants.LOWEST_APPLICABLE)) {
                getLowestApplicableFeatures(featureName, featureList, effectiveFeatureList);
            }
        }
        return effectiveFeatureList;
    }

    /**
     * This method picks up denied features, if there is no denied features it will add to the list, the final permitted feature.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getDenyOverridesFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        Feature evaluatedFeature = null;
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                if (feature.getRuleValue().equalsIgnoreCase("Deny")) {
                    evaluatedFeature = feature;
                    effectiveFeatureList.add(evaluatedFeature);
                    return;
                } else {
                    evaluatedFeature = feature;
                }
            }
        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }

    }

    /**
     * This method picks up permitted features, if there is no permitted features it will add to the list, the final denied feature.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getPermitOverridesFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        Feature evaluatedFeature = null;
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                if (feature.getRuleValue().equalsIgnoreCase("Permit")) {
                    evaluatedFeature = feature;
                    effectiveFeatureList.add(evaluatedFeature);
                    return;
                } else {
                    evaluatedFeature = feature;
                }
            }
        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }

    }

    /**
     * This method picks the first features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getFirstApplicableFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                effectiveFeatureList.add(feature);
                return;

            }
        }
    }

    /**
     * This method picks the last features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getLastApplicableFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        Feature evaluatedFeature = null;
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                evaluatedFeature = feature;
            }
        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }

    /**
     * This method picks the all features of the give type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getAllApplicableFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                effectiveFeatureList.add(feature);
            }
        }
    }

    /**
     * This method picks the feature with the highest value of given type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getHighestApplicableFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        Feature evaluatedFeature = null;
        int intValve = 0;
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                if (Integer.parseInt(feature.getRuleValue()) > intValve) {
                    intValve = Integer.parseInt(feature.getRuleValue());
                    evaluatedFeature = feature;
                }
            }
        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }

    /**
     * This method picks the feature with the lowest value of given type.
     * But if given policies do not have features of given type, it will not add anything.
     *
     * @param featureName
     * @param featureList
     * @param effectiveFeatureList
     */
    public void getLowestApplicableFeatures(String featureName, List<Feature> featureList, List<Feature> effectiveFeatureList) {
        Feature evaluatedFeature = null;
        int intValve = 0;
        for (Feature feature : featureList) {
            if (feature.getName().equalsIgnoreCase(featureName)) {
                if (Integer.parseInt(feature.getRuleValue()) < intValve) {
                    intValve = Integer.parseInt(feature.getRuleValue());
                    evaluatedFeature = feature;
                }
            }
        }
        if (evaluatedFeature != null) {
            effectiveFeatureList.add(evaluatedFeature);
        }
    }
}
