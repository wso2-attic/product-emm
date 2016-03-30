package org.wso2.emm.agent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import org.wso2.emm.agent.utils.Constants;

/**
 * This activity is used to lock the device.
 * LockActivty only works if agent is registered as the device owner.
 *
 */
public class LockActivity extends Activity {

    private static final String TAG = LockActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);

        Bundle extras = getIntent().getExtras();

        enablePinnedActivity();

        TextView adminMessage = (TextView) findViewById(R.id.admin_message);
        if (extras.getString(Constants.ADMIN_MESSAGE) != null) {
            adminMessage.setText(extras.getString(Constants.ADMIN_MESSAGE));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enablePinnedActivity() {
        startLockTask();
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Hard lock is enabled");
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Device is locked", Toast.LENGTH_LONG).show();
    }

}
