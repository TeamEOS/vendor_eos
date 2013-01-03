
package org.eos.controlcenter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;

public class MultiColorPreference extends Preference {
    private int mKeyIndex;
    private Context mContext;
    private int mDefaultColor;
    private int mColorHolder;
    private String mSettingsProviderTarget = null;
    private int[] colorArray = new int[4];

    public MultiColorPreference(Context context) {
        this(context, null);
    }

    public MultiColorPreference(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MultiColorPreference(Context context, AttributeSet attr, int defStyle) {
        super(context, attr);
        TypedArray a = context.obtainStyledAttributes(attr,
                R.styleable.MultiColorPreference, defStyle, 0);
        mSettingsProviderTarget = a
                .getString(R.styleable.MultiColorPreference_targetUri2);
        mKeyIndex = a.getInt(R.styleable.MultiColorPreference_keyIdentifier, 0);
        mContext = context;
        // set a generic, common android color to safely create the dialog
        // if we were unable to grab the target objects current color value
        // default color only refers to the dialog color selector circle
        mDefaultColor = mContext.getResources().getColor(
                com.android.internal.R.color.holo_blue_light);

        // we are interested in one color, but must hold all to write back to
        // provider
        colorArray = populateColorArray(Settings.System.getString(
                mContext.getContentResolver(), mSettingsProviderTarget));

        // hold the previously set color in the event of cancel or dismiss to
        // restore
        mColorHolder = colorArray[mKeyIndex];
    }

    @Override
    public void onClick() {
        colorArray = populateColorArray(Settings.System.getString(
                mContext.getContentResolver(), mSettingsProviderTarget));
        mColorHolder = colorArray[mKeyIndex];
        if (mColorHolder == -1)
            mColorHolder = mDefaultColor;
        ColorPickerDialog cpd = new ColorPickerDialog(mContext,
                new ColorPickerDialog.OnColorChangedListener() {

                    @Override
                    public void colorUpdate(int color) {
                        colorArray[mKeyIndex] = color;
                        writeColorSetting();
                    }

                    @Override
                    public void colorChanged(int color) {
                    }
                }, colorArray[mKeyIndex]);

        cpd.setOnCancelListener(new Dialog.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                colorArray[mKeyIndex] = mColorHolder;
                writeColorSetting();
            }
        });
        cpd.setCanceledOnTouchOutside(true);
        cpd.show();
    }

    public void setRestorePref(Preference pref) {
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                colorArray = populateColorArray(Settings.System.getString(
                        mContext.getContentResolver(), mSettingsProviderTarget));
                removeColorFilter();
                return false;
            }
        });
    }

    private void removeColorFilter() {
        colorArray[mKeyIndex] = -1;
        StringBuilder b = new StringBuilder();
        for (int i : colorArray) {
            b.append(String.valueOf(i));
            b.append("|");
        }
        Settings.System.putString(mContext.getContentResolver(),
                mSettingsProviderTarget, b.toString());
    }

    private void writeColorSetting() {
        StringBuilder b = new StringBuilder();
        for (int i : colorArray) {
            b.append(String.valueOf(i));
            b.append("|");
        }
        Settings.System.putString(mContext.getContentResolver(),
                mSettingsProviderTarget, b.toString());
    }

    private static int[] populateColorArray(String s) {
        int[] colorArray = new int[4];
        String[] colorStrings = s.split("\\|");
        int tmp = 0;
        for (String i : colorStrings) {
            try {
                colorArray[tmp] = Integer.parseInt(i);
            } catch (NumberFormatException e) {
                colorArray[tmp] = -1;
            }
            tmp++;
        }
        return colorArray;
    }
}
