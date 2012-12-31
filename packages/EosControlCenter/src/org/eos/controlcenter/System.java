
package org.eos.controlcenter;

import org.teameos.jellybean.settings.EOSConstants;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.IWindowManager;

public class System extends PreferenceFragment implements OnPreferenceChangeListener {

    private static final String EOS_DEVICE_SETTINGS = "eos_device_settings";
    private static final String EOS_PERFORMANCE_SETTINGS = "eos_performance_settings";
    private static final String PRIVACY = "eos_privacy_settings";
    private static final String KEY_SCREENSHOT_FACTOR = "screenshot_scaling";

    private boolean hasDeviceSettings;
    private CheckBoxPreference mVolumeKeysSwitch;
    private CheckBoxPreference mVolumeKeysMusicControl;
    private ListPreference mWifiChannelsPreference;
    private ListPreference mDefaultVolumeStreamPreference;
    private ListPreference mScreenshotFactor;
    private EditTextPreference mHostnamePreference;

    private Context mContext;
    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.system_settings);

        mContext = getActivity();
        mResolver = mContext.getContentResolver();

        hasDeviceSettings = this.getResources().getBoolean(R.bool.config_hasDeviceSettings);
        PreferenceScreen root = getPreferenceScreen();

        if (!hasDeviceSettings) {
            Preference ps = (Preference) findPreference(EOS_DEVICE_SETTINGS);
            if (ps != null)
                root.removePreference(ps);
        }

        mVolumeKeysSwitch = (CheckBoxPreference) findPreference("eos_system_volume_keys_rotate");
        mVolumeKeysSwitch.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_VOLUME_KEYS_SWITCH_ON_ROTATION,
                EOSConstants.SYSTEM_VOLUME_KEYS_SWITCH_ON_ROTATION_DEF) == 1);
        mVolumeKeysSwitch.setOnPreferenceChangeListener(this);

        mVolumeKeysMusicControl = (CheckBoxPreference) findPreference("eos_system_volume_keys_music_control");
        mVolumeKeysMusicControl.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_VOLUME_KEYS_MUSIC_CONTROL, 1) == 1);
        mVolumeKeysMusicControl.setOnPreferenceChangeListener(this);

        mWifiChannelsPreference = (ListPreference) findPreference("eos_wifi_regulatory_domain_selector");
        mWifiChannelsPreference.setOnPreferenceChangeListener(this);

        mDefaultVolumeStreamPreference = (ListPreference) findPreference("eos_system_default_volume_stream");
        mDefaultVolumeStreamPreference.setOnPreferenceChangeListener(this);
        String currentStreamValue = Settings.System.getString(mResolver,
                EOSConstants.SYSTEM_DEFAULT_VOLUME_STREAM);
        if (!"media".equals(currentStreamValue)) {
            currentStreamValue = "default";
        }
        mDefaultVolumeStreamPreference.setValue(currentStreamValue);

        mScreenshotFactor = (ListPreference) findPreference(KEY_SCREENSHOT_FACTOR);
        mScreenshotFactor.setOnPreferenceChangeListener(this);
        int currentVal = Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_SCREENSHOT_SCALE_INDEX, 0);
        mScreenshotFactor.setValue(String.valueOf(currentVal));
        updateScreenshotFactorSummary(currentVal);

        mHostnamePreference = (EditTextPreference) findPreference("eos_net_hostname");
        String hostname = Settings.System.getString(mResolver, EOSConstants.NET_HOSTNAME);
        if (TextUtils.isEmpty(hostname))
            hostname = SystemProperties.get("net.hostname");
        mHostnamePreference.setText(hostname);
        mHostnamePreference.setSummary(hostname);
        mHostnamePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mVolumeKeysSwitch.equals(preference)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEM_VOLUME_KEYS_SWITCH_ON_ROTATION,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (mVolumeKeysMusicControl.equals(preference)) {
            Settings.System.putInt(mContext.getContentResolver(),
                    EOSConstants.SYSTEM_VOLUME_KEYS_MUSIC_CONTROL,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (mWifiChannelsPreference.equals(preference)) {
            final String newCountryCode = (String) newValue;
            String currentCountryCode = Settings.Secure.getString(mContext.getContentResolver(),
                    Settings.Global.WIFI_COUNTRY_CODE);
            if (newCountryCode.equals(currentCountryCode)) {
                return false;
            } else {
                handleWifiState(newCountryCode);
                return true;
            }
        } else if (mDefaultVolumeStreamPreference.equals(preference)) {
            Settings.System.putString(mContext.getContentResolver(),
                    EOSConstants.SYSTEM_DEFAULT_VOLUME_STREAM,
                    (String) newValue);
            return true;
        } else if (KEY_SCREENSHOT_FACTOR.equals(preference.getKey())) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_SCREENSHOT_SCALE_INDEX, val);
            updateScreenshotFactorSummary(val);
            return true;
        } else if (mHostnamePreference.equals(preference)) {
            String value = (String) newValue;
            if (!value
                    .matches("^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$")) {
                return false;
            }
            Settings.System.putString(mResolver, EOSConstants.NET_HOSTNAME, value);
            SystemProperties.set("net.hostname", value);
            mHostnamePreference.setSummary(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        super.onPreferenceTreeClick(prefScreen, pref);
        if (pref.getKey().equals(PRIVACY)) {
            Main.showFragment("Privacy", new Privacy());
            return true;
        } else if (pref.getKey().equals(EOS_PERFORMANCE_SETTINGS)) {
            Main.showFragment("Performance", new Performance());
        }
        return false;
    }

    private void handleWifiState(String countryCode) {
        Settings.Secure.putString(mContext.getContentResolver(), Settings.Global.WIFI_COUNTRY_CODE,
                countryCode);
        mWifiChannelsPreference.setValue(String.valueOf(countryCode));
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            int delay = 5000;
            final ProgressDialog dialog = ProgressDialog.show(mContext,
                    getString(R.string.eos_wifi_regulatory_domain_changed),
                    getString(R.string.eos_wifi_restarting), true);
            dialog.show();
            wifi.setWifiEnabled(false);
            wifi.setWifiEnabled(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, delay);
        }
    }

    private void updateScreenshotFactorSummary(int val) {
        // little cheat as the percent sign used in the pref entries
        // does not play nice with getStringArray
        String[] mScreenshotEntries = {
                "1.0", "0.75", "0.50", "0.25"
        };
        StringBuilder b = new StringBuilder()
                .append(getResources().getString(R.string.eos_screenshot_scaling_summary))
                .append(mScreenshotEntries[val]);
        mScreenshotFactor.setSummary(b.toString());
    }
}
