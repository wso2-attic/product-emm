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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;

import javax.cache.Cache;
import javax.cache.CacheConfiguration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Class for generate random token for XCEP and WSTEP.
 */
public class DeviceUtil {

    private static final String TOKEN_CACHE_MANAGER = "TOKEN_CACHE_MANAGER";
    private static final String TOKEN_CACHE = "TOKEN_CACHE";
    private static final long CACHE_DURATION = 15l;
    private static boolean isContextCacheInitialized = false;

    public static String generateRandomToken() {
        return String.valueOf(UUID.randomUUID());
    }

    public static void persistChallengeToken(String token, String deviceID, String username) {

        Object objCacheEntry = getCacheEntry(token);
        CacheEntry cacheEntry;
        if (objCacheEntry == null) {
            cacheEntry = new CacheEntry();
            cacheEntry.setUsername(username);
        } else {
            cacheEntry = (CacheEntry) objCacheEntry;
        }
        if (deviceID != null) {
            cacheEntry.setDeviceID(deviceID);
        }
        getTokenCache().put(token, cacheEntry);
    }

    public static void removeToken(String token) {
        getTokenCache().remove(token);
    }

    public static Object getCacheEntry(String token) {
        return getTokenCache().get(token);
    }

    private static Cache getTokenCache() {
        CacheManager contextCacheManager = Caching.getCacheManager(TOKEN_CACHE_MANAGER).
                getCache(TOKEN_CACHE).getCacheManager();
        if (!isContextCacheInitialized) {
            return Caching.getCacheManager(TOKEN_CACHE_MANAGER).getCache(TOKEN_CACHE);
        } else {
            isContextCacheInitialized = true;
            return contextCacheManager.createCacheBuilder(TOKEN_CACHE_MANAGER).setExpiry(
                    CacheConfiguration.ExpiryType.MODIFIED,
                    new CacheConfiguration.Duration(TimeUnit.MINUTES, CACHE_DURATION)).setStoreByValue(false).build();
        }
    }
}
