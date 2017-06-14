package org.wso2.emm.agent;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;


public class AppLockActivity extends SherlockActivity {

    private String message;
    private Button btnOK;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_lock);

        btnOK = (Button) findViewById(R.id.btnOK);
        txtMessage = (TextView) findViewById(R.id.txtMessage);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey(getResources().getString(R.string.intent_extra_message_text))) {
                message = extras.getString(getResources().getString(R.string.intent_extra_message_text));
            }
        }

        txtMessage.setText(message);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeScreen();
            }
        });
    }

    @Override
    public void onBackPressed() {
        loadHomeScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            loadHomeScreen();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            loadHomeScreen();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadHomeScreen() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
        AppLockActivity.this.finish();
    }

}
