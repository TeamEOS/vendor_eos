
package org.eos.controlcenter;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class SoftKeyActions extends ActionFragment
        implements Preference.OnPreferenceChangeListener {

    CheckBoxPreference mEnable;
    CheckBoxPreference mMenuPersist;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        // back softkey and glow colors
        ((MultiColorPreference) findPreference("eos_softkey_color_back"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_back_default_key_color"));
        ((MultiColorPreference) findPreference("eos_softkey_glow_color_back"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_back_default_glow_color"));

        // home softkey and glow colors
        ((MultiColorPreference) findPreference("eos_softkey_color_home"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_home_default_key_color"));
        ((MultiColorPreference) findPreference("eos_softkey_glow_color_home"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_home_default_glow_color"));

        // recent softkey and glow colors
        ((MultiColorPreference) findPreference("eos_softkey_color_recent"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_recent_default_key_color"));
        ((MultiColorPreference) findPreference("eos_softkey_glow_color_recent"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_recent_default_glow_color"));

        // menu softkey and glow colors
        ((MultiColorPreference) findPreference("eos_softkey_color_menu"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_menu_default_key_color"));
        ((MultiColorPreference) findPreference("eos_softkey_glow_color_menu"))
                .setRestorePref((Preference) findPreference("eos_interface_softkey_menu_default_glow_color"));
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
