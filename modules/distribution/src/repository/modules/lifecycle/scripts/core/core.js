
/*
 * Copyright (c) WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The core namespace contains methods that load the lifecycle definitions from the registry
 * @namespace
 * @example
 *     var core = require('lifecycle').core;
 *     core.init(); //Should only be called once in the lifecycle of an app.Ideally in an init script
 * @requires store
 * @requires event
 * @requires utils
 * @requires Packages.org.wso2.carbon.governance.lcm.util.CommonUtil
 */
var core = {};
(function(core) {
    var CommonUtil = Packages.org.wso2.carbon.governance.lcm.util.CommonUtil;
    var LC_MAP = 'lc.map';
    var EMPTY = '';
    var log = new Log('lifecycle');
    var addRawLifecycle = function(lifecycleName, content, tenantId) {
        var lcMap = core.configs(tenantId);
        if (!lcMap.raw) {
            lcMap.raw = {};
        }
        lcMap.raw[lifecycleName] = new String(content);
    };
    var addJsonLifecycle = function(lifecycleName, definition, tenantId) {
        var lcMap = core.configs(tenantId);
        if (!lcMap.json) {
            lcMap.json = {};
        }
        lcMap.json[lifecycleName] = definition;
    };
    var getConfigRegistry = function(registry) {
        var rootReg = registry.registry;
        var configReg = rootReg.getChrootedRegistry('/_system/config');
        return configReg;
    };
    /**
     * Converts array references to properties.The JSON conversion produces some properties which need to be accessed
     * using array indexes.
     * @param  {Object} obj  Unaltered JSON object
     * @return {Object}      JSON object with resolved array references
     */
    var transformJSONLifecycle = function(obj) {
        obj.configuration = obj.configuration[0];
        obj.configuration.lifecycle = obj.configuration.lifecycle[0];
        obj.configuration.lifecycle.scxml = obj.configuration.lifecycle.scxml[0];
        var states = obj.configuration.lifecycle.scxml.state;
        var stateObj = {};
        var state;
        for (var index = 0; index < states.length; index++) {
            state = states[index];
            stateObj[state.id.toLowerCase()] = state;
            if (stateObj[state.id.toLowerCase()].datamodel) {
                stateObj[state.id.toLowerCase()].datamodel = stateObj[state.id.toLowerCase()].datamodel[0];
            }
        }
        obj.configuration.lifecycle.scxml.state = stateObj;
        return obj;
    };
    /*
    Creates an xml file from the contents of an Rxt file
    @rxtFile: An rxt file
    @return: An xml file
    */
    var createXml = function(content) {
        var fixedContent = content.replace('<xml version="1.0"?>', EMPTY).replace('</xml>', EMPTY);
        return new XML(fixedContent);
    };
    var parseLifeycle = function(content) {
        var ref = require('utils').xml;
        var obj = ref.convertE4XtoJSON(createXml(content));
        return obj;
    };
    var loadLifecycles = function(sysRegistry, tenantId) {
        var configReg = getConfigRegistry(sysRegistry);
        var lifecycleList = CommonUtil.getLifecycleList(configReg);
        var lifecycle;
        var content;
        for (var index in lifecycleList) {
            lifecycle = lifecycleList[index];
            log.debug('About to process raw lifecycle definition and convert to json for ' + lifecycle);
            //Obtain the definition 
            content = CommonUtil.getLifecycleConfiguration(lifecycle, configReg);
            //Store the raw lifecycle
            addRawLifecycle(lifecycle, content, tenantId);
            //Parse the raw lifecycle definition into a json
            var jsonLifecycle = parseLifeycle(new String(content));
            //Correct any array references
            jsonLifecycle = transformJSONLifecycle(jsonLifecycle);
            //Store the json lifecycle definition
            addJsonLifecycle(lifecycle, jsonLifecycle, tenantId);
            if(log.isDebugEnabled()){
                log.debug('Found lifecycle: ' + jsonLifecycle.name + ' tenant: ' + tenantId);
            }
        }
    };
    var init = function(tenantId) {
        var server = require('store').server;
        var sysRegistry = server.systemRegistry(tenantId);
        loadLifecycles(sysRegistry, tenantId);
    };
    core.force = function(tenantId) {
        init(tenantId);
    };
    /**
     * Initializes the logic which loads the lifecycle definitions on a per tenant basis
     * The loading of lifecycle definitions take place whenever a tenant is loaded
     */
    core.init = function() {
        var event = require('event');
        event.on('tenantLoad', function(tenantId) {
            init(tenantId);
        });
    };
    /**
     * Returns the lifecycle map which is stored in the application context 
     * The map is maintained on a per user basis
     * @param  {Number} tenantId  The tenant ID
     * @return {Object}           The lifecycle map
     */
    core.configs = function(tenantId) {
        var lcMap = application.get(LC_MAP);
        if (!lcMap) {
            log.debug('Creating lcMap in the application context');
            lcMap = {};
            application.put(LC_MAP, lcMap);
        }
        if (!lcMap[tenantId]) {
            log.debug('Creating lcMap for the tenant: ' + tenantId + ' in application context');
            lcMap[tenantId] = {};
        }
        return lcMap[tenantId];
    };
    /**
     * Returns the raw SCXML definition of the lifecycle
     * @param  {String} lifecycleName The name of the lifecycle for which the definition must be fetched
     * @param  {Number} tenantId      The tenant ID
     * @return {String}               The raw SCXML definition of the lifecycle 
     */
    core.getRawDef = function(lifecycleName, tenantId) {
        var lcMap = core.configs(tenantId);
        if (!lcMap) {
            throw 'There is no lifecycle information for the tenant: ' + tenantId;
        }
        if (!lcMap.raw) {
            throw 'There is no raw lifecycle information for the lifecycle: ' + lifecycleName + ' of tenant: ' + tenantId;
        }
        if (!lcMap.raw[lifecycleName]) {
            throw 'There is no lifecycle information for ' + lifecycleName;
        }
        return lcMap.raw[lifecycleName];
    };
    /**
     * Returns the JSON definition of the provided lifecycle for the given tenant
     * @param  {String} lifecycleName  The name of the cycle for which the definition must be returned
     * @param  {Number} tenantId       The tenant ID
     * @return {Object}                The JSON definitin of the lifecycle
     * @throws There is no lifcycle information for the tenant
     * @throws There is no json lifecycle information for the lifecycle of the tenant               
     */
    core.getJSONDef = function(lifecycleName, tenantId) {
        var lcMap = core.configs(tenantId);
        if (!lcMap) {
            throw 'There is no lifecycle information for the tenant: ' + tenantId;
        }
        if (!lcMap.json) {
            throw 'There is no json lifecycle information for the lifecycle: ' + lifecycleName + ' of tenant: ' + tenantId;
        }
        if (!lcMap.json[lifecycleName]) {
            throw 'There is no lifecycle information for ' + lifecycleName;
        }
        return lcMap.json[lifecycleName];
    };
    /**
     * Returns the list of all lifecycles that have been deployed to the Governance Registry
     * @param  {Number} tenantId  The tenants ID
     * @return {Array}            An array containing the names of all the lifecycles deployed to the Registry
     * @throws There is no lifecycle information for the tenant
     * @throws There is no json lifecycle information
     */
    core.getLifecycleList = function(tenantId) {
        var lcMap = core.configs(tenantId);
        if (!lcMap) {
            throw 'There is no lifecycle information for the tenant: ' + tenantId;
        }
        if (!lcMap.json) {
            throw 'There is no json lifecycle information' + tenantId;
        }
        var map = lcMap.json;
        var list = [];
        for (var i in map) {
            list.push(i);
        }
        return list;
    }
}(core));