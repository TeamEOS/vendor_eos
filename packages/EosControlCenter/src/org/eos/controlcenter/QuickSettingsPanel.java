
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.AttributeSet;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

public class QuickSettingsPanel extends PreferenceFragment implements OnPreferenceChangeListener {
    private static final String TILEPICKER = "eos_interface_panel_tile_chooser";
    private static final String COLUMNS = "eos_interface_panel_columns";

    private Context mContext;
    private ContentResolver mResolver;
    private EosMultiSelectListPreference mEosQuickPanelView;
    private ListPreference mColumns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.quick_settings_panel_settings);
        mContext = getActivity();
        mResolver = mContext.getContentResolver();
        mEosQuickPanelView = (EosMultiSelectListPreference) findPreference(TILEPICKER);
        if (mEosQuickPanelView != null) {
            mEosQuickPanelView.setOnPreferenceChangeListener(this);
            mEosQuickPanelView.setEntries(getResources().getStringArray(
                    R.array.eos_panel_enabled_names));
            mEosQuickPanelView.setEntryValues(getResources().getStringArray(
                    R.array.eos_panel_enabled_preferences));
            mEosQuickPanelView.setReturnFullList(true);
            populateEosPanelList();
        }
        mColumns = (ListPreference) findPreference(COLUMNS);
        if (mColumns != null) {
            mColumns.setOnPreferenceChangeListener(this);
            mColumns.setDefaultValue(String.valueOf((Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_PANEL_COLUMN_COUNT,
                    EOSConstants.SYSTEMUI_PANEL_COLUMNS_DEF))));
        }
    }

    private void populateEosPanelList() {
        LinkedHashSet<String> selectedValues = new LinkedHashSet<String>();
        String enabledControls = Settings.System.getString(mContext.getContentResolver(),
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

        if (!EOSUtils.hasTorch()) {
            mEosQuickPanelView.removeValueEntry(EOSConstants.SYSTEMUI_PANEL_TORCH_TILE);
        }

        if(!EOSUtils.isCdmaLTE(mContext)) {
            mEosQuickPanelView.removeValueEntry(EOSConstants.SYSTEMUI_PANEL_LTE_TILE);
        }
        mEosQuickPanelView.setEnabled(true);
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object newValue) {
        if (TILEPICKER.equals(pref.getKey())) {
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
        } else if (COLUMNS.equals(pref.getKey())) {
            int val = Integer.parseInt(((String) newValue).toString());
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_PANEL_COLUMN_COUNT,
                    (val));
            return true;
        }
        return false;
    }

}
