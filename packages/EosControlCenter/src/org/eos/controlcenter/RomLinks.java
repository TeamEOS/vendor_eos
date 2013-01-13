
package org.eos.controlcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class RomLinks extends PreferenceFragment {

    private final String XDA = "xda_thread";
    private final String ROOTZ = "rootz_thread";

    private String mDevice;
    private String mXdaUrl;
    private String mRootzUrl;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rom_links);
        mContext = (Context) getActivity();
        mDevice = Utils.getDevice();
        mXdaUrl = Utils.getXdaUrl(mContext, mDevice);
        mRootzUrl = Utils.getRootzUrl(mContext, mDevice);

        Preference pXda = findPreference(XDA);
        pXda.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mXdaUrl));
                startActivity(intent);
                return false;
            }
        });

//        Preference pRootz = findPreference(ROOTZ);

//        Disable until we get rootz links
//        pRootz.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mRootzUrl));
//                startActivity(intent);
//                return false;
//            }
//        });

        pXda.setSummary("Detected Device: " + mDevice);
//        pRootz.setSummary("Detected Device: " + mDevice);
    }
}
