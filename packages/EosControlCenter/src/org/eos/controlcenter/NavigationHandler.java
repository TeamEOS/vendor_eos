
package org.eos.controlcenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.database.ContentObserver;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

public class NavigationHandler extends PreferenceScreenHandler {
    private static final String CATEGORY_VISIBLE_KEY = "eos_navbar_visibility";
    private static final String CATEGORY_STYLE_KEY = "eos_navbar_style";
    private static final String CATEGORY_ACTIONS_KEY = "eos_navbar_actions";
    private static final String HIDE_BARS_KEY = "eos_interface_hide_navbar";
    private static final String HIDE_BARS_BOOT_KEY = "eos_interface_hide_navbar_on_boot";
    private static final String HIDE_BARS_STATBAR_KEY = "eos_interface_hide_statbar_too";
    private static final String STYLE_TABLET_KEY = "eos_interface_navbar_tablet_style";
    private static final String STYLE_SIZE_KEY = "eos_interface_navbar_size";
    private static final String STYLE_NX_KEY = "eos_interface_navbar_nx_style";

    OnActivityRequestedListener mListener;
    ContentObserver mBarHidingObserver;

    PreferenceCategory pc_visible;
    PreferenceCategory pc_style;
    PreferenceCategory pc_action;
    ListPreference mLowProfileNavBar;
    CheckBoxPreference mHideNavBar;
    CheckBoxPreference mHideBarsOnBoot;
    CheckBoxPreference mHideStatbarToo;
    CheckBoxPreference mTabletStyleBar;
    CheckBoxPreference mNxStyleBar;
    Preference mSoftKeyActions;
    Preference mSearchPanelActions;

    public NavigationHandler(PreferenceScreen pref, OnActivityRequestedListener listener) {
        super(pref);
        mListener = listener;
        init();
    }

    protected void init() {
        pc_visible = (PreferenceCategory) mRoot.findPreference(CATEGORY_VISIBLE_KEY);
        pc_style = (PreferenceCategory) mRoot.findPreference(CATEGORY_STYLE_KEY);
        pc_action = (PreferenceCategory) mRoot.findPreference(CATEGORY_ACTIONS_KEY);

        mHideNavBar = (CheckBoxPreference) pc_visible.findPreference(HIDE_BARS_KEY);
        mHideBarsOnBoot = (CheckBoxPreference) pc_visible.findPreference(HIDE_BARS_BOOT_KEY);
        mHideStatbarToo = (CheckBoxPreference) pc_visible.findPreference(HIDE_BARS_STATBAR_KEY);
        mTabletStyleBar = (CheckBoxPreference) pc_style.findPreference(STYLE_TABLET_KEY);
        mLowProfileNavBar = (ListPreference) pc_style.findPreference(STYLE_SIZE_KEY);
        mNxStyleBar = (CheckBoxPreference) pc_style.findPreference(STYLE_NX_KEY);
        mSoftKeyActions = (Preference) pc_action.findPreference(Utils.SOFTKEY_FRAG_TAG);
        mSearchPanelActions = (Preference) pc_action.findPreference(Utils.SEARCH_PANEL_FRAG_TAG);

        // remove softkey left side feature on all phones
        if (EOSUtils.isNormalScreen()) {
            pc_style.removePreference(mTabletStyleBar);
            mTabletStyleBar = null;
        }

        // visibility category
        mHideNavBar.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_HIDE_BARS,
                EOSConstants.SYSTEMUI_HIDE_BARS_DEF) == 1);

        mHideBarsOnBoot.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_HIDE_NAVBAR_ON_BOOT,
                EOSConstants.SYSTEMUI_HIDE_NAVBAR_ON_BOOT_DEF) == 1);

        mHideStatbarToo.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_HIDE_STATBAR_TOO,
                EOSConstants.SYSTEMUI_HIDE_STATBAR_TOO_DEF) == 1);

        // style category
        // initialize if available
        if (mTabletStyleBar != null) {
            mTabletStyleBar.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR,
                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR_DEF) == 1);
        }

        mLowProfileNavBar.setValue(String.valueOf((Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_BAR_SIZE_MODE, 0))));

        mNxStyleBar.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_USE_NX_NAVBAR,
                EOSConstants.SYSTEMUI_USE_NX_NAVBAR_DEF) == 1);

        // set initial feature enabled/disabled state
        updateEnabledState();

        // we need an observer in case bars get hidden from
        // some other source. Then we can adjust accordingly
        mBarHidingObserver = new ContentObserver(new Handler()) {

            @Override
            public void onChange(boolean selfChange) {
                mHideNavBar.setChecked(Settings.System.getInt(mResolver,
                        EOSConstants.SYSTEMUI_HIDE_BARS,
                        EOSConstants.SYSTEMUI_HIDE_BARS_DEF) == 1);
                updateEnabledState();
            }
        };

        mResolver.registerContentObserver(
                Settings.System.getUriFor(
                        EOSConstants.SYSTEMUI_HIDE_BARS), false, mBarHidingObserver);

        // set our preference change listeners
        // visibility category
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

        // style category
        if (mTabletStyleBar != null) {
            mTabletStyleBar
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean enabled = ((Boolean) newValue).booleanValue();
                            Settings.System.putInt(mResolver,
                                    EOSConstants.SYSTEMUI_USE_HYBRID_STATBAR, enabled ? 1 : 0);
                            mTabletStyleBar.setChecked(enabled);
                            updateEnabledState();
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

        mLowProfileNavBar
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Settings.System.putInt(mResolver,
                                EOSConstants.SYSTEMUI_BAR_SIZE_MODE,
                                Integer.parseInt(((String) newValue)));
                        return true;
                    }
                });

        mNxStyleBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean enabled = ((Boolean) newValue).booleanValue();
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_USE_NX_NAVBAR, enabled ? 1 : 0);
                mNxStyleBar.setChecked(enabled);
                updateEnabledState();
                return true;
            }
        });

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

    private void updateEnabledState() {
        // diable everything except hidebar toggle is bars are hidden
        boolean isNavbarHidden = mHideNavBar.isChecked();
        enableAllCategoryChilds(pc_visible, HIDE_BARS_KEY, !isNavbarHidden);
        pc_style.setEnabled(!isNavbarHidden);
        pc_action.setEnabled(!isNavbarHidden);
        if (isNavbarHidden) return;
        if (mTabletStyleBar != null) {
            mNxStyleBar.setEnabled(!mTabletStyleBar.isChecked());
            mTabletStyleBar.setEnabled(!mNxStyleBar.isChecked());
        }
        pc_action.setEnabled(!mNxStyleBar.isChecked());
    }
}
