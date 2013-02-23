
package org.eos.controlcenter;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public final class Utils {
    public static final boolean STATE_ON = true;
    public static final boolean STATE_OFF = false;

    public static final String ANDROIDNS = "http://schemas.android.com/apk/res/android";
    public static final String CONTROLCENTERNS = "http://schemas.android.com/apk/res/org.eos.controlcenter";
    public static final String URI_GRAVEYARD = "eos_graveyard_uri";
    
    public static final String INCOMING_FRAG_KEY = "eos_incoming_frag_key";
    public static final String FRAG_TITLE_KEY = "title";
    public static final String FRAG_POSITION_KEY = "position";
    public static final String INCOMING_LAST_FRAG_VIEWED = "eos_incoming_last_frag_viewed";
    public static final String SOFTKEY_FRAG_TAG = "eos_softkey_settings";
    public static final String SEARCH_PANEL_FRAG_TAG = "eos_search_panel_settings";
    public static final String PERFORMANCE_FRAG_TAG = "eos_performance_frag_tag";
    public static final String PRIVACY_FRAG_TAG = "eos_privacy_frag_tag";
    public static final String PRIVACY_LOG_PACKAGES = "eos_logger_packages";
    public static final String TEXT_FRAGMENT_TITLE_KEY = "eos_text_frag_title";
    public static final String TEXT_FRAGMENT_TEXT_RES_KEY = "eos_text_frag_res";
    
    public static final String DEFAULT_TITLE = "EOS Control Center";
    public static final String INTERFACE_SETTINGS_TITLE = "Interface";
    public static final String NAVBAR_SETTINGS_TITLE = "Navigation";
    public static final String STATUSBAR_SETTINGS_TITLE = "Statusbar";
    public static final String SYSTEM_SETTINGS_TITLE = "System";
    public static final String INFO_TITLE = "Info";
    
    public static int LAST_FRAG_VIEWED = 0;

    public static void turnOnEosUI(Context context) {
        Intent i = new Intent().setAction(EOSConstants.INTENT_EOS_CONTROL_CENTER);
        i.putExtra(EOSConstants.INTENT_EOS_CONTROL_CENTER_EXTRAS_STATE, STATE_ON);
        context.sendBroadcastAsUser(i, UserHandle.CURRENT);
    }
    
    public static void turnOffEosUI(Context context) {
        Intent i = new Intent().setAction(EOSConstants.INTENT_EOS_CONTROL_CENTER);
        i.putExtra(EOSConstants.INTENT_EOS_CONTROL_CENTER_EXTRAS_STATE, STATE_OFF);
        context.sendBroadcastAsUser(i, UserHandle.CURRENT);
    }
    
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

    public static boolean hasTorch(Context context) {
        if (context.getResources().getBoolean(R.bool.config_force_disable_torch)) {
            return false;
        } else {
            return EOSUtils.hasTorch();
        }
    }

    public static String readRawTextFile(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
