
package org.eos.controlcenter;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class ClockActions extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    CheckBoxPreference mStatusbarClock;
    ColorPreference mClockColor;
    Preference mClockColorDefault;
    ListPreference mAmPmStyle;
    ContentResolver cr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.clock_settings);
        cr = getActivity().getContentResolver();

        mStatusbarClock = (CheckBoxPreference) findPreference("eos_interface_statusbar_clock");
        mStatusbarClock.setChecked(Settings.System.getInt(cr, EOSConstants.SYSTEMUI_CLOCK_VISIBLE,
                EOSConstants.SYSTEMUI_CLOCK_VISIBLE_DEF) == 1);
        mStatusbarClock.setOnPreferenceChangeListener(this);

        mClockColor = (ColorPreference) findPreference("eos_interface_statusbar_clock_color");
        mClockColor.setProviderTarget(EOSConstants.SYSTEMUI_CLOCK_COLOR);

        mClockColorDefault = (Preference) findPreference("eos_interface_statusbar_clock_color_default");
        mClockColorDefault.setOnPreferenceChangeListener(this);

        mAmPmStyle = (ListPreference) findPreference("eos_interface_statusbar_clock_am_pm");
        mAmPmStyle.setOnPreferenceChangeListener(this);

        // if (isTablet()) {
        // PreferenceCategory cat = (PreferenceCategory)
        // findPreference("eos_statusbar_clock");
        // cat.removePreference(mAmPmStyle);
        // }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
        if (pref.equals(mClockColorDefault)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_CLOCK_COLOR, -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (pref.equals(mStatusbarClock)) {
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_CLOCK_VISIBLE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (pref.equals(mAmPmStyle)) {
            int val = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(cr, EOSConstants.SYSTEMUI_CLOCK_AMPM,
                    (val));
            return true;
        }
        return false;
    }

    private boolean isTablet() {
        Context mContext = (Context) getActivity();
        boolean xlarge = ((mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }
}
