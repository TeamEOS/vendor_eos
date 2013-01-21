
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
    
    private static final String WIFI_IDLE_MS = "wifi_idle_ms";

    private boolean hasDeviceSettings;
    private boolean isCrtOffChecked = false;
    private CheckBoxPreference mVolumeKeysSwitch;
    private CheckBoxPreference mVolumeKeysMusicControl;
    private CheckBoxPreference mBatteryWarning;
    private CheckBoxPreference mScreenCharging;
    private CheckBoxPreference mCrtOff;
    private CheckBoxPreference mCrtOn;
    private ListPreference mWifiChannelsPreference;
    private ListPreference mDefaultVolumeStreamPreference;
    private ListPreference mScreenshotFactor;
    private ListPreference mWifiIdleMs;
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

        boolean unplugTurnsOnScreen = mContext.getResources().getBoolean(com.android.internal.R.bool.config_unplugTurnsOnScreen);
        mScreenCharging = (CheckBoxPreference) findPreference("eos_system_power_unplug_screen_off");
        mScreenCharging.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_POWER_DONT_WAKE_DEVICE_PLUGGED,
                unplugTurnsOnScreen ? 0 : 1) == 1);
        mScreenCharging.setOnPreferenceChangeListener(this);

        // respect device default configuration
        // true fades while false animates
        boolean electronBeamFadesConfig = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_animateScreenLights);

        // use this to enable/disable crt on feature
        // crt only works if crt off is enabled
        // total system failure if only crt on is enabled
        isCrtOffChecked = Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_POWER_ENABLE_CRT_OFF,
                electronBeamFadesConfig ? 0 : 1) == 1;

        mCrtOff = (CheckBoxPreference) findPreference("eos_system_power_crt_screen_off");
        mCrtOff.setChecked(isCrtOffChecked);
        mCrtOff.setOnPreferenceChangeListener(this);

        mCrtOn = (CheckBoxPreference) findPreference("eos_system_power_crt_screen_on");
        mCrtOn.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_POWER_ENABLE_CRT_ON, 0) == 1);
        mCrtOn.setEnabled(isCrtOffChecked);
        mCrtOn.setOnPreferenceChangeListener(this);

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
        
        mWifiIdleMs = (ListPreference) findPreference("eos_wifi_idle_ms");
        mWifiIdleMs.setOnPreferenceChangeListener(this);
        mWifiIdleMs.setValue(String.valueOf(Settings.Secure.getLong(mContext.getContentResolver(), WIFI_IDLE_MS, 900000)));
        
        mScreenshotFactor = (ListPreference) findPreference(KEY_SCREENSHOT_FACTOR);
        mScreenshotFactor.setOnPreferenceChangeListener(this);
        int currentVal = Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_SCREENSHOT_SCALE_INDEX, 0);
        mScreenshotFactor.setValue(String.valueOf(currentVal));
        updateScreenshotFactorSummary(currentVal);

        mBatteryWarning = (CheckBoxPreference) findPreference("eos_system_disable_battery_warning");
        mBatteryWarning.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEM_DISABLE_LOW_BATTERY_WARNING,
                EOSConstants.SYSTEM_DISABLE_LOW_BATTERY_WARNING_DEF)
                == EOSConstants.SYSTEM_DISABLE_LOW_BATTERY_WARNING_DEF ? false : true);
        mBatteryWarning.setOnPreferenceChangeListener(this);

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
        } else if (mBatteryWarning.equals(preference)) {
            Settings.System.putInt(mResolver,
                    EOSConstants.SYSTEM_DISABLE_LOW_BATTERY_WARNING,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
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
        } else if (mWifiIdleMs.equals(preference)) {
            Settings.Secure.putLong(mContext.getContentResolver(), WIFI_IDLE_MS, Long.valueOf((String) newValue)); 
        } else if (mScreenCharging.equals(preference)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEM_POWER_DONT_WAKE_DEVICE_PLUGGED,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
            return true;
        } else if (mCrtOff.equals(preference)) {
            isCrtOffChecked = ((Boolean) newValue).booleanValue();
            Settings.System.putInt(mResolver, EOSConstants.SYSTEM_POWER_ENABLE_CRT_OFF,
                    (isCrtOffChecked ? 1 : 0));
            // if crt off gets turned off, crt on gets turned off and disabled
            if (!isCrtOffChecked) {
                Settings.System.putInt(mResolver, EOSConstants.SYSTEM_POWER_ENABLE_CRT_ON, 0);
                mCrtOn.setChecked(false);
            }
            mCrtOn.setEnabled(isCrtOffChecked);
            return true;
        } else if (mCrtOn.equals(preference)) {
            Settings.System.putInt(mResolver, EOSConstants.SYSTEM_POWER_ENABLE_CRT_ON,
                    ((Boolean) newValue).booleanValue() ? 1 : 0);
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
