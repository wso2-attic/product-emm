/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.operations;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;

/**
 * MetaTag data related to credentials.
 */
public class MetaTag {

    String format;
    String type;
    String nextNonce;

    public String getNextNonce() {
        return nextNonce;
    }

    public void setNextNonce(String nextNonce) {
        this.nextNonce = nextNonce;
    }

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
            Element format = doc.createElement(Constants.FORMAT);
            format.appendChild(doc.createTextNode(getFormat()));
            Attr attr = doc.createAttribute(Constants.XMLNS);
            attr.setValue(Constants.META_NAMESPACE);
            format.setAttributeNode(attr);
            meta.appendChild(format);
        }
        if (getType() != null) {
            Element type = doc.createElement(Constants.TYPE);
            type.appendChild(doc.createTextNode(getType()));
            Attr attr = doc.createAttribute(Constants.XMLNS);
            attr.setValue(Constants.META_NAMESPACE);
            type.setAttributeNode(attr);
            meta.appendChild(type);
        }
        if (getNextNonce() != null) {
            Element nextNonce = doc.createElement(Constants.NEXTNONCE);
            nextNonce.appendChild(doc.createTextNode(getNextNonce()));
            Attr attr = doc.createAttribute(Constants.XMLNS);
            attr.setValue(Constants.META_NAMESPACE);
            nextNonce.setAttributeNode(attr);
            meta.appendChild(nextNonce);
        }

    }
}
