package org.eos.controlcenter;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Info extends PreferenceFragment {

    private final String XDA = "xda_thread";
    private final String ROOTZ = "rootz_thread";
    
    private String mDevice;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.info);
        
        if (!Main.mTwoPane) {
            addPreferencesFromResource(R.xml.rom_links);
            getDevice();
            
            Preference pXda = findPreference(XDA);
            Preference pRootz = findPreference(ROOTZ);
            
            pXda.setSummary("Detected Device: " + mDevice);
            pRootz.setSummary("Detected Device: " + mDevice);
        }
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen prefScreen, Preference pref) {
        super.onPreferenceTreeClick(prefScreen, pref);
        if (pref.getKey().equals(XDA)) {
            //TODO: launch device thread after thread creation
            return true;
        } else if (pref.getKey().equals(ROOTZ)) {
            //TODO: launch device thread after thread creation
            return true;
        }
        return false;
    }

    private void getDevice() {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/getprop ro.goo.board");

            BufferedReader mBufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = mBufferedReader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            mBufferedReader.close();
            process.waitFor();

            mDevice = output.toString().trim();		
        } catch (Exception e) {
        }
    }
}
