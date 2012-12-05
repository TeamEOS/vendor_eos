
package org.eos.controlcenter;

import android.os.Bundle;
import android.provider.Settings;

import org.teameos.jellybean.settings.EOSConstants;

public class NavRingActions extends ActionFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navring_settings);

        addActionPreference((ActionPreference) findPreference("eos_interface_navring_1_release"));
        addActionPreference((ActionPreference) findPreference("eos_interface_navring_2_release"));
        addActionPreference((ActionPreference) findPreference("eos_interface_navring_3_release"));

        String target2 = Settings.System.getString(mContext.getContentResolver(),
                EOSConstants.SYSTEMUI_NAVRING_2);
        if (target2 == null || target2.equals("")) {
            Settings.System.putString(mContext.getContentResolver(),
                    EOSConstants.SYSTEMUI_NAVRING_2, "assist");
        }
    }
}
