
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class SoftKeyActions extends ActionFragment
        implements Preference.OnPreferenceChangeListener {

    Context mContext;
    ContentResolver mResolver;

    CheckBoxPreference mEnable;
    CheckBoxPreference mMenuPersist;
    ColorPreference mBackColor;
    ColorPreference mHomeColor;
    ColorPreference mRecentColor;
    ColorPreference mMenuColor;
    ColorPreference mBackGlowColor;
    ColorPreference mHomeGlowColor;
    ColorPreference mRecentGlowColor;
    ColorPreference mMenuGlowColor;
    Preference mBackColorDef;
    Preference mHomeColorDef;
    Preference mRecentColorDef;
    Preference mMenuColorDef;
    Preference mBackGlowColorDef;
    Preference mHomeGlowColorDef;
    Preference mRecentGlowColorDef;
    Preference mMenuGlowColorDef;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (Context) getActivity();
        mResolver = mContext.getContentResolver();
        addPreferencesFromResource(R.xml.softkey_settings);

        mEnable = (CheckBoxPreference) findPreference("eos_interface_softkey_enable_feature");
        mEnable.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_NAVBAR_DISABLE_GESTURE,
                EOSConstants.SYSTEMUI_NAVBAR_DISABLE_GESTURE_DEF) == 1);
        mEnable.setOnPreferenceChangeListener(this);

        addActionPreference((ActionPreference) findPreference("eos_interface_softkey_back_longpress"));
        addActionPreference((ActionPreference) findPreference("eos_interface_softkey_recent_longpress"));
        addActionPreference((ActionPreference) findPreference("eos_interface_softkey_menu_longpress"));

        mMenuPersist = (CheckBoxPreference) findPreference("eos_interface_softkey_menu_persist");
        mMenuPersist.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_SOFTKEY_MENU_PERSIST, 0) == 1);
        mMenuPersist.setOnPreferenceChangeListener(this);

        mBackColor = (ColorPreference) findPreference("eos_softkey_color_back");
        mBackColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_BACK_KEY_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mBackColorDef = (Preference) findPreference("eos_interface_softkey_back_default_key_color");

        mBackGlowColor = (ColorPreference) findPreference("eos_softkey_glow_color_back");
        mBackGlowColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_BACK_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mBackGlowColorDef = (Preference) findPreference("eos_interface_softkey_back_default_glow_color");

        mHomeColor = (ColorPreference) findPreference("eos_softkey_color_home");
        mHomeColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_HOME_KEY_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mHomeColorDef = (Preference) findPreference("eos_interface_softkey_home_default_key_color");

        mHomeGlowColor = (ColorPreference) findPreference("eos_softkey_glow_color_home");
        mHomeGlowColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_HOME_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mHomeGlowColorDef = (Preference) findPreference("eos_interface_softkey_home_default_glow_color");

        mRecentColor = (ColorPreference) findPreference("eos_softkey_color_recent");
        mRecentColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_RECENT_KEY_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mRecentColorDef = (Preference) findPreference("eos_interface_softkey_recent_default_key_color");

        mRecentGlowColor = (ColorPreference) findPreference("eos_softkey_glow_color_recent");
        mRecentGlowColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_RECENT_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mRecentGlowColorDef = (Preference) findPreference("eos_interface_softkey_recent_default_glow_color");

        mMenuColor = (ColorPreference) findPreference("eos_softkey_color_menu");
        mMenuColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_MENU_KEY_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mMenuColorDef = (Preference) findPreference("eos_interface_softkey_menu_default_key_color");

        mMenuGlowColor = (ColorPreference) findPreference("eos_softkey_glow_color_menu");
        mMenuGlowColor.setProviderTarget(EOSConstants.SYSTEMUI_NAVKEY_MENU_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVBAR_SOFTKEY_DEFAULT);
        mMenuGlowColorDef = (Preference) findPreference("eos_interface_softkey_menu_default_glow_color");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
        if (pref.equals(mBackColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_BACK_KEY_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mBackGlowColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_BACK_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mHomeColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_HOME_KEY_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mHomeGlowColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_HOME_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mRecentColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_RECENT_KEY_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mRecentGlowColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_RECENT_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mMenuColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_MENU_KEY_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        } else if (pref.equals(mMenuGlowColorDef)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVKEY_MENU_GLOW_COLOR, EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mEnable)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVBAR_DISABLE_GESTURE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference.equals(mMenuPersist)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_SOFTKEY_MENU_PERSIST,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        }
        return false;
    }

}
