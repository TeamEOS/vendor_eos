
package org.eos.controlcenter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.teameos.jellybean.settings.EOSConstants;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;

public class InterfaceSettings extends PreferenceFragment implements OnPreferenceChangeListener {

    private final String QUICKSETTINGSPANEL = "eos_interface_panel_settings";
    private final String BATTERY = "eos_battery_settings";
    private final String CLOCK = "eos_clock_settings";
    private final String STATBARCOLOR = "eos_statbar_color_settings";
    private final String NAVBAR = "eos_navbar_settings";
    private final String SOFTKEYS = "eos_softkey_settings";
    private final String NAVRING = "eos_navring_settings";
    private static final String LAST_FRAG = "interface_last_viewed_frag";

    private Context mContext;

    private CheckBoxPreference mRecentsKillallButtonPreference;
    private CheckBoxPreference mRecentsMemDisplayPreference;
    private CheckBoxPreference mShowAllLockscreenWidgetsPreference;
    private CheckBoxPreference mLowProfileNavBar;
    private CheckBoxPreference mTabletStyleBar;
    private CheckBoxPreference mEosTogglesEnabled;
    private CheckBoxPreference mHideIndicator;
    private ColorPreference mIndicatorColor;
    private Preference mIndicatorDefaultColor;
    private EosMultiSelectListPreference mEosQuickSettingsView;
    private boolean mEosSettingsEnabled = false;
    private boolean mHasNavBar = false;
    private static String mLastFrag;
    
    public static InterfaceSettings newInstance(String lastFrag) {
        InterfaceSettings frag = new InterfaceSettings();
        Bundle b = new Bundle();
        b.putString(LAST_FRAG, lastFrag);
        frag.setArguments(b);
        return frag;
    }

    public static InterfaceSettings newInstance() {
        return new InterfaceSettings();
    }

    public InterfaceSettings() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.interface_settings);

        mContext = getActivity();
        mHasNavBar = Utils.hasNavBar(mContext);

        mEosQuickSettingsView = (EosMultiSelectListPreference) findPreference("eos_interface_eos_quick_enabled");
        mEosTogglesEnabled = (CheckBoxPreference) findPreference("eos_interface_settings_eos_settings_enabled");
        mShowAllLockscreenWidgetsPreference = (CheckBoxPreference) findPreference("eos_interface_lockscreen_show_all_widgets");
        mRecentsKillallButtonPreference = (CheckBoxPreference)
                findPreference("eos_interface_recents_killall_button");
        mRecentsMemDisplayPreference = (CheckBoxPreference)
                findPreference("eos_interface_recents_mem_display");
        mLowProfileNavBar = (CheckBoxPreference) findPreference("eos_interface_navbar_low_profile");
        mTabletStyleBar = (CheckBoxPreference) findPreference("eos_interface_navbar_tablet_style");
        mHideIndicator = (CheckBoxPreference) findPreference("eos_interface_settings_indicator_visibility");
        mIndicatorColor = (ColorPreference) findPreference("eos_interface_settings_indicator_color");
        mIndicatorDefaultColor = (Preference) findPreference("eos_interface_settings_indicator_color_default");

        if (!mHasNavBar) {
            PreferenceScreen ps = this.getPreferenceScreen();
            PreferenceCategory pc = (PreferenceCategory) ps.findPreference("eos_interface_navbar");
            if (pc != null)
                ps.removePreference(pc);
        }

        if (!Utils.isHybridUI(mContext) && mTabletStyleBar != null) {
            PreferenceScreen ps = this.getPreferenceScreen();
            PreferenceCategory pc = (PreferenceCategory) ps.findPreference("eos_interface_navbar");
            if (pc != null)
                pc.removePreference(mTabletStyleBar);
        }

        // will be null on tablets and grouper in tablet mode
        if (mLowProfileNavBar != null && mHasNavBar) {
            mLowProfileNavBar.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_BAR_SIZE_MODE, 0) == 1);
            mLowProfileNavBar.setOnPreferenceChangeListener(this);
        }

        if (mTabletStyleBar != null && mHasNavBar) {
            mTabletStyleBar.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR,
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR_DEF) == 1);
            mTabletStyleBar.setOnPreferenceChangeListener(this);
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

        if (mRecentsKillallButtonPreference != null)
            mRecentsKillallButtonPreference.setOnPreferenceChangeListener(this);
        if (mRecentsMemDisplayPreference != null)
            mRecentsMemDisplayPreference.setOnPreferenceChangeListener(this);

        if (mShowAllLockscreenWidgetsPreference != null) {
            mShowAllLockscreenWidgetsPreference.setChecked(Settings.System.getInt(
                    mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_LOCKSCREEN_SHOW_ALL_WIDGETS, 0) == 1);
            mShowAllLockscreenWidgetsPreference.setOnPreferenceChangeListener(this);
        }
        
        if(savedInstanceState != null) {
            mLastFrag = savedInstanceState.getString(LAST_FRAG, BATTERY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Main.mTwoPane) {
            if (mLastFrag == null)
                mLastFrag = BATTERY;
            showFragment(mLastFrag);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LAST_FRAG, mLastFrag);
        super.onSaveInstanceState(outState);
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
        } else if (preference.equals(mRecentsKillallButtonPreference)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_RECENTS_KILLALL_BUTTON, ((Boolean)
                    newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference.equals(mRecentsMemDisplayPreference)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_RECENTS_MEM_DISPLAY, ((Boolean)
                    newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (preference.equals(mEosTogglesEnabled)) {
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
        } else if (preference.equals(mTabletStyleBar)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent i = new Intent();
                    i.setComponent(ComponentName
                            .unflattenFromString("com.android.systemui/.SystemUIService"));
                    mContext.startService(i);
                    Intent intent = new Intent().setAction(EOSConstants.INTENT_EOS_CONTROL_CENTER);
                    intent.putExtra(EOSConstants.INTENT_EOS_CONTROL_CENTER_EXTRAS_STATE, true);
                    mContext.sendBroadcast(intent);
                }
            }, 250);
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
    
    private boolean showFragment(String key) {
        if (key.equals(QUICKSETTINGSPANEL)) {
            Main.showFragment("Quick Settings", new QuickSettingsPanel());
            mLastFrag = key;
            return true;
        } else if (key.equals(BATTERY)) {
            Main.showFragment("Battery Settings", new BatteryActions());
            mLastFrag = key;
            return true;
        } else if (key.equals(CLOCK)) {
            Main.showFragment("Clock Settings", new ClockActions());
            mLastFrag = key;
            return true;
        } else if (key.equals(STATBARCOLOR)) {
            Main.showFragment("Status Bar Color", new StatusBarColor());
            mLastFrag = key;
            return true;
        } else if (key.equals(NAVBAR)) {
            Main.showFragment("Nav Bar Appearance", new NavigationAreaActions());
            mLastFrag = key;
            return true;
        } else if (key.equals(SOFTKEYS)) {
            Main.showFragment("SoftKey Settings", new SoftKeyActions());
            mLastFrag = key;
            return true;
        } else if (key.equals(NAVRING)) {
            Main.showFragment("Quick Launch Targets", new NavRingActions());
            mLastFrag = key;
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        super.onPreferenceTreeClick(prefScreen, pref);
        return showFragment(pref.getKey());
    }
}
