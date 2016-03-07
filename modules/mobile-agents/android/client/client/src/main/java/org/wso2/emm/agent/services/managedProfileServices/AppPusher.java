package org.wso2.emm.agent.services.managedProfileServices;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by pasinduj on 2/29/16.
 */
public class AppPusher extends Activity {

    public void pushApp(Uri contentUriToShare) {
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        //intent.setDataAndType(contentUriToShare, "application/vnd.android.package-archive");
        //startActivity(intent);
        intent.setData(contentUriToShare);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                getApplicationInfo().packageName);
        startActivity(intent);
    }
}
