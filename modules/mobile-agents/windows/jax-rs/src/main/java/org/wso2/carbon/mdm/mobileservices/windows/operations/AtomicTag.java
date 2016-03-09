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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.Constants;

import java.util.Iterator;
import java.util.List;

/**
 * Wrapper for other SyncML elements.
 */
public class AtomicTag {
    int commandId = -1;
    List<AddTag> adds;
    List<ReplaceTag> replaces;

    public List<ReplaceTag> getReplaces() {
        return replaces;
    }

    public void setReplaces(List<ReplaceTag> replaces) {
        this.replaces = replaces;
    }

    public List<AddTag> getAdds() {
        return adds;
    }

    public void setAdds(List<AddTag> adds) {
        this.adds = adds;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public void buildAtomicElement(Document doc, Element rootElement) {
        Element atomic = doc.createElement(Constants.ATOMIC);
        if (getAdds() != null) {
            rootElement.appendChild(atomic);
            if (getCommandId() != -1) {
                Element commandId = doc.createElement(Constants.COMMAND_ID);
                commandId.appendChild(doc.createTextNode(String.valueOf(getCommandId())));
                atomic.appendChild(commandId);
            }
            for (Iterator<AddTag> addIterator = getAdds().iterator(); addIterator.hasNext(); ) {
                AddTag add = addIterator.next();
                if (add != null) {
                    add.buildAddElement(doc, atomic);
                }
            }
        }
        if (getReplaces() != null) {
            for (Iterator<ReplaceTag> replaceIterator = getReplaces().iterator(); replaceIterator.hasNext(); ) {
                ReplaceTag add = replaceIterator.next();
                if (add != null) {
                    add.buildReplaceElement(doc, atomic);
                }
            }
        }
    }
}
