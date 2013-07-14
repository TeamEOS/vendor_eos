package com.cfx.settings;

import org.codefirex.utils.CFXConstants;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

public class StyleSettings extends CFXPreferenceFragment {
	public static StyleSettings newInstance() {
		return new StyleSettings();
	}

	public StyleSettings() {
	}

	static final String STYLE_GLASS_KEY = "cfx_style_navbar_glass";

	ContentResolver mResolver;
	Context mContext;
	CheckBoxPreference mGlassStyleBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.style_settings);
		mContext = (Context) getActivity();
		mResolver = getActivity().getContentResolver();

		mGlassStyleBar = (CheckBoxPreference) findPreference(STYLE_GLASS_KEY);

		mGlassStyleBar
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						boolean enabled = ((Boolean) newValue).booleanValue();
						Settings.System.putInt(mResolver,
								CFXConstants.SYSTEMUI_USE_GLASS, enabled ? 1
										: 0);
						mGlassStyleBar.setChecked(enabled);
						Intent intent = new Intent()
								.setAction(CFXConstants.ACTION_CFX_UI_CHANGE)
								.putExtra(
										CFXConstants.INTENT_REASON_UI_CHANGE,
										CFXConstants.INTENT_REASON_UI_CHANGE_GLASS_ENABLED);
						mContext.sendBroadcastAsUser(intent, new UserHandle(
								UserHandle.USER_ALL));
						return true;
					}
				});
	}

}
