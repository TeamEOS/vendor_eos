package com.cfx.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;

public class SystemSettings extends CFXPreferenceFragment {
	OnActivityRequestedListener mListener;

	public static SystemSettings newInstance() {
		return new SystemSettings();
	}

	public SystemSettings() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.system_settings);
	}

}
