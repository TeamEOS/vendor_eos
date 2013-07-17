package com.cfx.settings;

import org.codefirex.utils.CFXConstants;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;

public class SystemSettings extends CFXPreferenceFragment {
	private static final String VOLUME_OVERLAY = "cfx_system_volume_panel_controls_key";
	private static final String VOLUME_BUTTON_SOUND = "cfx_system_volume_hard_button_sounds";
	private static final String VOLUME_LINK = "cfx_system_volume_link_notification_key";
	private static final String SCREEN_PLUG = "cfx_system_power_unplug_screen_off";
	private static final String CRT_OFF = "cfx_system_power_crt_screen_off";
	private static final String CRT_ON = "cfx_system_power_crt_screen_on";
	private static final String POWER_MENU = "cfx_power_menu_title";

	OnActivityRequestedListener mListener;

	private ListPreference mVolumePanelStyle;
	private CheckBoxPreference mVolumeKeySounds;
	private CheckBoxPreference mVolumeNotificationLink;
	private CheckBoxPreference mScreenCharging;
	private CheckBoxPreference mCrtOff;
	private CheckBoxPreference mCrtOn;
	private boolean isCrtOffChecked = false;

	public static SystemSettings newInstance() {
		return new SystemSettings();
	}

	public SystemSettings() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.system_settings);
		
		mListener = (OnActivityRequestedListener)getActivity();

		mVolumePanelStyle = (ListPreference) findPreference(VOLUME_OVERLAY);
		mVolumePanelStyle.setValue(String.valueOf(Settings.System.getInt(
				mResolver, Settings.System.MODE_VOLUME_OVERLAY,
				Settings.System.VOLUME_OVERLAY_EXPANDABLE)));
		mVolumePanelStyle
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						int val = Integer.parseInt((String) newValue);
						Settings.System.putInt(mResolver,
								Settings.System.MODE_VOLUME_OVERLAY, val);
						return true;
					}
				});

		mVolumeNotificationLink = (CheckBoxPreference) findPreference(VOLUME_LINK);
		mVolumeNotificationLink.setChecked(Settings.System.getInt(mResolver,
				Settings.System.VOLUME_LINK_NOTIFICATION, 1) == 1);
		mVolumeNotificationLink
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System.putInt(mResolver,
								Settings.System.VOLUME_LINK_NOTIFICATION,
								((Boolean) newValue).booleanValue() ? 1 : 0);
						return true;
					}
				});

		mVolumeKeySounds = (CheckBoxPreference) findPreference(VOLUME_BUTTON_SOUND);
		mVolumeKeySounds.setChecked(Settings.System.getInt(mResolver,
				Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED, 1) == 1);
		mVolumeKeySounds
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System.putInt(mResolver,
								Settings.System.VOLUME_ADJUST_SOUNDS_ENABLED,
								((Boolean) newValue).booleanValue() ? 1 : 0);
						return true;
					}
				});

		// respect device default configuration
		// true fades while false animates
		boolean electronBeamFadesConfig = getResources().getBoolean(
				com.android.internal.R.bool.config_animateScreenLights);

		// use this to enable/disable crt on feature
		// crt only works if crt off is enabled
		// total system failure if only crt on is enabled
		isCrtOffChecked = Settings.System.getInt(mResolver,
				CFXConstants.SYSTEM_POWER_ENABLE_CRT_OFF,
				electronBeamFadesConfig ? 0 : 1) == 1;

		mCrtOff = (CheckBoxPreference) findPreference(CRT_OFF);
		mCrtOff.setChecked(isCrtOffChecked);
		mCrtOff.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				isCrtOffChecked = ((Boolean) newValue).booleanValue();
				Settings.System.putInt(mResolver,
						CFXConstants.SYSTEM_POWER_ENABLE_CRT_OFF,
						(isCrtOffChecked ? 1 : 0));
				// if crt off gets turned off, crt on gets turned off and
				// disabled
				if (!isCrtOffChecked) {
					Settings.System.putInt(mResolver,
							CFXConstants.SYSTEM_POWER_ENABLE_CRT_ON, 0);
					mCrtOn.setChecked(false);
				}
				mCrtOn.setEnabled(isCrtOffChecked);
				return true;
			}
		});

		mCrtOn = (CheckBoxPreference) findPreference(CRT_ON);
		mCrtOn.setChecked(Settings.System.getInt(mResolver,
				CFXConstants.SYSTEM_POWER_ENABLE_CRT_ON, 0) == 1);
		mCrtOn.setEnabled(isCrtOffChecked);
		mCrtOn.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Settings.System.putInt(mResolver,
						CFXConstants.SYSTEM_POWER_ENABLE_CRT_ON,
						((Boolean) newValue).booleanValue() ? 1 : 0);
				return true;
			}
		});

		findPreference(POWER_MENU).setOnPreferenceClickListener(
				new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						mListener.onActivityRequested(getString(R.string.cfx_power_menu_title));
						return true;
					}
				});
	}

}
