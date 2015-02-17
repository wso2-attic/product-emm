package org.wso2.cdm.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.wso2.cdm.agent.R;

/**
 * Data retrieval and saving to shared preferences is done here.
 */
public class Preference {
	public static String pack = "com.wso2.cdm.agent";

	/**
	 * Put data to shared preferences in private mode.
	 *
	 * @param context the context of activity which is requesting to put data
	 * @param key     is used to identify the value.
	 * @param value   is the actual value to be saved.
	 */
	public static void put(Context context, String key, String value) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
				                                    .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		Editor editor = mainPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * Retrieve data from shared preferences in private mode.
	 *
	 * @param context the context of activity which is requesting to put data
	 * @param key     is used to identify the value to to be retrieved.
	 */
	public static String get(Context context, String key) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
				                                    .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		return mainPref.getString(key, null);
	}

}
