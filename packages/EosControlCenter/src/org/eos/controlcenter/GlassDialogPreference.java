
package org.eos.controlcenter;

import android.content.ContentResolver;
import android.content.Context;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

public class GlassDialogPreference extends DialogPreference {
    Context mContext;
    ContentResolver mResolver;
    TextView mNavbar_header;
    SeekBar mNavbar_seek;
    TextView mNavbarDefTitle;
    CheckBox mNavbar_checkbox;
    TextView mNavbar_value;
    TextView mStatusbar_header;
    SeekBar mStatusbar_seek;
    TextView mStatusbarDefTitle;
    CheckBox mStatusbar_checkbox;
    TextView mStatusbar_value;

    int mNavbarValue;
    int mStatusbarValue;

    public GlassDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mResolver = context.getContentResolver();
    }

    @Override
    protected View onCreateDialogView() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.glass_dialog_preference, null);
        mNavbar_header = (TextView) root.findViewById(R.id.navbar_header);
        mNavbar_seek = (SeekBar) root.findViewById(R.id.navbar_seekbar);
        mNavbarDefTitle = (TextView) root.findViewById(R.id.navbar_default_title);
        mNavbar_checkbox = (CheckBox) root.findViewById(R.id.navbar_default_checkbox);
        mNavbar_value = (TextView) root.findViewById(R.id.navbar_progress_value);

        if (EOSUtils.hasNavBar(mContext)) {
            mNavbar_header.setText("Navigation bar transparency");

            mNavbar_seek.setMax(255);
            mNavbar_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mNavbarValue = progress;
                    mNavbar_value.setText("Current value: " + String.valueOf(mNavbarValue));
                    Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_NAVBAR_GLASS_LEVEL,
                            255 - mNavbarValue);

                }
            });

            mNavbarValue = Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_NAVBAR_GLASS_LEVEL,
                    EOSConstants.SYSTEMUI_NAVBAR_GLASS_PRESET);
            mNavbar_seek.setProgress(255 - mNavbarValue);

            mNavbarDefTitle.setText("Default launcher preset");

            mNavbar_checkbox.setChecked(Settings.System.getInt(mResolver,
                    EOSConstants.SYSTEMUI_NAVBAR_GLASS_DEFAULT_ENABLED, 0) == 1);
            mNavbar_seek.setEnabled(!mNavbar_checkbox.isChecked());
            mNavbar_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mNavbar_seek.setEnabled(!isChecked);
                    Settings.System.putInt(mResolver,
                            EOSConstants.SYSTEMUI_NAVBAR_GLASS_DEFAULT_ENABLED, isChecked ? 1 : 0);
                }

            });
            mNavbar_value.setText("Current value: " + String.valueOf(mNavbarValue));

        } else {
            mNavbar_header.setVisibility(View.GONE);
            mNavbar_seek.setVisibility(View.GONE);
            mNavbarDefTitle.setVisibility(View.GONE);
            mNavbar_checkbox.setVisibility(View.GONE);
            mNavbar_value.setVisibility(View.GONE);
        }

        mStatusbar_header = (TextView) root.findViewById(R.id.statusbar_header);
        mStatusbar_seek = (SeekBar) root.findViewById(R.id.statusbar_seekbar);
        mStatusbarDefTitle = (TextView) root.findViewById(R.id.statusbar_default_title);
        mStatusbar_checkbox = (CheckBox) root.findViewById(R.id.statusbar_default_checkbox);
        mStatusbar_value = (TextView) root.findViewById(R.id.statusbar_progress_value);

        mStatusbar_header.setText("Statusbar transparency");

        mStatusbar_seek.setMax(255);
        mStatusbar_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mStatusbarValue = progress;
                mStatusbar_value.setText("Current value: " + String.valueOf(mStatusbarValue));
                Settings.System.putInt(mResolver, EOSConstants.SYSTEMUI_STATUSBAR_GLASS_LEVEL,
                        255 - progress);

            }
        });
        mStatusbarValue = Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_STATUSBAR_GLASS_LEVEL,
                EOSConstants.SYSTEMUI_STATUSBAR_GLASS_PRESET);

        mStatusbar_seek.setProgress(255 - mStatusbarValue);
        mStatusbarDefTitle.setText("Default launcher preset");

        mStatusbar_checkbox.setChecked(Settings.System.getInt(mResolver,
                EOSConstants.SYSTEMUI_STATUSBAR_GLASS_DEFAULT_ENABLED, 0) == 1);
        mStatusbar_seek.setEnabled(!mStatusbar_checkbox.isChecked());
        mStatusbar_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStatusbar_seek.setEnabled(!isChecked);
                Settings.System.putInt(mResolver,
                        EOSConstants.SYSTEMUI_STATUSBAR_GLASS_DEFAULT_ENABLED, isChecked ? 1 : 0);
            }

        });
        mStatusbar_value.setText("Current value: " + String.valueOf(mStatusbarValue));

        return root;
    }
}
