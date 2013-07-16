package com.cfx.settings;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

public class PowerMenu extends CFXPreferenceFragment {
	private static final String REBOOT_MENU = "cfx_power_menu_item_reboot";
	private static final String SCREENSHOT = "cfx_power_menu_item_screenshot";
	private static final String EXPANDED_DESKTOP = "cfx_power_menu_item_expanded_desktop";
	private static final String AIRPLANE_MODE = "cfx_power_menu_item_airplane_mode";
	private static final String SOUND_PANEL = "cfx_power_menu_item_sound_panel";

	CheckBoxPreference mReboot;
	CheckBoxPreference mScreenshot;
	CheckBoxPreference mExpanded;
	CheckBoxPreference mAirplane;
	CheckBoxPreference mSound;

	public static PowerMenu newInstance() {
		return new PowerMenu();
	}

	public PowerMenu() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.power_menu);

		mReboot = (CheckBoxPreference) findPreference(REBOOT_MENU);
		mReboot.setChecked(Settings.System.getInt(mResolver,
				Settings.System.POWER_MENU_REBOOT_ENABLED, 1) == 1);
		mReboot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Settings.System.putInt(mResolver,
						Settings.System.POWER_MENU_REBOOT_ENABLED,
						((Boolean) newValue).booleanValue() ? 1 : 0);
				return true;
			}
		});

		mScreenshot = (CheckBoxPreference) findPreference(SCREENSHOT);
		mScreenshot.setChecked(Settings.System.getInt(mResolver,
				Settings.System.POWER_MENU_SCREENSHOT_ENABLED, 0) == 1);
		mScreenshot
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System.putInt(mResolver,
								Settings.System.POWER_MENU_SCREENSHOT_ENABLED,
								((Boolean) newValue).booleanValue() ? 1 : 0);
						return true;
					}
				});

		mExpanded = (CheckBoxPreference) findPreference(EXPANDED_DESKTOP);
		mExpanded.setChecked(Settings.System.getInt(mResolver,
				Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0) == 1);
		mExpanded
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System
								.putInt(mResolver,
										Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED,
										((Boolean) newValue).booleanValue() ? 1
												: 0);
						return true;
					}
				});

		mAirplane = (CheckBoxPreference) findPreference(AIRPLANE_MODE);
		mAirplane.setChecked(Settings.System.getInt(mResolver,
				Settings.System.POWER_MENU_AIRPLANE_ENABLED, 1) == 1);
		mAirplane
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Settings.System.putInt(mResolver,
								Settings.System.POWER_MENU_AIRPLANE_ENABLED,
								((Boolean) newValue).booleanValue() ? 1 : 0);
						return true;
					}
				});

		mSound = (CheckBoxPreference) findPreference(SOUND_PANEL);
		mSound.setChecked(Settings.System.getInt(mResolver,
				Settings.System.POWER_MENU_SOUND_ENABLED, 1) == 1);
		mSound.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Settings.System.putInt(mResolver,
						Settings.System.POWER_MENU_SOUND_ENABLED,
						((Boolean) newValue).booleanValue() ? 1 : 0);
				return true;
			}
		});
	}
}
