Prerequisites

1. Apache maven 3.1.1 or higher
2. Apache Ant 1.9.3 or higher
3. Android SDK (SDK 17 or higher) must be installed & ANDROID_HOME environment variable must be set

How to use MDM-Android-agent archetype

1. Install the archetype jar by executing the following command in the cmd.

mvn install:install-file -Dfile=<PATH_TO mdm-android-agent-archetype-1.0.0-SNAPSHOT.jar> -DgroupId=org.wso2.mdmserver -DartifactId=mdm-android-agent-archetype -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

2. Execute the following command to generate a mdm-android-agent project. Use the generated client-key & client-secret for API subscription as the values for -DclientKey & -DclientSecret arguments. The default android target version used here is 17. If you need to change that please provide -Dplatform argument.

mvn archetype:generate -B -DarchetypeGroupId=org.wso2.mdmserver -DarchetypeArtifactId=mdm-android-agent-archetype -DarchetypeVersion=1.0.0-SNAPSHOT -DgroupId=org.wso2.carbon -DartifactId=mdm-android-agent -DclientKey=<YOUR_CLIENT_KEY> -DclientSecret=<YOUR_CLIENT_SECRET> -Dplatform=<TARGET_ANDROID_SDK_VERSION>

3. Navigate to the created cdm-android-agent directory through the cmd.
4. Execute ant release / ant debug to generate the apk file