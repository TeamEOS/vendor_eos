
package org.eos.controlcenter;

import android.os.Bundle;

public class Info extends PreferenceListFragment {
    
    public static Info newInstance(int xml) {
        Info frag = new Info(xml);
        Bundle b = new Bundle();
        b.putInt("xml", xml);
        frag.setArguments(b);
        return frag;
    }
    
    public Info(int xml) {
        super(xml);
    }
    
    public Info() {};

}