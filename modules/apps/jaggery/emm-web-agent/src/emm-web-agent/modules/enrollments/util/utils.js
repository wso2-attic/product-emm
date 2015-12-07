/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var methods;
methods = function () {
    var log = new Log("modules/enrollments/util/utils.js");

    var publicMethods = {};

    publicMethods.getResource = function (resourcePath) {
        var file = new File(resourcePath);
        var resource = null;
        try {
            file.open("r");
            resource = file.readAll();
        } catch (e) {
            log.error("Error in reading resource");
        } finally {
            file.close();
        }
        return resource;
    };

    return publicMethods;
}();