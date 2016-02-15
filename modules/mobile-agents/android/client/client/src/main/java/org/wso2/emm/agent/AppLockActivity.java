package org.wso2.emm.agent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;


public class AppLockActivity extends SherlockActivity {

    private String message;
    private Button btnOK;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_lock);


        btnOK = (Button) findViewById(R.id.btnOK);
        txtMessage = (TextView) findViewById(R.id.txtMessage);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey(getResources().getString(R.string.intent_extra_message))) {
                message = extras.getString(getResources().getString(R.string.intent_extra_message));
            }
        }

        txtMessage.setText(message);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startHomescreen=new Intent(Intent.ACTION_MAIN);
                startHomescreen.addCategory(Intent.CATEGORY_HOME);
                startHomescreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startHomescreen);
                AppLockActivity.this.finish();
            }
        });


    }
}
