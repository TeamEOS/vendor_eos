
package org.eos.controlcenter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.teameos.jellybean.settings.EOSConstants;

//import com.android.internal.telephony.RILConstants;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.IWindowManager;

import com.android.internal.telephony.RILConstants;

public class Interface extends PreferenceFragment implements OnPreferenceChangeListener {

    private final String QUICKSETTINGSPANEL = "eos_interface_panel_settings";
    private final String BATTERY = "eos_battery_settings";
    private final String CLOCK = "eos_clock_settings";
    private final String STATBARCOLOR = "eos_statbar_color_settings";
    private final String NAVBAR = "eos_navbar_settings";
    private final String SOFTKEYS = "eos_softkey_settings";
    private final String NAVRING = "eos_navring_settings";

    private Context mContext;

    // private CheckBoxPreference mRecentsKillallButtonPreference;
    // private CheckBoxPreference mRecentsMemDisplayPreference;
    private CheckBoxPreference mShowAllLockscreenWidgetsPreference;
    private CheckBoxPreference mLowProfileNavBar;
    private CheckBoxPreference mEosTogglesEnabled;
    private CheckBoxPreference mHideIndicator;
    private ColorPreference mIndicatorColor;
    private Preference mIndicatorDefaultColor;
    private EosMultiSelectListPreference mEosQuickSettingsView;
    private boolean mEosSettingsEnabled = false;
    private boolean mHasNavBar = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.interface_settings);

        mContext = getActivity();
        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            mHasNavBar = mWindowManager.hasNavigationBar();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mEosQuickSettingsView = (EosMultiSelectListPreference) findPreference("eos_interface_eos_quick_enabled");
        mEosTogglesEnabled = (CheckBoxPreference) findPreference("eos_interface_settings_eos_settings_enabled");
        mShowAllLockscreenWidgetsPreference = (CheckBoxPreference) findPreference("eos_interface_lockscreen_show_all_widgets");
        // mRecentsKillallButtonPreference = (CheckBoxPreference)
        // findPreference("eos_interface_recents_killall_button");
        // mRecentsMemDisplayPreference = (CheckBoxPreference)
        // findPreference("eos_interface_recents_mem_display");
        mLowProfileNavBar = (CheckBoxPreference) findPreference("eos_interface_navbar_low_profile");
        mHideIndicator = (CheckBoxPreference) findPreference("eos_interface_settings_indicator_visibility");
        mIndicatorColor = (ColorPreference) findPreference("eos_interface_settings_indicator_color");
        mIndicatorDefaultColor = (Preference) findPreference("eos_interface_settings_indicator_color_default");

        if (!mHasNavBar) {
            PreferenceScreen ps = this.getPreferenceScreen();
            PreferenceCategory pc = (PreferenceCategory) ps.findPreference("eos_interface_navbar");
            if (pc != null)
                ps.removePreference(pc);
        }

        // will be null on tablets and grouper in tablet mode
        if (mLowProfileNavBar != null && mHasNavBar) {
            mLowProfileNavBar.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_BAR_SIZE_MODE, 0) == 1);
            mLowProfileNavBar.setOnPreferenceChangeListener(this);
        }

        mEosSettingsEnabled = Settings.System.getInt(mContext.getContentResolver(),
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED,
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED_DEF) == 1;
        if (mEosTogglesEnabled != null) {
            mEosTogglesEnabled.setChecked(mEosSettingsEnabled);
            mEosTogglesEnabled.notifyDependencyChange(mEosSettingsEnabled);
            mEosTogglesEnabled.setOnPreferenceChangeListener(this);
        }

        if (mEosQuickSettingsView != null) {
            mEosQuickSettingsView.setOnPreferenceChangeListener(this);
            mEosQuickSettingsView.setEntries(getResources().getStringArray(
                    R.array.eos_quick_enabled_names));
            mEosQuickSettingsView.setEntryValues(getResources().getStringArray(
                    R.array.eos_quick_enabled_preferences));
            mEosQuickSettingsView.setReturnFullList(true);
            populateEosSettingsList();
        }

        if (mHideIndicator != null) {
            mHideIndicator.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN,
                    EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN_DEF) == 1);
            mHideIndicator.setOnPreferenceChangeListener(this);
            mHideIndicator.setEnabled(mEosSettingsEnabled);
        }

        if (mIndicatorColor != null) {
            mIndicatorColor.setProviderTarget(EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_COLOR);
            mIndicatorColor.setEnabled(mEosSettingsEnabled);
        }

        if (mIndicatorDefaultColor != null) {
            mIndicatorDefaultColor
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            // TODO Auto-generated method stub
                            Settings.System.putInt(mContext.getContentResolver(),
                                    EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_COLOR,
                                    EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_COLOR_DEF);
                            return true;
                        }
                    });
            mIndicatorDefaultColor.setEnabled(mEosSettingsEnabled);
        }

        /**
         * if (mRecentsKillallButtonPreference != null) {
         * mRecentsKillallButtonPreference.setOnPreferenceChangeListener(this);
         * } if (mRecentsMemDisplayPreference != null) {
         * mRecentsMemDisplayPreference.setOnPreferenceChangeListener(this); }
         */

        if (mShowAllLockscreenWidgetsPreference != null) {
            mShowAllLockscreenWidgetsPreference.setChecked(Settings.System.getInt(
                    mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_LOCKSCREEN_SHOW_ALL_WIDGETS, 0) == 1);
            mShowAllLockscreenWidgetsPreference.setOnPreferenceChangeListener(this);
        }
    }

    private void populateEosSettingsList() {
        LinkedHashSet<String> selectedValues = new LinkedHashSet<String>();
        String enabledControls = Settings.System.getString(mContext.getContentResolver(),
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED_CONTROLS);
        if (enabledControls != null) {
            String[] controls = enabledControls.split("\\|");
            selectedValues.addAll(Arrays.asList(controls));
        } else {
            selectedValues.addAll(Arrays.asList(EOSConstants.SYSTEMUI_SETTINGS_DEFAULTS));
        }
        mEosQuickSettingsView.setValues(selectedValues);

        if (!Utils.hasData(mContext)) {
            mEosQuickSettingsView
                    .removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_MOBILEDATA);
            mEosQuickSettingsView
                    .removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_WIFITETHER);
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_USBTETHER);
        }

        if (!Utils.isCdmaLTE(mContext)) {
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_LTE);
        }
        if (!Utils.hasTorch()) {
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_TORCH);
        }
        mEosQuickSettingsView.setEnabled(mEosSettingsEnabled);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mEosQuickSettingsView)) {
            Map<String, Boolean> values = (Map<String, Boolean>) newValue;
            StringBuilder newPreferenceValue = new StringBuilder();
            for (Entry entry : values.entrySet()) {
                newPreferenceValue.append(entry.getKey());
                newPreferenceValue.append("|");
            }
            Settings.System.putString(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_SETTINGS_ENABLED_CONTROLS,
                    newPreferenceValue.toString());
            return true;
        } else
        /**
         * if (preference.equals(mRecentsKillallButtonPreference)) {
         * Settings.System.putInt(mContext.getContentResolver(),
         * EOSConstants.SYSTEMUI_RECENTS_KILLALL_BUTTON, ((Boolean)
         * newValue).booleanValue() ? 1 : 0); return true; } else if
         * (preference.equals(mRecentsMemDisplayPreference)) {
         * Settings.System.putInt(mContext.getContentResolver(),
         * EOSConstants.SYSTEMUI_RECENTS_MEM_DISPLAY, ((Boolean)
         * newValue).booleanValue() ? 1 : 0); return true; } else
         */
        if (preference.equals(mEosTogglesEnabled)) {
            mEosSettingsEnabled = ((Boolean) newValue).booleanValue();
            int val = mEosSettingsEnabled ? 1 : 0;
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_SETTINGS_ENABLED, val);
            mEosTogglesEnabled.notifyDependencyChange(mEosSettingsEnabled ? false : true);
            mEosQuickSettingsView.setEnabled(mEosSettingsEnabled);
            mHideIndicator.setEnabled(mEosSettingsEnabled);
            mIndicatorColor.setEnabled(mEosSettingsEnabled);
            mIndicatorDefaultColor.setEnabled(mEosSettingsEnabled);
            return true;
        } else if (preference.equals(mLowProfileNavBar)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_BAR_SIZE_MODE,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference.equals(mHideIndicator)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference.equals(mShowAllLockscreenWidgetsPreference)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_LOCKSCREEN_SHOW_ALL_WIDGETS,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        super.onPreferenceTreeClick(prefScreen, pref);
        if (pref.getKey().equals(QUICKSETTINGSPANEL)) {
            Main.showFragment("Quick Settings", new QuickSettingsPanel());
            return true;
        } else if (pref.getKey().equals(BATTERY)) {
            Main.showFragment("Battery Settings", new BatteryActions());
            return true;
        } else if (pref.getKey().equals(CLOCK)) {
            Main.showFragment("Clock Settings", new ClockActions());
            return true;
        } else if (pref.getKey().equals(STATBARCOLOR)) {
            Main.showFragment("Status Bar Color", new StatusBarColor());
            return true;
        } else if (pref.getKey().equals(NAVBAR)) {
            Main.showFragment("Nav Bar Appearance", new NavigationAreaActions());
            return true;
        } else if (pref.getKey().equals(SOFTKEYS)) {
            Main.showFragment("SoftKey Settings", new SoftKeyActions());
            return true;
        } else if (pref.getKey().equals(NAVRING)) {
            Main.showFragment("Quick Launch Targets", new NavRingActions());
            return true;
        }
        return false;
    }
}
