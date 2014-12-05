package org.wso2.cdm.agent.utils;

import org.wso2.cdm.agent.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preference {
	public static String pack = "com.inosh.jwnotes";

	public static void put(Context c, String key, String value) {
		SharedPreferences mainPref =
		                             c.getSharedPreferences(c.getResources()
		                                                     .getString(R.string.shared_pref_package),
		                                                    Context.MODE_PRIVATE);
		Editor editor = mainPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String get(Context c, String key) {
		SharedPreferences mainPref =
		                             c.getSharedPreferences(c.getResources()
		                                                     .getString(R.string.shared_pref_package),
		                                                    Context.MODE_PRIVATE);
		return mainPref.getString(key, null);
	}

}
