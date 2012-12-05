
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.IWindowManager;

import org.teameos.jellybean.settings.EOSConstants;

public class NavigationAreaActions extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    ColorPreference mNavBar;
    Preference mStockColor;
    Context mContext;
    ContentResolver mResolver;
    boolean mHasNavBar = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (Context) getActivity();
        mResolver = mContext.getContentResolver();

        IWindowManager mWindowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            mHasNavBar = mWindowManager.hasNavigationBar();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addPreferencesFromResource(R.xml.navbar_settings);
        PreferenceScreen ps = this.getPreferenceScreen();
        PreferenceCategory pc = (PreferenceCategory) ps.findPreference("eos_navbar_appearance");
        pc.setTitle(mHasNavBar ? R.string.eos_interface_navbar_appearance
                : R.string.eos_interface_systembar_appearance);

        mNavBar = (ColorPreference) findPreference("eos_interface_navbar_color");
        mNavBar.setProviderTarget(EOSConstants.SYSTEMUI_NAVBAR_COLOR,
                EOSConstants.SYSTEMUI_NAVBAR_COLOR_DEF);

        mStockColor = (Preference) findPreference("eos_interface_navbar_color_default");
        mStockColor.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        // TODO Auto-generated method stub
        if (pref.equals(mStockColor)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVBAR_COLOR,
                    EOSConstants.SYSTEMUI_NAVKEY_COLOR_DEF);
        }
        return false;
    }
}
