/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eos.controlcenter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

public class Performance extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    
    public static Performance newInstance(Bundle args) {
        Performance frag = new Performance();
        if (args != null) {
            args.putString(Utils.PERFORMANCE_FRAG_TAG, "Performance");
        }
        frag.setArguments(args);
        return frag;
    }

    public static Performance newInstance() {
        Performance frag = new Performance();
        Bundle args = new Bundle();
        args.putString(Utils.PERFORMANCE_FRAG_TAG, "Performance");
        frag.setArguments(args);
        return frag;
    }

    public Performance(Bundle args) {
        newInstance(args);
    }

    public Performance() {
    }

    private CheckBoxPreference mClocksOnBootPreference;
    private ListPreference mClocksMinPreference;
    private ListPreference mClocksMaxPreference;
    private ListPreference mClocksGovPreference;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.performance_settings);

        mContext = (Context) getActivity();

        File dataDirectory = mContext.getDir("eos", Context.MODE_PRIVATE);
        mClocksOnBootPreference = (CheckBoxPreference) findPreference("eos_performance_cpu_set_on_boot");
        mClocksOnBootPreference.setOnPreferenceChangeListener(this);
        File overclockingFile = new File(dataDirectory.getAbsolutePath() + File.separator
                + "clocks_on_boot");
        mClocksOnBootPreference.setChecked(overclockingFile.exists());

        mClocksMinPreference = (ListPreference) findPreference("eos_performance_cpu_min");
        mClocksMaxPreference = (ListPreference) findPreference("eos_performance_cpu_max");
        mClocksMinPreference.setOnPreferenceChangeListener(this);
        mClocksMaxPreference.setOnPreferenceChangeListener(this);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies"));
            String[] frequencies = reader.readLine().split(" ");
            reader.close();
            for (int i = 0; i < frequencies.length; i++) {
                frequencies[i] = "" + (Integer.parseInt(frequencies[i]) / 1000) + " MHz";
            }
            mClocksMinPreference.setEntries(frequencies);
            mClocksMinPreference.setEntryValues(frequencies);
            mClocksMaxPreference.setEntries(frequencies);
            mClocksMaxPreference.setEntryValues(frequencies);
        } catch (IOException e) {
        }

        mClocksGovPreference = (ListPreference) findPreference("eos_performance_cpu_governor");
        mClocksGovPreference.setOnPreferenceChangeListener(this);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors"));
            String[] governors = reader.readLine().split(" ");
            reader.close();
            mClocksGovPreference.setEntries(governors);
            mClocksGovPreference.setEntryValues(governors);
        } catch (IOException e) {
            mClocksGovPreference.setEntries(new String[] {
                    "interactive", "ondemand"
            });
            mClocksGovPreference.setEntryValues(new String[] {
                    "interactive", "ondemand"
            });
        }

        /* Overclocking - setup the selected values */
        try {
            updateCpuPreferenceValues(mClocksMinPreference, CLOCK_TYPE.MIN);
            updateCpuPreferenceValues(mClocksMaxPreference, CLOCK_TYPE.MAX);
            updateCpuPreferenceValues(mClocksGovPreference, CLOCK_TYPE.GOV);
        } catch (IOException e) {
            Log.w("Settings", e.toString());
        }

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference.equals(mClocksOnBootPreference)) {
            if (((Boolean) objValue).booleanValue()) {
                File onBoot = new File(mContext.getDir("eos", Context.MODE_PRIVATE),
                        "clocks_on_boot");
                try {
                    onBoot.createNewFile();
                } catch (IOException e) {
                    Log.d("Settings", e.toString());
                    return false;
                }
            } else {
                new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_on_boot").delete();
                new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_min").delete();
                new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_max").delete();
                new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_gov").delete();
            }

            try {
                updateCpuPreferenceValues(mClocksMinPreference, CLOCK_TYPE.MIN);
            } catch (Exception e) {
            }
            try {
                updateCpuPreferenceValues(mClocksMaxPreference, CLOCK_TYPE.MAX);
            } catch (Exception e) {
            }
            try {
                updateCpuPreferenceValues(mClocksGovPreference, CLOCK_TYPE.GOV);
            } catch (Exception e) {
            }
            return true;
        } else if (preference.equals(mClocksMinPreference) ||
                preference.equals(mClocksMaxPreference)) {
            try {
                CLOCK_TYPE clockType = null;
                File bootFile = null;
                if (preference.equals(mClocksMinPreference)) {
                    clockType = CLOCK_TYPE.MIN;
                    bootFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_min");
                }
                else {
                    clockType = CLOCK_TYPE.MAX;
                    bootFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_max");
                }

                String newValue = (String) objValue;
                String output = "" + (Integer.parseInt(newValue.substring(0,
                        newValue.indexOf(" MHz"))) * 1000);

                BufferedWriter writer = new BufferedWriter(new FileWriter(bootFile));
                writer.write(output.toCharArray(), 0, output.toCharArray().length);
                writer.close();

                if (!writeToCpuFiles(clockType, output))
                    return false; // Writing the files failed, so don't proceed
                                  // further.

                updateCpuPreferenceValues((ListPreference) preference, clockType);
            } catch (IOException e) {
                Log.d("Settings", e.toString());
                return false;
            }

            return true;
        } else if (preference.equals(mClocksGovPreference)) {
            try {
                String newValue = (String) objValue;
                File out = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_gov");
                BufferedWriter writer = new BufferedWriter(new FileWriter(out));
                writer.write(newValue.toCharArray(), 0, newValue.toCharArray().length);
                writer.close();

                if (!writeToCpuFiles(CLOCK_TYPE.GOV, newValue))
                    return false; // Writing the files failed, so don't proceed
                                  // further.

                updateCpuPreferenceValues(mClocksGovPreference, CLOCK_TYPE.GOV);
            } catch (IOException e) {
                Log.d("Settings", e.toString());
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Dynamically write the values to the specific cpufrequency files according
     * to the number of cpu's available at run time.
     * 
     * @param fileName String of the file in the cpufrequency directory that you
     *            want to write to.
     * @param contents String of the contents you want to write.
     * @return boolean of whether the writes completed or failed miserably.
     */
    private boolean writeToCpuFiles(CLOCK_TYPE clockType, String contents) {
        File outputFile = null;

        switch (clockType) {
            case MIN:
                outputFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
                break;
            case MAX:
                outputFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                break;
            case GOV:
                outputFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
                break;
        }

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(contents.toCharArray(), 0, contents.toCharArray().length);
            } catch (IOException e) {
                return false;
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    return false; // We can't do anything if we hit an exception
                                  // while closing.
                }
            }
        }
        return true;
    }

    private void updatePreferenceSummary(Preference preference, String currentValue,
            String bootValue) {
        if (preference == null || currentValue == null)
            throw new IllegalArgumentException(
                    "Preference and currentValue variables cannot be null.");

        StringBuilder newSummary = new StringBuilder();
        newSummary.append(getResources().getString(R.string.eos_performance_current_value));
        newSummary.append(": ");
        newSummary.append(currentValue);

        if (bootValue != null) {
            newSummary.append(" ");
            newSummary.append(getResources().getString(R.string.eos_performance_boot_value));
            newSummary.append(" ");
            newSummary.append(bootValue);
        }

        preference.setSummary(newSummary.toString());
    }

    private void updateCpuPreferenceValues(ListPreference preference, CLOCK_TYPE clockType)
            throws IOException {
        BufferedReader reader = null;
        String input = null, currentValue = null, bootValue = null;
        File currentClockFile = null, bootClockFile = null;

        switch (clockType) {
            case MIN:
                currentClockFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
                bootClockFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_min");
                break;
            case MAX:
                currentClockFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
                bootClockFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_max");
                break;
            case GOV:
                currentClockFile = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor");
                bootClockFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_gov");
                break;
        }

        reader = new BufferedReader(new FileReader(currentClockFile));
        input = reader.readLine();
        reader.close();
        if (clockType == CLOCK_TYPE.MIN || clockType == CLOCK_TYPE.MAX)
            currentValue = (Integer.parseInt(input) / 1000) + " MHz";
        else
            currentValue = input;
        preference.setValue(currentValue);

        if (bootClockFile.exists() && new File(mContext.getDir("eos", Context.MODE_PRIVATE),
                "clocks_on_boot").exists()) {
            reader = new BufferedReader(new FileReader(bootClockFile));
            bootValue = reader.readLine();
            reader.close();
            if (bootValue != null && (clockType == CLOCK_TYPE.MIN || clockType == CLOCK_TYPE.MAX)) {
                try {
                    bootValue = (Integer.parseInt(bootValue) / 1000) + " MHz";
                } catch (NumberFormatException e) {
                    bootClockFile.delete();
                    bootValue = null;
                } catch (NullPointerException e) {
                    bootClockFile.delete();
                    bootValue = null;
                }
            }
        }
        updatePreferenceSummary(preference, currentValue, bootValue);
    }

    private enum CLOCK_TYPE {
        MIN, MAX, GOV;
    }
}
