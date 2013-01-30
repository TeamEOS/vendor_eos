
package org.eos.controlcenter;

import android.os.Bundle;

public class SystemSettings extends PreferenceListFragment {
    
    public static SystemSettings newInstance(int xml) {
        SystemSettings frag = new SystemSettings(xml);
        Bundle b = new Bundle();
        b.putInt("xml", xml);
        frag.setArguments(b);
        return frag;
    }

    public SystemSettings(int xml) {
        super(xml);
    }

    public SystemSettings() {}

}
