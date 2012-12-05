
package org.eos.controlcenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

public class Privacy extends PreferenceFragment implements
        OnPreferenceChangeListener {

    private final String LOGGERPACKAGES = "eos_logger_packages";

    public static final String UIDS_MODE_FILE = "uids_mode";
    public static final String UIDS_LIST_FILE = "uids_list";

    public static final String UIDS_MODE_NORMAL = "normal";
    public static final String UIDS_MODE_BLACKLIST = "blacklist";
    public static final String UIDS_MODE_WHITELIST = "whitelist";

    private Context mContext;
    private ListPreference mLoggerModePreference;
    private String mLoggerModeValuesArray[];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        addPreferencesFromResource(R.xml.privacy_settings);

        mLoggerModeValuesArray = mContext.getResources().getStringArray(
                R.array.eos_logger_mode_values);
        mLoggerModePreference = (ListPreference) findPreference("eos_logger_mode");
        mLoggerModePreference.setOnPreferenceChangeListener(this);

        File loggingMode = new File(mContext.getDir("eos", Context.MODE_PRIVATE), UIDS_MODE_FILE);
        String input = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(loggingMode));
            input = br.readLine();
            br.close();
        } catch (IOException e) {
        }

        if (input != null && input.equals(UIDS_MODE_NORMAL)) {
            mLoggerModePreference.setValueIndex(0);
            setLoggerListEnabled(UIDS_MODE_NORMAL);
        } else if (input != null && input.equals(UIDS_MODE_BLACKLIST)) {
            mLoggerModePreference.setValueIndex(1);
            setLoggerListEnabled(UIDS_MODE_BLACKLIST);
        } else if (input != null && input.equals(UIDS_MODE_WHITELIST)) {
            mLoggerModePreference.setValueIndex(2);
            setLoggerListEnabled(UIDS_MODE_WHITELIST);
        } else {
            mLoggerModePreference.setValueIndex(0);
            setLoggerListEnabled(UIDS_MODE_NORMAL);
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.eos_privacy_warning_title)
                .setMessage(R.string.eos_privacy_warning_message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mLoggerModePreference)) {
            File loggingMode = new File(mContext.getDir("eos", Context.MODE_PRIVATE),
                    UIDS_MODE_FILE);
            setLoggerListEnabled((String) newValue);
            try {
                FileWriter writer = new FileWriter(loggingMode);
                writer.write((String) newValue + "\n");
                writer.close();
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void setLoggerListEnabled(String mode) {
        findPreference("eos_logger_packages").setEnabled(!mode.equals(UIDS_MODE_NORMAL));
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        super.onPreferenceTreeClick(prefScreen, pref);
        if (pref.getKey().equals(LOGGERPACKAGES)) {
            Main.showFragment("Log", new LoggerPackages());
            return true;
        }
        return false;
    }
}
