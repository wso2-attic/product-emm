Updating/Changing the Andorid SDK
---------------------------------
There are some updates/changes needed to the Android SDK (eg when using it through IDE) when compiling the app.
Reason: App code uses some API that are hidden - we need the exposed interfaces to compile with. 

As an Example : 

In Context.java
public abstract void startActivityAsUser(Intent intent, UserHandle user);
public abstract void startActivityAsUser(Intent intent, Bundle options, UserHandle userId) ;

In ActivityManager.java
public static int getCurrentUser() {
        UserInfo ui;
        try {
            ui = ActivityManagerNative.getDefault().getCurrentUser();
            return ui != null ? ui.id : 0;
        } catch (RemoteException e) {
            return 0;
        }
    }
	
Note: You will get a "MethodNotFoundException on other "Standard" devices. 

Steps to build:
1. Go to <SDK Location>\android-sdk\platforms\android-21   (We use level 21 API for our build)
2. Unzip android.jar ( to a different temp folder)
3. Download the andorid.jar (which is used in the OS) from [1] or build it from AOSP source and put back under <SDK Location>\android-sdk\platforms\android-21
4. Build the app from IDE as you build a usual android app.

Steps to install on a rooted device:
1. Enable USB debugging on your device and connect your device to the computer via USB cable.
2. Enter the following commands:
       adb remount
       adb push apk-filename-here /system/app/
       adb shell chmod 644 /system/app/
       apk-filename-here
       adb reboot

[1] - https://docs.google.com/uc?id=0BxzxgoCRhSfzZ3p6VmFoT1VYaUU&export=download
