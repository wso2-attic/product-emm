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
 * The api namespace exposes methods to retrieve information individual states of the lifecycles
 * deployed to the Governance Registry
 * @namespace
 * @example
 *     var api = require('lifecycle').api;
 *     var superTenantId=-1234;
 *
 *     api.getLifecycleList(superTenantId);
 * @requires store
 * @requires event
 * @requires utils
 * @requires Packages.org.wso2.carbon.governance.lcm.util.CommonUtil
 */
var api = {};
(function (api, core) {
    var log = new Log('lifecycle');
    var CHECKITEM_TOKEN = 'checkItems';
    var TRANSITION_EXECUTION = 'transitionExecution';

    /**
     * Represents a class which models a lifecycle
     * @constructor
     * @param {Object} definiton The JSON definition of a lifecycle
     * @memberOf api
     */
    function Lifecycle(definiton) {
        this.definition = definiton;
    }

    /**
     * Returns the JSON definition for the lifecycle managed by the instance
     * @return {Object} Lifecycle definition
     */
    Lifecycle.prototype.getDefinition = function () {
        return this.definition;
    };

    /**
     * Returns the name of the lifecycle
     * @return {String} The name of the lifecycle
     */
    Lifecycle.prototype.getName = function () {
        if (!this.definition.name) {
            throw 'Unable to locate name attribute in the lifecycle definition ';
        }
        return this.definition.name;
    };

    /**
     * Returns a list of states which can be reached from the provided state
     * @example
     *     var states =  lc.nextStates('Initial');
     *
     *     for(var i =0 ;i< states.length; i++){
     *         print(states[i].target);
     *         print(states[i].event);
     *     }
     *
     * @return {Array}  The list of states reachable from the provided state
     * @throws The lifecycle does not have any state information.Make sure that the states are defined in the scxml definition
     * @throws The state is not present in the lifecycle
     * @throws The stae has defined any transitions in the lifecycle
     */
    Lifecycle.prototype.nextStates = function (currentStateName) {
        var currentStateName = currentStateName ? currentStateName.toLowerCase() : currentStateName;
        var states = this.definition.configuration.lifecycle.scxml.state;
        var nextStates = [];
        if (!states) {
            throw 'The lifecycle : ' + this.getName() + ' does not have any state information.Make sure that the states are defined in the scxml definition.';
        }
        if (!states[currentStateName]) {
            throw 'The state: ' + currentStateName + ' is not present in the lifecycle: ' + this.getName();
        }
        if (!states[currentStateName].transition) {
            throw 'The state: ' + currentStateName + ' has not defined any transitions in the lifecycle: ' + this.getName();
        }
        var transitions = states[currentStateName].transition;
        for (var index = 0; index < transitions.length; index++) {
            var transition = {};
            transition.state = transitions[index].target;
            transition.action = transitions[index].event;
            nextStates.push(transition);
        }
        return nextStates;
    };

    /**
     * Returns the check list items for a state.If there are no check list items defined for a state an empty array is returned
     * @param  {String} currentStateName The name of the state
     * @return {Array}       The list of check list items
     */
    Lifecycle.prototype.checklistItems = function (currentStateName) {
        var currentStateName = currentStateName ? currentStateName.toLowerCase() : currentStateName;
        var states = this.definition.configuration.lifecycle.scxml.state;
        var checklistItems = [];
        try {
            var datamodel = states[currentStateName].datamodel.data;
            for (var index = 0; index < datamodel.length; index++) {
                if (datamodel[index].name == 'checkItems') {
                    checklistItems = datamodel[index].item;
                    return checklistItems;
                }
            }
        } catch (e) {
            log.error(e);
        }
        return checklistItems;
    };

    /**
     * Builds a state object given raw state information
     * @param  {String} rawState The state definitin
     * @return {Object}           Processed state definition including check list items
     */
    var buildStateObject = function (rawState) {
        var state = {};
        state.id = rawState.id;
        if (!rawState.datamodel) {
            log.warn('Unable to read data model of the state ');
            return state;
        }
        var checkItems = getCheckitems(rawState.datamodel.data || {});
        state.checkItems = checkItems;
        return state;
    };

    /**
     * Returns the check list items by processing a state meta block
     * @param  {Object} data A state meta block
     * @return {Array}      Check list items
     */
    var getCheckitems = function (data) {
        var items = [];
        for (var index in data) {
            item = data[index];
            //There can be multiple checklist items define din a data model
            if (item.name == CHECKITEM_TOKEN) {
                return item.item || [];
            }
        }
        return items;
    };

    /**
     * Returns details about the state
     * @example
     *     var state = lc.state('Initial');
     *
     *     print(state.id); //Initial
     *     print(state.checkItems);
     *
     * @param  {String} stateName The name of the state
     * @return {Object}     Details about the state
     */
    Lifecycle.prototype.state = function (stateName) {
        //Convert the state to lowercase
        var stateName = stateName ? stateName.toLowerCase() : stateName;
        var states = this.definition.configuration.lifecycle.scxml.state;
        var state = {};
        if (!states) {
            throw 'The lifecycle : ' + this.getName() + ' does not have any state information.Make sure that the states are defined in the scxml definition.';
        }
        if (!states[stateName]) {
            log.warn('The state: ' + stateName + ' is not present in the lifecycle: ' + this.getName());
            return state;
        }
        var rawState = states[stateName];
        //Process the raw state 
        state = buildStateObject(rawState);
        //Add the next states
        state.nextStates = this.nextStates(stateName);
        return state;
    };

    /**
     * Returns the action that will cause a transition from the fromState to the toState.If there is no
     * transition action then NULL is returned
     * @param  {String} fromState The initial state before the transition
     * @param  {String} toState   The desired state of the transition
     * @return {String}           The action which will cause a transition
     */
    Lifecycle.prototype.transitionAction = function (fromState, toState) {
        var fromState = fromState ? fromState.toLowerCase() : fromState;
        var toState = toState ? toState.toLowerCase() : toState;
        //Get the list of states that can be reached from the fromState
        var states = this.nextStates(fromState);
        if (states.length == 0) {
            log.warn('There is no way to move from ' + fromState + ' to ' + toState + ' in lifecycle: ' + this.getName());
            return null;
        }
        for (var index = 0; index < states.length; index++) {
            if (states[index].state.toLowerCase() == toState.toLowerCase()) {
                return states[index].action;
            }
        }
        log.warn('There is no transition action to move from ' + fromState + ' to ' + toState + ' in lifecycle: ' + this.getName());
        return null;
    };

    /**
     * Returns the execution events that are triggered by the action for the state
     * @param  {String} state The state name
     * @param  {String} action The transition action
     * @return {Array}       An array of transition events
     */
    Lifecycle.prototype.transitionExecution = function (state, action) {
        var state = state ? state.toLowerCase() : state;
        var action = action ? action.toLowerCase() : action;
        var states = this.definition.configuration.lifecycle.scxml.state;
        var parameters = [];
        if (!states) {
            throw 'The lifecycle : ' + this.getName() + ' does not have any state information.Make sure that the states are defined in the scxml definition.';
        }
        if (!states[state]) {
            log.warn('The state: ' + state + ' is not present in the lifecycle: ' + this.getName());
            return parameters;
        }
        var data = states[state].datamodel.data;
        if (!data) {
            log.warn('The lifecycle: ' + this.getName() + ' does not have a data property declared.Unable to obtain execution events.');
            return parameters;
        }
        var item;
        for (var index in data) {
            item = data[index];
            if (item.name == 'transitionExecution') {
                var executions = item.execution;
                var execution;
                if (executions) {
                    //Look for the event triggered by the provided action
                    for (var exIndex in executions) {
                        execution = executions[exIndex];
                        //Check if the event matches the action
                        if (execution.forEvent.toLowerCase() == action) {
                            parameters = execution.parameter || [];
                            return parameters;
                        }
                    }
                }
            }
        }
        return parameters;
    };

    /**
     * Returns an instance of the Lifecycle class
     * @example
     *     var lc = api.getLifecycle('SimpleLifeCycle',-1234);
     *     lc.nextStates('initial');
     * @param  {String} lifecycleName The name of the lifecycle
     * @param  {Number} tenantId       The tenant ID
     * @return {Object}                An instance of the Lifecycle class
     * @throws Unable to locate lifecycle without a tenant ID
     */
    api.getLifecycle = function (lifecycleName, tenantId) {
        if (!tenantId) {
            throw 'Unable to locate lifecycle ' + lifecycleName + ' without a tenantId';
        }
        var lcJSON = core.getJSONDef(lifecycleName, tenantId);
        if (!lcJSON) {
            log.warn('Unable to locate lifecycle ' + lifecycleName + ' for the tenant: ' + tenantId);
            return null; //TODO: This should throw an exception
        }
        return new Lifecycle(lcJSON);
    };

    /**
     * Returns a list of lifecycles that can be accessed by the tenant
     * @example
     *     var lifecycles = api.getLifecycleList(-1234);
     *     print(lifecycles); // ['SampleLifeCycle','MobileAppLifeCycle']
     * @param  {Number} tenantId  The tenant ID
     * @return {Array}       An array of lifecycle names
     */
    api.getLifecycleList = function (tenantId) {
        if (!tenantId) {
            throw 'Unable to locate lifecycle without a tenantId';
        }
        var lcList = core.getLifecycleList(tenantId);
        if (!lcList) {
            throw 'Unable to locate lifecycles for the tenant: ' + tenantId;
        }
        return lcList;
    };
}(api, core));