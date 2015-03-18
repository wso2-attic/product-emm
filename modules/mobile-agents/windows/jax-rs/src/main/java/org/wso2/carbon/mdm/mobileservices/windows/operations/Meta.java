/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;

/**
 * Meta data related to credentials.
 */
public class Meta {

	String format;
	String type;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void buildMetaElement(Document doc, Element rootElement) {
		Element meta = doc.createElement(Constants.META);
		rootElement.appendChild(meta);
		if (getFormat() != null) {
			Element format= doc.createElement(Constants.FORMAT);
			format.appendChild(doc.createTextNode(getFormat()));

			Attr attr = doc.createAttribute(Constants.XMLNS);
			attr.setValue(Constants.META_NAMESPACE);
			format.setAttributeNode(attr);

			meta.appendChild(format);
		}
		if (getFormat() != null) {
			Element format= doc.createElement(Constants.TYPE);
			format.appendChild(doc.createTextNode(getType()));

			Attr attr = doc.createAttribute(Constants.XMLNS);
			attr.setValue(Constants.META_NAMESPACE);
			format.setAttributeNode(attr);

			meta.appendChild(format);
		}
	}

}
