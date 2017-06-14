/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.qsg.utils;

/**
 * This class defines the constants used by the EMm-QSG package.
 */
public final class Constants {

    public static final class DeviceType {
        private DeviceType() {
            throw new AssertionError();
        }

        public static final String ANDROID = "android";
        public static final String WINDOWS = "windows";
        public static final String IOS = "ios";
    }

    public static final class ContentType {
        private ContentType() {
            throw new AssertionError();
        }

        public static final String APPLICATION_JSON = "application/json";
        public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    }

    public static final class Header {
        private Header() {
            throw new AssertionError();
        }

        public static final String AUTH = "Authorization";
        public static final String CONTENT_TYPE = "Content-Type";
    }

    public static final class HTTPStatus {
        private HTTPStatus() {
            throw new AssertionError();
        }

        public static final int OK = 200;
        public static final int CREATED = 201;
    }

    public static final String UTF_8 = "utf-8";
    public static final String EMM_USER_ROLE = "emm-user";
}