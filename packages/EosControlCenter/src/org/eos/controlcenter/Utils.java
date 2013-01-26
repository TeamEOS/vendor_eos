
package org.eos.controlcenter;

import android.content.Context;

public final class Utils {
    public static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
    public static final String CONTROLCENTERNS = "http://schemas.android.com/apk/res/org.eos.controlcenter";
    public static final String URI_GRAVEYARD = "eos_graveyard_uri";
    
    public static String getXdaUrl(Context context, String device) {
        if(device.equals("grouper")) {
            return context.getString(R.string.eos_information_rom_link_xda_grouper);
        } else if (device.equals("maguro")) {
            return context.getString(R.string.eos_information_rom_link_xda_maguro);
        } else if (device.equals("toro")) {
            return context.getString(R.string.eos_information_rom_link_xda_toro);
        } else if (device.equals("toroplus")) {
            return context.getString(R.string.eos_information_rom_link_xda_toroplus);
        } else if (device.equals("wingray")) {
            return context.getString(R.string.eos_information_rom_link_xda_wingray);
        } else if (device.equals("stingray")) {
            return context.getString(R.string.eos_information_rom_link_xda_stingray);
        } else if (device.equals("umts_everest")) {
            return context.getString(R.string.eos_information_rom_link_xda_umts_everest);
        } else if (device.equals("tf101")) {
            return context.getString(R.string.eos_information_rom_link_xda_tf101);
        } else {
            return "hello";
        }        
    }
    
    public static String getRootzUrl(Context context, String device) {
        if(device.equals("grouper")) {
            return context.getString(R.string.eos_information_rom_link_rootz_grouper);
        } else if (device.equals("maguro")) {
            return context.getString(R.string.eos_information_rom_link_rootz_maguro);
        } else if (device.equals("toro")) {
            return context.getString(R.string.eos_information_rom_link_rootz_toro);
        } else if (device.equals("toroplus")) {
            return context.getString(R.string.eos_information_rom_link_rootz_toroplus);
        } else if (device.equals("wingray")) {
            return context.getString(R.string.eos_information_rom_link_rootz_wingray);
        } else if (device.equals("stingray")) {
            return context.getString(R.string.eos_information_rom_link_rootz_stingray);
        } else if (device.equals("umts_everest")) {
            return context.getString(R.string.eos_information_rom_link_rootz_umts_everest);
        } else if (device.equals("tf101")) {
            return context.getString(R.string.eos_information_rom_link_rootz_tf101);
        } else {
            return "hello";
        }    
    }
}
