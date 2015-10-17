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

/**
 * Handlebar helpers to be used for frond-end templating
 */
(function () {
    var cache = {};
    $.template = function (name, location, callback) {
        var template = cache[name];
        if (!template) {
            $.get(location, function (data) {
                var compiledTemplate = Handlebars.compile(data);
                cache[name] = compiledTemplate;
                callback(compiledTemplate);
            });
        } else {
            callback(template);
        }
    };
    $.registerPartial = function (name, location, callback) {
            $.get(location, function (data) {
                Handlebars.registerPartial( name, data);
                console.log("Partial " + name + " has been registered");
                callback();
            });
    };
})();

Handlebars.registerHelper("equal", function (lvalue, rvalue, options) {
    if (arguments.length < 3)
        throw new Error("Handlebars Helper equal needs 2 parameters");
    if (lvalue != rvalue) {
        return options.inverse(this);
    } else {
        return options.fn(this);
    }
});

Handlebars.registerHelper("unequal", function (lvalue, rvalue, options) {
    if (arguments.length < 3)
        throw new Error("Handlebars Helper equal needs 2 parameters");
    if (lvalue == rvalue) {
        return options.inverse(this);
    } else {
        return options.fn(this);
    }
});
