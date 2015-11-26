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

describe('User Module', function () {
    var log = new Log();
    var userModule = require("/modules/user.js").userModule;
    var constants = require("/modules/constants.js");
    function tearUp() {
        session.put(constants.USER_SESSION_KEY, {"username" : "admin", "domain": "carbon.super", "tenantId": "-1234"});
    }

    function tearDown() {
        deleteData();
        session.put(constants.USER_SESSION_KEY, null);
    }

    function deleteData(){
    }

    it('Get user', function () {
        try {
            tearUp();
            var response = userModule.getUser("admin");
            expect(response.status).toBe("success");
        } catch (e) {
            log.error(e);
            throw e;
        } finally {
            tearDown();
        }
    });
    /*
     Not testable since the functionality moved to front end
    it('Create user', function () {
        try {
            tearUp();
            var statusCode = userModule.addUser("wso2user", "wso2", "corp", "wso2-no-reply@wso2.com", ["admin"]);
            expect(statusCode).toBe(201);
        } catch (e) {
            log.error(e);
            throw e;
        } finally {
            tearDown();
        }
    });

    it('Update user', function () {
        try {
            tearUp();
            var results = userModule.updat
            expect(results.length).not.toBe(0);
        } catch (e) {
            log.error(e);
            throw e;
        } finally {
            tearDown();
        }
    });

    it('List all users', function () {
        try {
            tearUp();
            var results = userModule.getUsers();
            expect(results.length).not.toBe(0);
        } catch (e) {
            log.error(e);
            throw e;
        } finally {
            tearDown();
        }
    });
    it('Check permission for user', function () {
        try {
            tearUp();
            expect(userModule.isAuthorized("/permission/device-mgt/user/devices/list")).toBe(true);
        } catch (e) {
            log.error(e);
            throw e;
        } finally {
            tearDown();
        }
    });
    */
});