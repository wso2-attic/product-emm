/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.agent.beans;


public class WifiProfile {

    public final static String SSID = "ssid";
    public final static String PASSWORD = "password";
    public final static String TYPE = "type";
    public final static String PHASE2 = "phase2";
    public final static String EAPMETHOD = "eap";
    public final static String PROVISIONING = "provisioning";
    public final static String IDENTITY = "identity";
    public final static String ANONYMOUSIDENTITY = "anonymousIdentity";
    public final static String CACERT = "cacert";
    public final static String CACERTNAME = "cacertName";

    public enum Type {

        NONE("none"), WEP("wep"), WPA("wpa"), EAP("802eap");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public void setType(String value){

        }

        public static Type getByValue(String value) {
            for(Type e: Type.values()) {
                if(e.value.equals(value)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }


    public enum EAPMethod {
        PEAP("peap"), TLS("tls"), TTLS("ttls"), PWD("pwd"), SIM("sim"), AKA("aka"), AKA2("aka2"), FAST("fast"), LEAP("leap");

        private String value;

        EAPMethod(String value) {
            this.value = value;
        }

        public static EAPMethod getByValue(String value) {
            for(EAPMethod e: EAPMethod.values()) {
                if(e.value.equals(value)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Phase2 {
        NONE("none"), PAP("pap"), MCHAP("mchap"), MCHAPV2("mchapv2"), GTC("gtc");

        private String value;

        Phase2(String value) {
            this.value = value;
        }

        public static Phase2 getByValue(String value) {
            for(Phase2 e: Phase2.values()) {
                if(e.value.equals(value)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    private String ssid;
    private String password;
    private Type type;
    private Phase2 phase2;
    private EAPMethod eapMethod;
    private int provisioning;
    private String identity;
    private String anonymousIdentity;
    private String caCert;
    private String caCertName;


    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Phase2 getPhase2() {
        return phase2;
    }

    public void setPhase2(Phase2 phase2) {
        this.phase2 = phase2;
    }

    public EAPMethod getEapMethod() {
        return eapMethod;
    }

    public void setEapMethod(EAPMethod eapMethod) {
        this.eapMethod = eapMethod;
    }

    public int getProvisioning() {
        return provisioning;
    }

    public void setProvisioning(int provisioning) {
        this.provisioning = provisioning;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getAnonymousIdentity() {
        return anonymousIdentity;
    }

    public void setAnonymousIdentity(String anonymousIdentity) {
        this.anonymousIdentity = anonymousIdentity;
    }

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }

    public String getCaCertName() {
        return caCertName;
    }

    public void setCaCertName(String caCertName) {
        this.caCertName = caCertName;
    }

}
