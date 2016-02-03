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
 * Class used to build syncml SequenceTag.
 */
public class SequenceTag {

    int commandId;
    ExecuteTag exec;
    GetTag get;
    DeleteTag deleteTag;
    AtomicTag atomicTag;
    List<ReplaceTag> replaces;

    public DeleteTag getDeleteTag() {
        return deleteTag;
    }

    public void setDeleteTag(DeleteTag deleteTag) {
        this.deleteTag = deleteTag;
    }

    public List<ReplaceTag> getReplaces() {
        return replaces;
    }

    public void setReplaces(List<ReplaceTag> replaces) {
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

    public GetTag getGet() {
        return get;
    }

    public void setGet(GetTag get) {
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
            for (Iterator<ReplaceTag> replaceIterator = getReplaces().iterator(); replaceIterator.hasNext(); ) {
                ReplaceTag replace = replaceIterator.next();
                if (replace != null) {
                    replace.buildReplaceElement(doc, sequence);
                }
            }
        }
        if (getDeleteTag() != null) {
            getDeleteTag().buildDeleteElement(doc, sequence);
        }
        if (getAtomicTag() != null) {
            getAtomicTag().buildAtomicElement(doc, sequence);
        }

    }
}
