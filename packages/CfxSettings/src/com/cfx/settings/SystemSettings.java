package com.cfx.settings;

import org.codefirex.utils.CFXConstants;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

public class SystemSettings extends CFXPreferenceFragment {
	private static final String SCREEN_PLUG = "cfx_system_power_unplug_screen_off";
	private static final String CRT_OFF = "cfx_system_power_crt_screen_off";
	private static final String CRT_ON = "cfx_system_power_crt_screen_on";

	OnActivityRequestedListener mListener;

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
	}

}
