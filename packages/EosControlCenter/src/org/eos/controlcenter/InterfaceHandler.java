
package org.eos.controlcenter;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class InterfaceHandler extends PreferenceScreenHandler {
    private CheckBoxPreference mRecentsKillallButtonPreference;
    private CheckBoxPreference mRecentsMemDisplayPreference;
    private CheckBoxPreference mShowAllLockscreenWidgetsPreference;

    public InterfaceHandler(PreferenceScreen pref) {
        super(pref);
        init();
    }

    @Override
    protected void init() {
        mShowAllLockscreenWidgetsPreference = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_lockscreen_show_all_widgets");
        mRecentsKillallButtonPreference = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_recents_killall_button");
        mRecentsMemDisplayPreference = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_recents_mem_display");

        mShowAllLockscreenWidgetsPreference.setChecked(Settings.System.getInt(
                mResolver,
                EOSConstants.SYSTEMUI_LOCKSCREEN_SHOW_ALL_WIDGETS, 0) == 1);

        mRecentsKillallButtonPreference
                .setChecked(Settings.System.getInt(mResolver,
                        EOSConstants.SYSTEMUI_RECENTS_KILLALL_BUTTON, 0) == 1);

        mRecentsMemDisplayPreference.setChecked(Settings.System.getInt(
                mResolver, EOSConstants.SYSTEMUI_RECENTS_MEM_DISPLAY, 0) == 1);

        mRecentsKillallButtonPreference
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_RECENTS_KILLALL_BUTTON, ((Boolean)
                                newValue).booleanValue() ? 1 : 0);
                        return true;
                    }
                });

        mRecentsMemDisplayPreference
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_RECENTS_MEM_DISPLAY, ((Boolean)
                                newValue).booleanValue() ? 1 : 0);
                        return true;
                    }
                });

        mShowAllLockscreenWidgetsPreference
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_LOCKSCREEN_SHOW_ALL_WIDGETS,
                                ((Boolean) newValue).booleanValue() ? 1 : 0);
                        return true;
                    }
                });

    }

}
