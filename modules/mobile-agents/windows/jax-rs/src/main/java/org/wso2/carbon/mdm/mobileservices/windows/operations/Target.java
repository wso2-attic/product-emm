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

import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Target details of syncml header's.
 */
public class Target {

    private String LocURI;
    private String LocName;

    public String getLocURI() {
        return LocURI;
    }

    public void setLocURI(String locURI) {
        LocURI = locURI;
    }

    public String getLocName() {
        return LocName;
    }

    public void setLocName(String locName) {
        LocName = locName;
    }

    public void buildTargetElement(Document doc, Element rootElement) {
        Element target = doc.createElement(Constants.TARGET);
        rootElement.appendChild(target);
        if (getLocURI() != null) {
            Element locURI = doc.createElement(Constants.LOC_URI);
            locURI.appendChild(doc.createTextNode(getLocURI()));
            target.appendChild(locURI);
        }
        if (getLocName() != null) {
            Element locName = doc.createElement(Constants.LOC_NAME);
            locName.appendChild(doc.createTextNode(getLocName()));
            target.appendChild(locName);
        }
    }
}
