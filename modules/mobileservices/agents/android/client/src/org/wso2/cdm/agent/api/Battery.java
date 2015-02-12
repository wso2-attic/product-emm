/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.api;

public class Battery {
	int scale = -1;
	int level = -1;
    int voltage = -1;
    int temp = -1;
	private static Battery battery = null;
	private Battery(){
		
	}
	public static Battery getInstance(){
		if(battery==null){
			battery = new Battery();
		}
		return battery;
	}
    
    public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getVoltage() {
		return voltage;
	}
	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	public int getTemp() {
		return temp;
	}
	public void setTemp(int temp) {
		this.temp = temp;
	}
	
}
