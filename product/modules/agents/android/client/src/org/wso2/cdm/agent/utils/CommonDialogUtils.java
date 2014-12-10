/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.cdm.agent.utils;

import org.wso2.cdm.agent.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
/**
 * 
 * The CommonDialogUtils class contains the all dialog templates.
 *
 */
public abstract class CommonDialogUtils {
	
	/**
	 * Return an Alert Dialog with one button.
	 * 
	 * @param context the Activity which needs this alert dialog
	 * @param message the message in the alert
	 * @param positiveBtnLabel the label of the positive button
	 * @param positiveClickListener the onClickListener of the positive button
	 * 
	 * @return the generated Alert Dialog
	 */
	public static AlertDialog.Builder getAlertDialogWithOneButton(Context context,
			String message, String positiveBtnLabel, 
			DialogInterface.OnClickListener positiveClickListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setPositiveButton(positiveBtnLabel, positiveClickListener);

		return builder;
	}

	/**
	 * Return an Alert Dialog with two buttons.
	 * 
	 * @param context 
	 * @param context the Activity which needs this alert dialog
	 * @param message the message in the alert
	 * @param positiveBtnLabel the label of the positive button
	 * @param negetiveBtnLabel the label of the negative button
	 * @param positiveClickListener the onClickListener of the positive button
	 * @param negativeClickListener the onClickListener of the negative button
	 * 
	 * @return the generated Alert Dialog.
	 */
	public static AlertDialog.Builder getAlertDialogWithTwoButton(Context context,
			String message, String positiveBtnLabel, String negetiveBtnLabel,
			DialogInterface.OnClickListener positiveClickListener,
			DialogInterface.OnClickListener negativeClickListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setPositiveButton(positiveBtnLabel, positiveClickListener)
				.setNegativeButton(negetiveBtnLabel, negativeClickListener);

		return builder;
	}
	
	/**
	 * Shows the Network unavailable message.
	 * 
	 * @param context the Activity where checking the network availability.
	 */
	public static void showNetworkUnavailableMessage(Context context) {
		AlertDialog.Builder builder = CommonDialogUtils
				.getAlertDialogWithOneButton(
						context,
						context.getResources().getString(R.string.error_network_unavailable),
						context.getResources().getString(R.string.button_ok), null);
		builder.show();
	}
	
	public static AlertDialog.Builder getAlertDialogWithTwoButtonAndTitle(Context context,
			String title, String message,
			String positiveBtnLabel, String negetiveBtnLabel,
			DialogInterface.OnClickListener positiveClickListener,
			DialogInterface.OnClickListener negativeClickListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message)
				.setPositiveButton(positiveBtnLabel, positiveClickListener)
				.setNegativeButton(negetiveBtnLabel, negativeClickListener);

		return builder;
	}
	
	/**
	 * Returns an Alert Dialog with one button and title.
	 * 
	 * @param context the activity which need this alert.
	 * @param title the alert title
	 * @param message the alert message
	 * @param positiveBtnLabel the positive button label
	 * @param positiveClickListener the positive button listener
	 * 
	 * @return an alert dialog
	 */
	public static AlertDialog.Builder getAlertDialogWithOneButtonAndTitle(Context context,
			String title, String message,
			String positiveBtnLabel, DialogInterface.OnClickListener positiveClickListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message)
				.setPositiveButton(positiveBtnLabel, positiveClickListener);
		builder.show();
		return builder;
	}
	
	/**
	 * Shows the ProgressDialog.
	 * 
	 * @param context the Activity which needs the ProgressDialog
	 * @param title the title
	 * @param message the message
	 * @param cancelListener the OnCancelListener
	 */
	public static ProgressDialog showPrgressDialog (Context context, String title, String message, OnCancelListener cancelListener) {
		ProgressDialog progressDialog = ProgressDialog.show(context,
				title,
				message, true);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(cancelListener);
		
		return progressDialog;
	}
	
	/**
	 * Stops progressDialog.
	 * 
	 */
	public static void stopProgressDialog(ProgressDialog progressDialog) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}
