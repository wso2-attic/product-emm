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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for generate security token for client and server.
 */
public class SyncmlCredentials {

    public String generateCredData(String nextNonce) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        MessageDigest digest;
        String usrPwdNonceHash;
        String nonce;
        try {
            nonce = new String(Base64.decodeBase64(nextNonce), Constants.UTF_8);
            digest = MessageDigest.getInstance(Constants.MD5);
            String usrPwd = Constants.PROVIDER_ID + ":" + Constants.SERVER_SECRET;
            String usrPwdHash = Base64.encodeBase64String(digest.digest(usrPwd.getBytes(Constants.UTF_8)));
            String usrPwdNonce = usrPwdHash + ":" + nonce;
            usrPwdNonceHash = Base64.encodeBase64String(digest.digest(usrPwdNonce.getBytes(Constants.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("Problem occurred in encoding credentials data.");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Problem occurred in generating password hash.", e);
        }
        return usrPwdNonceHash;
    }

    public String generateRST(String username, String password) throws UnsupportedEncodingException,
            NoSuchAlgorithmException {
        MessageDigest digest;
        String usrPwdNonceHash;
        String nonce;
        try {
            nonce = new String(Base64.decodeBase64(Constants.INITIAL_NONCE), Constants.UTF_8);
            digest = MessageDigest.getInstance(Constants.MD5);
            String usrPwd = username + ":" + password;
            String usrPwdHash = Base64.encodeBase64String(digest.digest(usrPwd.getBytes(Constants.UTF_8)));
            String usrPwdNonce = usrPwdHash + ":" + nonce;
            usrPwdNonceHash = Base64.encodeBase64String(digest.digest(usrPwdNonce.getBytes(Constants.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException("Problem occurred in encoding credentials data.");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Problem occurred in generating password hash.", e);
        }
        return usrPwdNonceHash;
    }
}
