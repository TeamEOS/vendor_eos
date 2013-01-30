
package org.eos.controlcenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.database.ContentObserver;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

public class NavigationBarHandler extends PreferenceScreenHandler {
    OnActivityRequestedListener mListener;
    ContentObserver mBarHidingObserver;

    PreferenceCategory pc;
    CheckBoxPreference mLowProfileNavBar;
    CheckBoxPreference mHideNavBar;
    CheckBoxPreference mHideBarsOnBoot;
    CheckBoxPreference mHideStatbarToo;
    CheckBoxPreference mTabletStyleBar;
    Preference mSoftKeyActions;
    Preference mSearchPanelActions;

    public NavigationBarHandler(PreferenceScreen pref, OnActivityRequestedListener listener) {
        super(pref);
        mListener = listener;
        init();
    }

    protected void init() {
        pc = (PreferenceCategory) mRoot.findPreference("eos_interface_navbar");
        mLowProfileNavBar = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_navbar_low_profile");
        mHideNavBar = (CheckBoxPreference) mRoot.findPreference("eos_interface_hide_navbar");
        mHideBarsOnBoot = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_hide_navbar_on_boot");
        mHideStatbarToo = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_hide_statbar_too");
        mTabletStyleBar = (CheckBoxPreference) mRoot
                .findPreference("eos_interface_navbar_tablet_style");
        mSoftKeyActions = (Preference) mRoot.findPreference(Utils.SOFTKEY_FRAG_TAG);
        mSearchPanelActions = (Preference) mRoot.findPreference(Utils.SEARCH_PANEL_FRAG_TAG);

        // remove softkey left side feature on all phones
        if (EOSUtils.isNormalScreen()) {
            pc.removePreference(mTabletStyleBar);
            mTabletStyleBar = null;
        }

        mLowProfileNavBar.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_BAR_SIZE_MODE, 0) == 1);

        mHideNavBar.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_HIDE_BARS,
                EOSConstants.SYSTEMUI_HIDE_BARS_DEF) == 1);

        // Grey out other NavBar related options if Hiding NavBar
        if (pc != null)
            enableAllCategoryChilds(pc, "eos_interface_hide_navbar", !mHideNavBar.isChecked());
        // good spot to set up observer too
        mBarHidingObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                mHideNavBar.setChecked(Settings.System.getInt(mResolver,
                        EOSConstants.SYSTEMUI_HIDE_BARS,
                        EOSConstants.SYSTEMUI_HIDE_BARS_DEF) == 1);
                if (pc != null)
                    enableAllCategoryChilds(pc, "eos_interface_hide_navbar",
                            !mHideNavBar.isChecked());
            }
        };

        mResolver.registerContentObserver(
                Settings.System.getUriFor(
                        EOSConstants.SYSTEMUI_HIDE_BARS), false, mBarHidingObserver);

        if (mHideBarsOnBoot != null) {
            mHideBarsOnBoot.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_HIDE_NAVBAR_ON_BOOT,
                    EOSConstants.SYSTEMUI_HIDE_NAVBAR_ON_BOOT_DEF) == 1);
        }

        if (mHideStatbarToo != null) {
            mHideStatbarToo.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_HIDE_STATBAR_TOO,
                    EOSConstants.SYSTEMUI_HIDE_STATBAR_TOO_DEF) == 1);
        }

        // initialize if available
        if (mTabletStyleBar != null) {
            mTabletStyleBar.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR,
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR_DEF) == 1);
        }

        mLowProfileNavBar
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_BAR_SIZE_MODE,
                                ((Boolean) newValue).booleanValue() ? 1 : 0);
                        return true;
                    }
                });

        mHideNavBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mContext.sendBroadcast(new Intent()
                        .setAction(EOSConstants.INTENT_SYSTEMUI_BAR_STATE_REQUEST_TOGGLE));
                return true;
            }
        });

        mHideBarsOnBoot.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_HIDE_NAVBAR_ON_BOOT,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        mHideStatbarToo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_HIDE_STATBAR_TOO,
                        ((Boolean) newValue).booleanValue() ? 1 : 0);
                return true;
            }
        });

        if (mTabletStyleBar != null) {
            mTabletStyleBar
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Settings.System.putInt(mResolver,
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
                                    Intent intent = new Intent()
                                            .setAction(EOSConstants.INTENT_EOS_CONTROL_CENTER);
                                    intent.putExtra(
                                            EOSConstants.INTENT_EOS_CONTROL_CENTER_EXTRAS_STATE,
                                            true);
                                    mContext.sendBroadcast(intent);
                                }
                            }, 250);
                            return true;
                        }
                    });
        }

        mSoftKeyActions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mListener.onActivityRequested(Utils.SOFTKEY_FRAG_TAG);
                return true;
            }
        });

        mSearchPanelActions
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        mListener.onActivityRequested(Utils.SEARCH_PANEL_FRAG_TAG);
                        return true;
                    }
                });
    }

    private void enableAllCategoryChilds(PreferenceCategory pc, String keyToExclude, boolean enabled) {
        int nbPrefs = pc.getPreferenceCount();
        for (int pref = 0; pref < nbPrefs; pref++)
            if (!pc.getPreference(pref).getKey().equals(keyToExclude))
                pc.getPreference(pref).setEnabled(enabled);
    }

}
