package org.eos.controlcenter;

import android.os.Bundle;

public class InterfaceSettings extends PreferenceListFragment {


    public static InterfaceSettings newInstance(int xml) {
        InterfaceSettings frag = new InterfaceSettings(xml);
        Bundle b = new Bundle();
        b.putInt("xml", xml);
        frag.setArguments(b);
        return frag;
    }

    public InterfaceSettings(int xmlId) {
        // TODO Auto-generated constructor stub
        super(xmlId);
    }

    public InterfaceSettings() {}
}
