/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common;

/**
 * Constants class for Windows plugin. This class has inner classes for containing constants for
 * each service.
 */
public final class Constants {

    //Service endpoints
    public static final String DISCOVERY_SERVICE_ENDPOINT =
            "org.wso2.carbon.mdm.mobileservices.windows.services.discovery.DiscoveryService";
    public static final String CERTIFICATE_ENROLLMENT_SERVICE_ENDPOINT =
            "org.wso2.carbon.mdm.mobileservices.windows.services.wstep" +
            ".CertificateEnrollmentService";
    public static final String CERTIFICATE_ENROLLMENT_POLICY_SERVICE_ENDPOINT =
            "org.wso2.carbon.mdm.mobileservices.windows.services.xcep" +
            ".CertificateEnrollmentPolicyService";

    //Services' target namespaces
    public static final String DISCOVERY_SERVICE_TARGET_NAMESPACE =
            "http://schemas.microsoft.com/windows/management/2012/01/enrollment";
    public static final String DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE =
            "http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RSTRC";
    public static final String CERTIFICATE_ENROLLMENT_POLICY_SERVICE_TARGET_NAMESPACE =
            "http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy";

    //Certificate enrollment service urls and namespaces
    public static final String WS_TRUST_TARGET_NAMESPACE =
            "http://docs.oasis-open.org/ws-sx/ws-trust/200512";
    public static final String WS_SECURITY_TARGET_NAMESPACE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String SOAP_AUTHORIZATION_TARGET_NAMESPACE =
            "http://schemas.xmlsoap.org/ws/2006/12/authorization";

    //Certificate enrollment policy service urls and namespaces
    public static final String ENROLLMENT_POLICY_TARGET_NAMESPACE =
            "http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy";
    public static final String REQUEST_WRAPPER_CLASS_NAME =
            "com.microsoft.schemas.windows.pki._2009._01.enrollmentpolicy.GetPolicies";
    public static final String RESPONSE_WRAPPER_CLASS_NAME =
            "com.microsoft.schemas.windows.pki._2009._01.enrollmentpolicy.GetPoliciesResponse";

    //Servlet Context attributes names
    public static final String CONTEXT_WAP_PROVISIONING_FILE = "WAP_PROVISIONING_FILE";
    public static final String WINDOWS_PLUGIN_PROPERTIES = "WINDOWS_PLUGIN_PROPERTIES";

    //Message handler constants
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String SECURITY = "Security";

    //Web services media types
    public static final String SYNCML_MEDIA_TYPE = "application/vnd.syncml.dm+xml;charset=utf-8";

    /**
     * Discovery service related other constants
     */
    public final class Discovery {
        private Discovery() { throw new AssertionError(); }

        public static final String ENROLL_SUBDOMAIN = "https://EnterpriseEnrollment.";
        public static final String CERTIFICATE_ENROLLMENT_POLICY_SERVICE_URL =
                "/ENROLLMENTSERVER/PolicyEnrollmentWebservice" +
                ".svc";
        public static final String CERTIFICATE_ENROLLMENT_SERVICE_URL =
                "/ENROLLMENTSERVER/DeviceEnrollmentWebservice" +
                ".svc";
        public static final String ONPREMISE_CERTIFICATE_ENROLLMENT_POLICY =
                "/ENROLLMENTSERVER/ONPREMISE/" +
                "PolicyEnrollmentWebservice.svc";
        public static final String ONPREMISE_CERTIFICATE_ENROLLMENT_SERVICE_URL =
                "/ENROLLMENTSERVER/ONPREMISE/DeviceEnrollmentWebservice.svc";
        public static final String WAB_URL = "/mdm/login-agent";

    }

    /**
     * Certificate enrolment policy service related constants
     */
    public final class CertificateEnrolmentPolicy {
        private CertificateEnrolmentPolicy() { throw new AssertionError(); }

        public static final int MINIMAL_KEY_LENGTH = 2048;
        public static final int POLICY_SCHEMA = 3;
        public static final int HASH_ALGORITHM_OID_REFERENCE = 0;
        public static final int OID_REFERENCE = 0;
        public static final String OID = "1.3.14.3.2.29";
        public static final String OID_DEFAULT_NAME = "szOID_OIWSEC_sha1RSASign";
        public static final int OID_GROUP = 1;
        public static final int OID_REFERENCE_ID = 0;
    }

    /**
     * Certificate enrollment Service related constants
     */
    public final class CertificateEnrolment {
        private CertificateEnrolment () { throw new AssertionError(); }

        public static final String TOKEN_TYPE =
                "http://schemas.microsoft.com/5.0.0" +
                ".0/ConfigurationManager/Enrollment/DeviceEnrollmentToken";
        public static final String PARM = "parm";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String VALUE_TYPE =
                "http://schemas.microsoft.com/5.0.0" +
                ".0/ConfigurationManager/Enrollment/DeviceEnrollmentProvisionDoc";
        public static final String ENCODING_TYPE =
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0" +
                ".xsd#base64binary";
        public static final String WSO2_MDM_JKS_FILE = "wso2mdm.jks";
        public static final String CA_CERT = "cacert";
        public static final String X_509 = "X.509";
        public static final String PROPERTIES_XML = "properties.xml";
        public static final String WAP_PROVISIONING_XML = "wap-provisioning.xml";
        public static final String PROVIDER = "BC";
        public static final String ALGORITHM = "SHA1withRSA";
        public static final String JKS = "JKS";
        public static final String SECURITY = "Security";
        public static final String WSS_SECURITY_UTILITY =
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0" +
                ".xsd";
        public static final String TIMESTAMP_ID = "Id";
        public static final String TIMESTAMP_U = "u";
        public static final String TIMESTAMP = "Timestamp";
        public static final String TIMESTAMP_0 = "_0";
        public static final String CREATED = "Created";
        public static final String EXPIRES = "Expires";
        public static final String UTF_8 = "utf-8";
    }

    /**
     * SynclML service related constants
     */
    public final class SyncML {
        private SyncML() { throw new AssertionError(); }

        public static final String SYNCML_SOURCE = "Source";
        public static final String SYNCML_DATA = "Data";
        public static final String SYNCML_CMD = "Cmd";
        public static final String SYNCML_CHAL = "Chal";
        public static final String SYNCML_CMD_ID = "CmdID";
        public static final String SYNCML_CMD_REF = "CmdRef";
        public static final String SYNCML_MESSAGE_REF = "MsgRef";
        public static final String SYNCML_LOCATION_URI = "LocURI";
        public static final String SYNCML_TARGET_REF = "TargetRef";
    }

    /**
     * windows device constants
     */
    public final class DeviceConstants {
        private DeviceConstants() {
            throw new AssertionError();
        }

        public static final String DEVICE_ID_NOT_FOUND = "Device Id not found for device found at %s";
        public static final String DEVICE_ID_SERVICE_NOT_FOUND =
                "Issue in retrieving device management service instance for device found at %s";
    }

    /**
     * Device Operation codes
     */
    public final class OperationCodes {
        private OperationCodes() {
            throw new AssertionError();
        }

        public static final String DEVICE_LOCK = "DEVICE_LOCK";
        public static final String DISENROLL = "DISENROLL";
        public static final String DEVICE_RING = "DEVICE_RING";
        public static final String WIPE_DATA = "WIPE_DATA";
        public static final String ENCRYPT_STORAGE = "ENCRYPT_STORAGE";
        public static final String LOCK_RESET = "LOCKRESET";
        public static final String PIN_CODE = "LOCK_PIN";

    }

    public final class StatusCodes {
        private StatusCodes() {
            throw new AssertionError();
        }

        public static final int MULTI_STATUS_HTTP_CODE = 207;
    }

}
