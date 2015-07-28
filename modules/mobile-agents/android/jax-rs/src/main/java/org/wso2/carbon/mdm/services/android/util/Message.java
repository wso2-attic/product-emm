/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android.util;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {

    private String responseCode;
    private String responseMessage;

    @XmlElement
    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @XmlElement
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

	private Message.MessageBuilder getBuilder() {
		return new Message.MessageBuilder();
	}

	public static Message.MessageBuilder responseCode(String responseCode) {
		Message message = new Message();
		return message.getBuilder().responseCode(responseCode);
	}

	public static Message.MessageBuilder responseMessage(String responseMessage) {
		Message message = new Message();
		return message.getBuilder().responseMessage(responseMessage);
	}

	public class MessageBuilder {

		private String responseCode;
		private String responseMessage;


		public MessageBuilder responseCode(String responseCode) {
			this.responseCode = responseCode;
			return this;
		}

		public MessageBuilder responseMessage(String responseMessage) {
			this.responseMessage = responseMessage;
			return this;
		}

		public Message build() {
			Message message = new Message();
			message.setResponseCode(responseCode);
			message.setResponseMessage(responseMessage);
			return message;
		}
	}
}
