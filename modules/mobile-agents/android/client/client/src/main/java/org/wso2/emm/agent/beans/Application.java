/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.emm.agent.beans;

/**
 * Represents application data.
 */
public class Application {

    private String packageName;
    private int pid;
    private int pss;
    private int uss;
    private int sharedDirty;
    private int cpu;

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPss() {
        return pss;
    }

    public void setPss(int pss) {
        this.pss = pss;
    }

    public int getUss() {
        return uss;
    }

    public void setUss(int uss) {
        this.uss = uss;
    }

    public int getSharedDirty() {
        return sharedDirty;
    }

    public void setSharedDirty(int sharedDirty) {
        this.sharedDirty = sharedDirty;
    }

}
