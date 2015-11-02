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
 * Sequence data that use to execute tag list
 */
public class SequenceTag {

    int commandId;
    ExecuteTag exec;
    Get get;
    DeleteTag deleteTag;
    AtomicTag atomicTag;
    List<Replace> replaces;

    public DeleteTag getDeleteTag() {
        return deleteTag;
    }

    public void setDeleteTag(DeleteTag deleteTag) {
        this.deleteTag = deleteTag;
    }

    public List<Replace> getReplaces() {
        return replaces;
    }

    public void setReplaces(List<Replace> replaces) {
        this.replaces = replaces;
    }

    public AtomicTag getAtomicTag() {
        return atomicTag;
    }

    public void setAtomicTag(AtomicTag atomicTag) {
        this.atomicTag = atomicTag;
    }

    public ExecuteTag getExec() {
        return exec;
    }

    public void setExec(ExecuteTag exec) {
        this.exec = exec;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public Get getGet() {
        return get;
    }

    public void setGet(Get get) {
        this.get = get;
    }

    public void buildSequenceElement(Document doc, Element rootElement) {
        Element sequence = doc.createElement(Constants.SEQUENCE);
        rootElement.appendChild(sequence);
        if (getCommandId() != -1) {
            Element commandId = doc.createElement(Constants.COMMAND_ID);
            commandId.appendChild(doc.createTextNode(String.valueOf(getCommandId())));
            sequence.appendChild(commandId);
        }
        if (getExec() != null) {
            getExec().buildExecElement(doc, sequence);
        }
        if (getGet() != null) {
            getGet().buildGetElement(doc, sequence);
        }
        if (getReplaces() != null) {
            for (Iterator<Replace> replaceIterator = getReplaces().iterator(); replaceIterator.hasNext(); ) {
                Replace replace = replaceIterator.next();
                if (replace != null) {
                    replace.buildReplaceElement(doc, sequence);
                }
            }
        }
        if (getAtomicTag() != null) {
            getAtomicTag().buildAtomicElement(doc, sequence);
        }
        if (getDeleteTag() != null) {
            getDeleteTag().buildDeleteElement(doc, sequence);
        }
    }
}
