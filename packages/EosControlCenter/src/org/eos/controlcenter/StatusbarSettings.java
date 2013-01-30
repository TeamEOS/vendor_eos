package org.eos.controlcenter;

import android.os.Bundle;

public class StatusbarSettings extends PreferenceListFragment {


    public static StatusbarSettings newInstance(int xml) {
        StatusbarSettings frag = new StatusbarSettings(xml);
        Bundle b = new Bundle();
        b.putInt("xml", xml);
        frag.setArguments(b);
        return frag;
    }

    public StatusbarSettings(int xmlId) {
        // TODO Auto-generated constructor stub
        super(xmlId);
    }

    public StatusbarSettings() {}
}
