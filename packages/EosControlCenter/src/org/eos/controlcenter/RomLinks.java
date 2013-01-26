
package org.eos.controlcenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.teameos.jellybean.settings.EOSUtils;

public class RomLinks extends PreferenceFragment {

    private final String XDA = "xda_thread";
    private final String ROOTZ = "rootz_thread";
    private static final String LAST_FRAG = "romlinks_last_viewed_frag";
    private static final String ROSTER_TITLE = "Team Roster";

    private String mDevice;
    private String mXdaUrl;
    private String mRootzUrl;

    private Context mContext;

    public static RomLinks newInstance(String lastFrag) {
        RomLinks frag = new RomLinks();
        Bundle b = new Bundle();
        b.putString(LAST_FRAG, lastFrag);
        frag.setArguments(b);
        return frag;
    }
    
    public static RomLinks newInstance() {
        return new RomLinks();
    }
    
    public RomLinks() {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rom_links);
        
        mContext = (Context) getActivity();
        mDevice =EOSUtils.getDevice();
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

    @Override
    public void onStart() {
        super.onStart();
        if (Main.mTwoPane) {
            Main.showFragment(ROSTER_TITLE, Info.newInstance());
        }
    }
}
