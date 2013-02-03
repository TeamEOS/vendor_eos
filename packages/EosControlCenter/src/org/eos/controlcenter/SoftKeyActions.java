
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class SoftKeyActions extends ActionFragment
        implements Preference.OnPreferenceChangeListener {

    public static SoftKeyActions newInstance(Bundle args) {
        SoftKeyActions frag = new SoftKeyActions();
        if (args != null) {
            args.putString(Utils.FRAG_TITLE_KEY, "Softkey Actions");
        }
        frag.setArguments(args);
        return frag;
    }

    public static SoftKeyActions newInstance() {
        SoftKeyActions frag = new SoftKeyActions();
        Bundle args = new Bundle();
        args.putString(Utils.FRAG_TITLE_KEY, "Softkey Actions");
        frag.setArguments(args);
        return frag;
    }

    public SoftKeyActions(Bundle args) {
        newInstance(args);
    }

    public SoftKeyActions() {
    }

    Context mContext;
    ContentResolver mResolver;

    CheckBoxPreference mEnable;
    CheckBoxPreference mMenuPersist;

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

    @Override
    public void onPackagesUpdated(int percent) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPackagesLoaded(boolean loaded) {
        // TODO Auto-generated method stub
        
    }

}
