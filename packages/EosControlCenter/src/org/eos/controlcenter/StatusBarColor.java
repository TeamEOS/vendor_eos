
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class StatusBarColor extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    ColorPreference mStatBar;
    Preference mStockColor;
    Context mContext;
    ContentResolver mResolver;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (Context) getActivity();
        mResolver = mContext.getContentResolver();
        addPreferencesFromResource(R.xml.statusbar_color);

        mStatBar = (ColorPreference) findPreference("eos_interface_navbar_color");
        mStatBar.setProviderTarget(EOSConstants.SYSTEMUI_STATUSBAR_COLOR,
                EOSConstants.SYSTEMUI_STATUSBAR_COLOR_DEF);

        mStockColor = (Preference) findPreference("eos_interface_navbar_color_default");
        mStockColor.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        // TODO Auto-generated method stub
        if (pref.equals(mStockColor)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_STATUSBAR_COLOR, -1);
        }
        return false;
    }
}
