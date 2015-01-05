/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.common;

import java.util.Properties;

public class OperationData {

    public enum Type {
        CONFIG, MESSAGE, STATE
    }

    private String name;
    private boolean state;
    private Properties properties;
    private String text;
    private Type type;

    public OperationData(String name, Type type) {
        this.type = type;
        this.name = name;
    }

    public OperationData(String name, boolean state) {
        this.name = name;
        this.type = Type.STATE;
        this.state = state;
    }

    public OperationData(String name, Properties properties) {
        this.name = name;
        this.type = Type.CONFIG;
        this.properties = properties;
    }

    public OperationData(String name, String text) {
        this.name = name;
        this.type = Type.MESSAGE;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public boolean getState() {
        return state;
    }

    public String getText() {
        return text;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

}
