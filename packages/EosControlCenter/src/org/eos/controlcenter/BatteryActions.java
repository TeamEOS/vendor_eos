
package org.eos.controlcenter;

import android.os.Bundle;
import android.content.ContentResolver;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class BatteryActions extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    CheckBoxPreference mBatteryIcon;
    CheckBoxPreference mBatteryText;
    CheckBoxPreference mBatteryPercent;
    ColorPreference mBatteryTextColor;
    Preference mBatteryTextColorDefault;
    ContentResolver cr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.battery_settings);
        cr = getActivity().getContentResolver();

        mBatteryIcon = (CheckBoxPreference) findPreference("eos_interface_statusbar_battery_icon");
        mBatteryIcon.setChecked(Settings.System.getInt(cr,
                EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE_DEF) == 1);
        mBatteryIcon.setOnPreferenceChangeListener(this);

        mBatteryText = (CheckBoxPreference) findPreference("eos_interface_statusbar_battery_text");
        mBatteryText.setChecked(Settings.System.getInt(cr,
                EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE_DEF) == 1);
        mBatteryText.setOnPreferenceChangeListener(this);

        mBatteryPercent = (CheckBoxPreference) findPreference("eos_interface_statusbar_battery_text_percent");
        mBatteryPercent.setChecked(Settings.System.getInt(cr,
                EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE_DEF) == 1);
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mBatteryTextColor = (ColorPreference) findPreference("eos_interface_statusbar_battery_text_color");
        mBatteryTextColor.setProviderTarget(EOSConstants.SYSTEMUI_BATTERY_TEXT_COLOR);

        mBatteryTextColorDefault = (Preference) findPreference("eos_interface_statusbar_battery_text_color_default");
        mBatteryTextColorDefault.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
        if (pref.equals(mBatteryTextColorDefault)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_BATTERY_TEXT_COLOR, -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (pref.equals(mBatteryIcon)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
        } else if (pref.equals(mBatteryText)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
        } else if (pref.equals(mBatteryPercent)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
        }
        return true;
    }
}
