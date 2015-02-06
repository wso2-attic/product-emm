Prerequisites

1. Apache maven 3.0.5 or higher & M2_HOME environment variable must be set
2. Apache Ant 1.9.3 or higher
3. Android SDK (SDK 17 or higher) must be installed & ANDROID_HOME environment variable must be set

How to use Generate a MDM-Android-agent project

1. Generate the mdm-android-agent project by executing android-agent.sh shell-script.
2. Provide the client-Key, client-Secret & target Android SDK version in the interactive mode.
3. Then the script will install the maven archetype to the local repository & generate a mdm-android-agent project with provided arguments.
4. Navigate to the created mdm-android-agent directory through the cmd.
5. Execute ant release / ant debug to generate the apk file





