
package org.eos.controlcenter;

import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

public class StatusbarHandler extends PreferenceScreenHandler {
    private static final String APPEARANCE_CATEGORY = "statusbar_appearance_category";
    private static final String TILEPICKER = "eos_interface_panel_tile_chooser";
    private static final String COLUMNS = "eos_interface_panel_columns";
    private static final String TOGGLES = "eos_interface_settings";
    private static final String GLASS = "eos_interface_statusbar_glass_style";
    private static final String STYLE_GLASS_SEEKBAR_KEY = "eos_interface_statusbar_glass_seekbar";

    CheckBoxPreference mGlass;
    GlassDialogPreference mGlassSeekBar;
    CheckBoxPreference mBatteryIcon;
    CheckBoxPreference mBatteryText;
    CheckBoxPreference mBatteryPercent;
    CheckBoxPreference mStatusbarClock;
    ListPreference mAmPmStyle;
    EosMultiSelectListPreference mEosQuickPanelView;
    ListPreference mColumns;
    CheckBoxPreference mEosTogglesEnabled;
    CheckBoxPreference mHideIndicator;
    EosMultiSelectListPreference mEosQuickSettingsView;

    PreferenceCategory mAppearance;
    PreferenceCategory mToggles;

    boolean mEosSettingsEnabled = false;

    public StatusbarHandler(PreferenceScreen root) {
        super(root);
        init();
    }

    protected void init() {
        mAppearance = (PreferenceCategory) mRoot.findPreference(APPEARANCE_CATEGORY);
        mGlass = (CheckBoxPreference) mAppearance.findPreference(GLASS);
        mGlassSeekBar = (GlassDialogPreference) mAppearance.findPreference(STYLE_GLASS_SEEKBAR_KEY);

        // if the device has no navbar, the user will control glass
        // from here. Otherwise it is removed and user will control glass
        // from Navigation settings

        if (EOSUtils.hasNavBar(mContext)) {
            mAppearance.removePreference(mGlass);
            mAppearance.removePreference(mGlassSeekBar);
            mGlass = null;
            mGlassSeekBar = null;
        } else {
            mGlass.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_USE_GLASS,
                    EOSConstants.SYSTEMUI_USE_GLASS_DEF) == 1);
            mGlass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = ((Boolean) newValue).booleanValue();
                    Settings.System.putInt(mResolver,
                            EOSConstants.SYSTEMUI_USE_GLASS, enabled ? 1 : 0);
                    mGlass.setChecked(enabled);
                    return true;
                }
            });
        }

        mToggles = (PreferenceCategory) mRoot.findPreference(TOGGLES);

        mBatteryIcon = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_statusbar_battery_icon");
        mBatteryIcon.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE_DEF) == 1);
        mBatteryIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_BATTERY_ICON_VISIBLE,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        mBatteryText = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_statusbar_battery_text");
        mBatteryText.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE_DEF) == 1);
        mBatteryText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_BATTERY_TEXT_VISIBLE,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        mBatteryPercent = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_statusbar_battery_text_percent");
        mBatteryPercent.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE,
                EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE_DEF) == 1);

        mBatteryPercent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_BATTERY_PERCENT_VISIBLE,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        mStatusbarClock = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_statusbar_clock");
        mStatusbarClock.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_CLOCK_VISIBLE,
                EOSConstants.SYSTEMUI_CLOCK_VISIBLE_DEF) == 1);

        mStatusbarClock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_CLOCK_VISIBLE,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        mAmPmStyle = (ListPreference) mRoot.findPreference("eos_interface_statusbar_clock_am_pm");

        String defValue = String.valueOf(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_CLOCK_AMPM,
                EOSConstants.SYSTEMUI_CLOCK_AMPM_DEF));

        mAmPmStyle.setDefaultValue(defValue);
        mAmPmStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int val = Integer.parseInt(((String) newValue).toString());
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_CLOCK_AMPM,
                        (val));
                return true;
            }
        });

        mEosQuickPanelView = (EosMultiSelectListPreference) mRoot.findPreference(TILEPICKER);
        mEosQuickPanelView.setEntries(mContext.getResources().getStringArray(
                R.array.eos_panel_enabled_names));
        mEosQuickPanelView.setEntryValues(mContext.getResources().getStringArray(
                R.array.eos_panel_enabled_preferences));
        mEosQuickPanelView.setReturnFullList(true);
        populateEosPanelList();

        mEosQuickPanelView
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Map<String, Boolean> values = (Map<String, Boolean>) newValue;
                        StringBuilder newPreferenceValue = new StringBuilder();
                        for (Entry entry : values.entrySet()) {
                            newPreferenceValue.append(entry.getKey());
                            newPreferenceValue.append("|");
                        }
                        Settings.System.putString(mResolver,
                                EOSConstants.SYSTEMUI_PANEL_ENABLED_TILES,
                                newPreferenceValue.toString());
                        return true;
                    }
                });

        mColumns = (ListPreference) mRoot.findPreference(COLUMNS);
        mColumns.setDefaultValue(String.valueOf((Settings.System.getInt(
                mResolver,
                EOSConstants.SYSTEMUI_PANEL_COLUMN_COUNT,
                EOSConstants.SYSTEMUI_PANEL_COLUMNS_DEF))));
        mColumns.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int val = Integer.parseInt(((String) newValue).toString());
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_PANEL_COLUMN_COUNT,
                        (val));
                return true;
            }

        });

        mEosTogglesEnabled = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_settings_eos_settings_enabled");
        mEosTogglesEnabled.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED,
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED_DEF) == 1);
        mEosTogglesEnabled
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_SETTINGS_ENABLED,
                                ((Boolean) newValue).booleanValue() ? 1 : 0);
                        enableAllCategoryChilds(mToggles,
                                "eos_interface_settings_eos_settings_enabled",
                                ((Boolean) newValue).booleanValue());
                        return true;
                    }
                });

        mEosQuickSettingsView = (EosMultiSelectListPreference) mRoot
                .findPreference("eos_interface_eos_quick_enabled");
        mEosQuickSettingsView.setEntries(mContext.getResources().getStringArray(
                R.array.eos_quick_enabled_names));
        mEosQuickSettingsView.setEntryValues(mContext.getResources().getStringArray(
                R.array.eos_quick_enabled_preferences));
        mEosQuickSettingsView.setReturnFullList(true);
        populateEosSettingsList();
        mEosQuickSettingsView
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Map<String, Boolean> values = (Map<String, Boolean>) newValue;
                        StringBuilder newPreferenceValue = new StringBuilder();
                        for (Entry entry : values.entrySet()) {
                            newPreferenceValue.append(entry.getKey());
                            newPreferenceValue.append("|");
                        }
                        Settings.System.putString(mResolver,
                                EOSConstants.SYSTEMUI_SETTINGS_ENABLED_CONTROLS,
                                newPreferenceValue.toString());
                        return true;
                    }
                });

        mHideIndicator = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_settings_indicator_visibility");
        mHideIndicator.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN,
                EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN_DEF) == 1);
        mHideIndicator.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_SETTINGS_INDICATOR_HIDDEN,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        enableAllCategoryChilds(mToggles, "eos_interface_settings_eos_settings_enabled",
                mEosTogglesEnabled.isChecked());
    }

    private void populateEosPanelList() {
        LinkedHashSet<String> selectedValues = new LinkedHashSet<String>();
        String enabledControls = Settings.System.getString(mResolver,
                EOSConstants.SYSTEMUI_PANEL_ENABLED_TILES);
        if (enabledControls != null) {
            String[] controls = enabledControls.split("\\|");
            selectedValues.addAll(Arrays.asList(controls));
        } else {
            selectedValues.addAll(Arrays.asList(EOSConstants.SYSTEMUI_PANEL_DEFAULTS));
        }
        mEosQuickPanelView.setValues(selectedValues);

        if (!EOSUtils.hasData(mContext)) {
            mEosQuickPanelView
                    .removeValueEntry(EOSConstants.SYSTEMUI_PANEL_DATA_TILE);
            mEosQuickPanelView
                    .removeValueEntry(EOSConstants.SYSTEMUI_PANEL_WIFIAP_TILE);
        }

        if (!Utils.hasTorch(mContext)) {
            mEosQuickPanelView.removeValueEntry(EOSConstants.SYSTEMUI_PANEL_TORCH_TILE);
        }

        if (!EOSUtils.isCdmaLTE(mContext)) {
            mEosQuickPanelView.removeValueEntry(EOSConstants.SYSTEMUI_PANEL_LTE_TILE);
        }
    }

    private void populateEosSettingsList() {
        LinkedHashSet<String> selectedValues = new LinkedHashSet<String>();
        String enabledControls = Settings.System.getString(mResolver,
                EOSConstants.SYSTEMUI_SETTINGS_ENABLED_CONTROLS);
        if (enabledControls != null) {
            String[] controls = enabledControls.split("\\|");
            selectedValues.addAll(Arrays.asList(controls));
        } else {
            selectedValues.addAll(Arrays.asList(EOSConstants.SYSTEMUI_SETTINGS_DEFAULTS));
        }
        mEosQuickSettingsView.setValues(selectedValues);

        if (!EOSUtils.hasData(mContext)) {
            mEosQuickSettingsView
                    .removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_MOBILEDATA);
            mEosQuickSettingsView
                    .removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_WIFITETHER);
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_USBTETHER);
        }

        if (!EOSUtils.isCdmaLTE(mContext)) {
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_LTE);
        }

        if (!Utils.hasTorch(mContext)) {
            mEosQuickSettingsView.removeValueEntry(EOSConstants.SYSTEMUI_SETTINGS_TORCH);
        }
    }

    private void enableAllCategoryChilds(PreferenceCategory pc, String keyToExclude, boolean enabled) {
        int nbPrefs = pc.getPreferenceCount();
        for (int pref = 0; pref < nbPrefs; pref++)
            if (!pc.getPreference(pref).getKey().equals(keyToExclude))
                pc.getPreference(pref).setEnabled(enabled);
    }
}
