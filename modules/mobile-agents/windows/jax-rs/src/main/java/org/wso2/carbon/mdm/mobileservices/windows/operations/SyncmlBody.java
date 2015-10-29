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
 * Represents the body details of a syncml.
 */
public class SyncmlBody {
    Get getCommands;
    List<ExecuteTag> exec;
    List<Status> status;
    Alert alert;
    Replace replace;
    Results results;
    SequenceTag sequence;
    AtomicTag atomicTag;

    public AtomicTag getAtomicTag() {
        return atomicTag;
    }

    public void setAtomicTag(AtomicTag atomicTag) {
        this.atomicTag = atomicTag;
    }

    public SequenceTag getSequence() {
        return sequence;
    }

    public void setSequence(SequenceTag sequence) {
        this.sequence = sequence;
    }

    public List<ExecuteTag> getExec() {
        return exec;
    }

    public void setExec(List<ExecuteTag> exec) {
        this.exec = exec;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public Replace getReplace() {
        return replace;
    }

    public void setReplace(Replace replace) {
        this.replace = replace;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Get getGet() {
        return getCommands;
    }

    public void setGet(Get get) {
        this.getCommands = get;
    }

    public void buildBodyElement(Document doc, Element rootElement) {

        Element syncBody = doc.createElement(Constants.SYNC_BODY);
        rootElement.appendChild(syncBody);
        if (getStatus() != null) {
            for (int x = 0; x < getStatus().size(); x++) {
                if (getStatus().get(x) != null) {
                    getStatus().get(x).buildStatusElement(doc, syncBody);
                }
            }
        }
        if (getAlert() != null) {
            getAlert().buildAlertElement(doc, syncBody);
        }
        if (getResults() != null) {
            getResults().buildResultElement(doc, syncBody);
        }
        if (getGet() != null) {
            getGet().buildGetElement(doc, syncBody);
        }
        if (getReplace() != null) {
            getReplace().buildReplaceElement(doc, syncBody);
        }
        if (getExec() != null) {
            for (Iterator<ExecuteTag> execIterator = getExec().iterator(); execIterator.hasNext(); ) {
                ExecuteTag exec = execIterator.next();
                if (exec != null) {
                    exec.buildExecElement(doc, syncBody);
                }
            }
        }
        if (getSequence() != null) {
            getSequence().buildSequenceElement(doc, syncBody);
        }
        if (getAtomicTag() != null) {
            getAtomicTag().buildAtomicElement(doc, syncBody);
        }
        syncBody.appendChild(doc.createElement(Constants.FINAL));
    }
}
