package org.wso2.emm.agent.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import org.wso2.emm.agent.R;

/**
 * This class handles all the functionality related to data retrieval and saving to 
 * shared preferences.
 */
public class Preference {
	private static final int DEFAULT_INDEX = 0;

	/**
	 * Put data to shared preferences in private mode.
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value.
	 * @param value   - The actual value to be saved.
	 */
	public static void putString(Context context, String key, String value) {
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
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value to to be retrieved.
	 */
	public static String getString(Context context, String key) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
				                                    .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		return mainPref.getString(key, null);
	}

	/**
	 * Put data to shared preferences in private mode.
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value.
	 * @param value   - The actual value to be saved.
	 */
	public static void putFloat(Context context, String key, float value) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
				                                    .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		Editor editor = mainPref.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	/**
	 * Retrieve data from shared preferences in private mode.
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value to to be retrieved.
	 */
	public static float getFloat(Context context, String key) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
				                                    .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		return mainPref.getFloat(key, DEFAULT_INDEX);
	}

	/**
	 * Put data to shared preferences in private mode.
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value.
	 * @param value   - The actual value to be saved.
	 */
	public static void putInt(Context context, String key, int value) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
						                             .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		Editor editor = mainPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * Retrieve data from shared preferences in private mode.
	 * @param context - The context of activity which is requesting to put data.
	 * @param key     - Used to identify the value to to be retrieved.
	 */
	public static int getInt(Context context, String key) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
						                             .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		return mainPref.getInt(key, DEFAULT_INDEX);
	}

	/**
	 * Clear data saved in app local shared preferences.
	 * @param context - The context of activity which is requesting to put data.
	 */
	public static void clearPreferences(Context context) {
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
						                             .getString(R.string.shared_pref_package),
				                             Context.MODE_PRIVATE
				);
		mainPref.edit().clear().commit();
	}

}
