/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.mdm.services.android.bean.wrapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.dto.operation.mgt.ProfileOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OperationWrapper {

    public enum Type {
        CONFIG, MESSAGE, INFO, COMMAND, PROFILE, POLICY
    }

    public enum Status {
        IN_PROGRESS, PENDING, COMPLETED, ERROR
    }

    private String code;
    private Properties properties;
    private Type type;
    private int id;
    private Status status;
    private String receivedTimeStamp;
    private String createdTimeStamp;
    private boolean isEnabled;
    private String payLoad;
    private String operationResponse;
    private Object objPayLoad;
    private Object objOpResponse;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(String receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public String getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(String createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getPayLoad() {

        Gson gson = new Gson();
        if (this.type.equals(Operation.Type.POLICY)){
            List<ProfileOperation> profileOperations = (List<ProfileOperation>) objPayLoad;
            JsonElement element = gson.toJsonTree(profileOperations, new TypeToken<List<ProfileOperation>>() {}.getType());
            payLoad = element.toString();
        }else{

            this.payLoad =  gson.toJson(objPayLoad);
        }
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getOperationResponse() {
        Gson gson = new Gson();
        this.operationResponse =  gson.toJson(objOpResponse);
        return operationResponse;
    }

    public void setOperationResponse(String operationResponse) {
        this.operationResponse = operationResponse;
    }

    public Object getObjPayLoad() {
        return objPayLoad;
    }

    public void setObjPayLoad(Object objPayLoad) {
        this.objPayLoad = objPayLoad;
    }

    public Object getObjOpResponse() {
        return objOpResponse;
    }

    public void setObjOpResponse(Object objOpResponse) {
        this.objOpResponse = objOpResponse;
    }
}
