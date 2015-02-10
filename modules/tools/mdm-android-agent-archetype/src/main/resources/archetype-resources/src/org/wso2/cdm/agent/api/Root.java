/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.api;

import java.io.File;

import android.util.Log;

/**
 * @author Kasun Dananjaya
 * 
 */
public class Root {

    private static String LOG_TAG = Root.class.getName();

    /**
	*Returns true if the device is rooted (if any of the root methods returns true)
	*/
    public boolean isDeviceRooted() {
    	if (checkRootMethod3()){return true;}
    	if (checkRootMethod2()){return true;}
        if (checkRootMethod1()){return true;}
        
        
        return false;
    }

    /**
	*Returns true if the OS build tags contains "test-keys"
	*/
    public boolean checkRootMethod1(){
        String buildTags = android.os.Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")) {
        	Log.e("ROOT CHECKER", "ROOT METHOD 1");
            return true;
        }
        return false;
    }
    /**
	*Returns true if the device contains SuperUser.apk which is stored into the device in the rooting process
	*/
    public boolean checkRootMethod2(){
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
            	Log.e("ROOT CHECKER", "ROOT METHOD 2");
                return true;
                
            }
        } catch (Exception e) { }

        return false;
    }
    /**
	*Executes a shell command (superuser access with su binary) and returns true if the command succeeds
	*/
    public boolean checkRootMethod3() {
        if (new ExecShell().executeCommand(ExecShell.SHELL_CMD.check_su_binary) != null){
        	Log.e("ROOT CHECKER", "ROOT METHOD 3");
            return true;
        }else{
            return false;
        }
    }
}