package com.cfx.settings;

import org.codefirex.utils.CFXConstants;
import org.codefirex.utils.CFXUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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
	static final String ED_SINGLE = "style_expanded_desktop_enabled";
	static final String ED_DUAL = "style_expanded_desktop_options";

	ContentResolver mResolver;
	Context mContext;
	CheckBoxPreference mGlassStyleBar;
	CheckBoxPreference mExpandedSingle;
	ListPreference mExpandedDual;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.style_settings);
		mContext = (Context) getActivity();
		mResolver = getActivity().getContentResolver();

		// Expanded desktop: use checkbox for no bars, list for dual bar options
		boolean mHasDualBars = CFXUtils.hasNavBar(mContext)
				|| CFXUtils.hasSystemBar(mContext);
		mExpandedSingle = (CheckBoxPreference) findPreference(ED_SINGLE);
		mExpandedDual = (ListPreference) findPreference(ED_DUAL);

		if (mHasDualBars) {
			getPreferenceScreen().removePreference(mExpandedSingle);
		} else {
			getPreferenceScreen().removePreference(mExpandedDual);
		}

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

		if (mExpandedDual != null) {
			mExpandedDual.setValue(String.valueOf(Settings.System.getInt(
					mResolver, Settings.System.EXPANDED_DESKTOP_STYLE, 0)));
			mExpandedDual
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							int val = Integer.parseInt(((String) newValue)
									.toString());
							Settings.System
									.putInt(mResolver,
											Settings.System.EXPANDED_DESKTOP_STYLE,
											val);
							return true;
						}
					});
		}

		if (mExpandedSingle != null) {
			mExpandedSingle.setChecked(Settings.System.getInt(mResolver,
					Settings.System.EXPANDED_DESKTOP_STYLE, 0) == 1);
			mExpandedSingle
					.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							Settings.System
									.putInt(mResolver,
											Settings.System.EXPANDED_DESKTOP_STYLE,
											((Boolean) newValue).booleanValue() ? 1
													: 0);
							return true;
						}
					});
		}
	}
}
