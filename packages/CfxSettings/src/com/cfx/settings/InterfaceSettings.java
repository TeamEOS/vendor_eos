package com.cfx.settings;

import org.codefirex.utils.CFXConstants;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;

public class InterfaceSettings extends PreferenceFragment implements
		OnPreferenceChangeListener {

	public static InterfaceSettings newInstance() {
		return new InterfaceSettings();
	}

	public InterfaceSettings() {
	}

	static final String CATEGORY_INPUT = "cfx_interface_input";
	static final String PREF_MENU_OVERFLOW = "cfx_interface_input_menu_overflow";

	CheckBoxPreference mMenuOverflow;

	ContentResolver mResolver;
	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.interface_settings);

		mContext = (Context) getActivity();
		mResolver = getActivity().getContentResolver();
		mMenuOverflow = (CheckBoxPreference) findPreference(PREF_MENU_OVERFLOW);
		mMenuOverflow.setChecked(Settings.System.getBoolean(getActivity()
				.getContentResolver(),
				Settings.System.UI_FORCE_OVERFLOW_BUTTON, false));
		mMenuOverflow.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		if (mMenuOverflow.equals(pref)) {
			Settings.System.putBoolean(getActivity().getContentResolver(),
					Settings.System.UI_FORCE_OVERFLOW_BUTTON,
					((Boolean) newValue).booleanValue());
			return true;
		}
		return false;
	}

}