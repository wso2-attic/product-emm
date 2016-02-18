/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.operations.AddTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ItemTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.MetaTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.TargetTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains utility methods which are used while creating Syncml Messages.
 */
public class TagUtil {
    /**
     * Build syncml AddTag for Device response message.
     *
     * @param operation Policy operation
     * @param data      Configuration service provider(CSP) data value 1/0
     * @return Syncml AddTag type object.
     */
    public static AddTag buildAddTag(Operation operation, String data) {
        TargetTag target = new TargetTag();
        MetaTag meta = new MetaTag();
        AddTag add = new AddTag();

        List<ItemTag> itemTags = new ArrayList<>();
        ItemTag itemTag = new ItemTag();
        itemTag.setTarget(target);
        itemTag.setMeta(meta);
        itemTag.setData(data);
        itemTags.add(itemTag);
        add.setCommandId(operation.getId());
        add.setItems(itemTags);
        return add;
    }
}
