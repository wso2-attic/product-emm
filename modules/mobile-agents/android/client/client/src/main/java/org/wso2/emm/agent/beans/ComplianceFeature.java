package org.wso2.emm.agent.beans;

/**
 * This class represents the basic information of
 * the policy feature compliance
 */
public class ComplianceFeature {

    private String featureCode;
    private boolean compliance;
    private String message;

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public boolean isCompliance() {
        return compliance;
    }

    public void setCompliance(boolean compliance) {
        this.compliance = compliance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}