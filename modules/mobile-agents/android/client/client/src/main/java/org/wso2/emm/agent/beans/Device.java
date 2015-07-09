/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.agent.beans;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.wso2.emm.agent.AndroidAgentException;

import java.io.IOException;
import java.util.List;

/**
 * This class represents the basic information of
 * the carbon-device-mgt supported device
 */
public class Device {

	private String description;
	private String name;
	private String deviceIdentifier;
	private List<Property> properties;
	private EnrolmentInfo enrolmentInfo;

	public EnrolmentInfo getEnrolmentInfo() {
		return enrolmentInfo;
	}

	public void setEnrolmentInfo(EnrolmentInfo enrolmentInfo) {
		this.enrolmentInfo = enrolmentInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String toJSON() throws AndroidAgentException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(this);
		} catch (JsonMappingException e) {
			String errorMessage = "Error occurred while mapping class to json.";
			throw new AndroidAgentException(errorMessage, e);
		} catch (JsonGenerationException e) {
			String errorMessage = "Error occurred while generating json.";
			throw new AndroidAgentException(errorMessage, e);
		} catch (IOException e) {
			String errorMessage = "Error occurred while reading the stream.";
			throw new AndroidAgentException(errorMessage, e);
		}
	}

	//This has been added temporarily due to compatible with backend.
	public static class Property {

		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public static class EnrolmentInfo {
		private OwnerShip ownership;
		private java.lang.String owner;

		public enum OwnerShip {
			BYOD, COPE
		}

		public OwnerShip getOwnership() {
			return ownership;
		}

		public void setOwnership(OwnerShip ownership) {
			this.ownership = ownership;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}
	}

}
