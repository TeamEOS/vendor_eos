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
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import org.teameos.jellybean.settings.EOSUtils;

public class Performance extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static Performance newInstance(Bundle args) {
        Performance frag = new Performance();
        if (args != null) {
            args.putString(Utils.FRAG_TITLE_KEY, "Performance");
        }
        frag.setArguments(args);
        return frag;
    }

    public static Performance newInstance() {
        Performance frag = new Performance();
        Bundle args = new Bundle();
        args.putString(Utils.FRAG_TITLE_KEY, "Performance");
        frag.setArguments(args);
        return frag;
    }

    public Performance(Bundle args) {
        newInstance(args);
    }

    public Performance() {
    }

    /*
     * kernel feature path constants. We only use detected paths and features
     */
    // cpu frequency and governors
    private static final String CPU_AVAIL_FREQ = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String CPU_AVAIL_GOV = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    private static final String CPU_MIN_SCALE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
    private static final String CPU_MAX_SCALE = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private static final String CPU_GOV = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

    private static final String IO_SCHED = "/sys/block/mmcblk0/queue/scheduler";
    private File scheduler_file;

    private static final String FC_CATEGORY = "eos_settings_fast_charge";
    private static final String S2W_CATEGORY = "eos_settings_sweep2wake";

    private CheckBoxPreference mClocksOnBootPreference;
    private CheckBoxPreference mIoSchedOnBootPreference;

    private ListPreference mClocksMinPreference;
    private ListPreference mClocksMaxPreference;
    private ListPreference mClocksGovPreference;
    private ListPreference mIoSchedPreference;
    private CheckBoxPreference mFastChargePreference;
    private CheckBoxPreference mEnableSweep2Wake;
    private CheckBoxPreference mSweep2WakeOnBoot;

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

        scheduler_file = new File(IO_SCHED);

        mIoSchedPreference = (ListPreference) findPreference("eos_performance_iosched");
        mIoSchedPreference.setPersistent(false);
        mIoSchedPreference.setOnPreferenceChangeListener(this);

        mIoSchedOnBootPreference = (CheckBoxPreference) findPreference("eos_performance_iosched_on_boot");
        mIoSchedOnBootPreference.setChecked(getSchedulerFlag().exists());
        mIoSchedOnBootPreference.setPersistent(false);
        mIoSchedOnBootPreference.setOnPreferenceChangeListener(this);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CPU_AVAIL_FREQ));
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
            BufferedReader reader = new BufferedReader(new FileReader(CPU_AVAIL_GOV));
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

        if (!EOSUtils.hasFastCharge()) {
            final PreferenceCategory fc = (PreferenceCategory) getPreferenceScreen()
                    .findPreference(FC_CATEGORY);
            getPreferenceScreen().removePreference(fc);
        } else {
            mFastChargePreference = (CheckBoxPreference) findPreference("eos_performance_fast_charge");
            mFastChargePreference.setPersistent(false);
            mFastChargePreference.setChecked(EOSUtils.isFastChargeEnabled());
            mFastChargePreference.setOnPreferenceChangeListener(this);
        }

        if (!EOSUtils.hasSweep2Wake()) {
            final PreferenceCategory s2w = (PreferenceCategory) getPreferenceScreen()
                    .findPreference(S2W_CATEGORY);
            getPreferenceScreen().removePreference(s2w);
        } else {
            mEnableSweep2Wake = (CheckBoxPreference) findPreference("eos_performance_sweep2wake_enable");
            mEnableSweep2Wake.setChecked(EOSUtils.isSweep2WakeEnabled());
            mEnableSweep2Wake.setOnPreferenceChangeListener(this);

            mSweep2WakeOnBoot = (CheckBoxPreference) findPreference("eos_performance_sweep2wake_set_on_boot");
            mSweep2WakeOnBoot.setChecked(getSweep2WakeFlag().exists());
            mSweep2WakeOnBoot.setOnPreferenceChangeListener(this);
        }

        getSchedulers();
    }

    private void getSchedulers() {
        boolean success = false;
        String defSched = "cfq";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    scheduler_file.getAbsolutePath()));
            String[] temp = reader.readLine().split(" ");
            reader.close();
            String[] schedulers = new String[temp.length];
            for (int i = 0; i < temp.length; i++) {
                String test = temp[i];
                if (test.contains("[")) {
                    schedulers[i] = test.substring(1, test.length() - 1);
                    defSched = schedulers[i];
                    success = true;
                    continue;
                }
                schedulers[i] = test;
            }
            if (!success) {
                mIoSchedPreference.setEntries(new String[] {
                        "cfq", "deadline"
                });
                mIoSchedPreference.setEntryValues(new String[] {
                        "cfq", "deadline"
                });
                mIoSchedPreference.setSummary("Unable to read schedulers");
                mIoSchedPreference.setValue(defSched);
            } else {
                mIoSchedPreference.setEntries(schedulers);
                mIoSchedPreference.setEntryValues(schedulers);
                mIoSchedPreference.setSummary("Current value:: " + defSched);
                mIoSchedPreference.setValue(defSched);
            }
        } catch (Exception e) {
            mIoSchedPreference.setEntries(new String[] {
                    "cfq", "deadline"
            });
            mIoSchedPreference.setEntryValues(new String[] {
                    "cfq", "deadline"
            });
            mIoSchedPreference.setSummary("Unable to read schedulers");
            mIoSchedPreference.setValue(defSched);
        }
    }

    private File getSchedulerFlag() {
        return new File(mContext.getDir("eos", Context.MODE_PRIVATE),
                "iosched_on_boot");
    }

    private void writeSchedulerFlag(String scheduler) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getSchedulerFlag()));
            writer.write(scheduler.toCharArray(), 0, scheduler.toCharArray().length);
            writer.close();
        } catch (Exception e) {
            Log.w("Settings", e.toString());
        }
    }

    private void writeScheduler(String scheduler) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(scheduler_file));
            writer.write(scheduler.toCharArray(), 0, scheduler.toCharArray().length);
            writer.close();
        } catch (Exception e) {
            Log.w("Settings", e.toString());
        }
    }

    private File getSweep2WakeFlag() {
        return new File(mContext.getDir("eos", Context.MODE_PRIVATE), "sw2_on_boot");
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
        } else if (preference.equals(mIoSchedPreference)) {
            writeScheduler((String) objValue);
            getSchedulers();
            if (mIoSchedOnBootPreference.isChecked()) {
                writeSchedulerFlag((String) objValue);
            }
        } else if (preference.equals(mIoSchedOnBootPreference)) {
            if (((Boolean) objValue).booleanValue()) {
                try {
                    getSchedulerFlag().createNewFile();
                    writeSchedulerFlag(mIoSchedPreference.getValue());
                } catch (IOException e) {
                    Log.d("Settings", e.toString());
                    return false;
                }
            } else {
                getSchedulerFlag().delete();
            }
        } else if (preference.equals(mEnableSweep2Wake)) {
            EOSUtils.setSweep2WakeEnabled(((Boolean) objValue).booleanValue());
        } else if (preference.equals(mSweep2WakeOnBoot)) {
            if (((Boolean) objValue).booleanValue()) {
                try {
                    getSweep2WakeFlag().createNewFile();
                } catch (IOException e) {
                    Log.d("Settings", e.toString());
                    return false;
                }
            } else {
                getSweep2WakeFlag().delete();
            }
        } else if (preference.equals(mFastChargePreference)) {
            EOSUtils.setFastChargeEnabled(((Boolean) objValue).booleanValue());
        }
        return true;
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
                outputFile = new File(CPU_MIN_SCALE);
                break;
            case MAX:
                outputFile = new File(CPU_MAX_SCALE);
                break;
            case GOV:
                outputFile = new File(CPU_GOV);
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
                currentClockFile = new File(CPU_MIN_SCALE);
                bootClockFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_min");
                break;
            case MAX:
                currentClockFile = new File(CPU_MAX_SCALE);
                bootClockFile = new File(mContext.getDir("eos", Context.MODE_PRIVATE), "clocks_max");
                break;
            case GOV:
                currentClockFile = new File(CPU_GOV);
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
