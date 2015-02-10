package org.wso2.cdm.agent.security;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.utils.CommonUtilities;

public class APIResultCallBackImpl implements APIResultCallBack {

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String senderId = "";
		String mode = "";
		String interval = "";
		JSONObject response = null;
		if (result != null) {
			String responseStatus = result.get("status");
			// validate status
			// {"sender_id" : "853689113861", "notifier" : "LOCAL", "notifierInterval" : 5}
			try {
				response = (JSONObject)new JSONParser().parse(result.get("response"));
				senderId = response.getString("sender_id");
				mode = response.getString("notifier");
				interval = response.getString("notifierInterval");
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (requestCode == CommonUtilities.SENDER_ID_REQUEST_CODE) {/*
				if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
					if(!senderId.equals("")){
			    		CommonUtilities.setSENDER_ID(senderId);
			    	}
			    	SharedPreferences mainPref = context.getSharedPreferences(
			    			getResources().getString(R.string.shared_pref_package), Context.MODE_PRIVATE);
			    	Editor editor = mainPref.edit();
			    	editor.putString(getResources().getString(R.string.shared_pref_sender_id), senderId);
			    	editor.putString(getResources().getString(R.string.shared_pref_message_mode), mode);
			    	editor.putString(getResources().getString(R.string.shared_pref_monitor_interval), interval);
					editor.commit();
					
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}		
					getLicense();
					
				} else {
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					
					alertDialog = CommonDialogUtils.getAlertDialogWithOneButton(AuthenticationActivity.this, getResources().getString(R.string.title_init_msg_error), getResources().getString(R.string.button_ok), senderIdFailedClickListener);
					alertDialog.show();
					
				}
				
			}
			
			if (requestCode == CommonUtilities.LICENSE_REQUEST_CODE) {
				if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
					showAlert(eula, CommonUtilities.EULA_TITLE);
					// fetchLicense();
					// NEED TO ADD PIN CODE IMPLEMENTATION. REGISTRATION HAS TO BE DONE IN PINCODE ACTIVITY
					Intent intent = new Intent(AuthenticationActivity.this,
							PinCodeActivity.class);
					intent.putExtra(
							getResources().getString(R.string.intent_extra_regid),
							regId);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					if (txtDomain.getText() != null
							&& txtDomain.getText().toString().trim() != "") {
						intent.putExtra(
								getResources().getString(R.string.intent_extra_email),
								username.getText().toString().trim() + "@"
										+ txtDomain.getText().toString().trim());
					} else {
						intent.putExtra(
								getResources().getString(R.string.intent_extra_email),
								username.getText().toString().trim());
					}
					startActivity(intent);
					
				} else {
					// NEED TO IMPLEMENT
				}
				
			*/}

		}
		
		
	}

}
