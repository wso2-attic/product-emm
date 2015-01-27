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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.util.Log;

/**
 * @author Kasun Dananjaya
 *
 */
public class ExecShell {

    private static String LOG_TAG = ExecShell.class.getName();

    public static enum SHELL_CMD {
        check_su_binary(new String[] {"/system/xbin/which","su"}),
        ;

        String[] command;

        SHELL_CMD(String[] command){
            this.command = command;
        }
    }

    public ArrayList<String> executeCommand(SHELL_CMD shellCmd){
        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;

        try {
            localProcess = Runtime.getRuntime().exec(shellCmd.command);
        } catch (Exception e) {
            return null;
            //e.printStackTrace();
        }

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));

        try {
            while ((line = in.readLine()) != null) {
                Log.d(LOG_TAG, "--> Line received: " + line);
                fullResponse.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "--> Full response was: " + fullResponse);

        return fullResponse;
    }

}