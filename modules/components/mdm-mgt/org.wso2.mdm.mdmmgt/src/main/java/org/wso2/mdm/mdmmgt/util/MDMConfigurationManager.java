package org.wso2.mdm.mdmmgt.util;

import java.io.File;
import org.wso2.carbon.utils.CarbonUtils;


public class MDMConfigurationManager {

    public static String getCarbonConfigDirPath() {
        String carbonConfigDirPath = System.getProperty("carbon.config.dir.path");
        if(carbonConfigDirPath == null) {
            carbonConfigDirPath = System.getenv("CARBON_CONFIG_DIR_PATH");
            if(carbonConfigDirPath == null) {
                return getCarbonHome() + File.separator + "repository" + File.separator + "conf";
            }
        }

        return carbonConfigDirPath;
    }
    public static String getCarbonHome() {
        String carbonHome = System.getProperty("carbon.home");
        if(carbonHome == null) {
            carbonHome = System.getenv("CARBON_HOME");
            System.setProperty("carbon.home", carbonHome);
        }
        return carbonHome;
    }

}
