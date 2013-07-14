package com.cfx.settings.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.cfx.settings.R;
import com.cfx.settings.CFXPreferenceFragment;

public class DensityChanger extends CFXPreferenceFragment implements
        OnPreferenceChangeListener {
	
	public static DensityChanger newInstance() {
		return new DensityChanger ();
	}

	public DensityChanger () {
	}

    private static final String TAG = "DensityChanger";

    private static final String PREF_CLEAR_MARKET_DATA = "clear_market_data";
    private static final String PREF_STOCK_DENSITY = "stock_density";
    private static final String PREF_REBOOT = "reboot";
    private static final String PREF_OPEN_MARKET = "open_market";
    private static final String PREF_LCD_DENSITY = "lcd_density";

    private static final String PROP_LCD_DENSITY = "ro.sf.lcd_density";

    Preference mReboot;
    Preference mStockDensity;
    Preference mClearMarketData;
    Preference mOpenMarket;
    ListPreference mCustomDensity;

    private static final int MSG_DATA_CLEARED = 500;

    private static final int DIALOG_DENSITY = 101;
    private static final int DIALOG_WARN_DENSITY = 102;

    int newDensityValue;

    // value from system prop. if user changes this
    // in our build, it's on them
    int stockDensity;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_DATA_CLEARED:
                    mClearMarketData.setSummary(R.string.clear_market_data_cleared);
                    break;
            }

        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.lcd_density);

        /** stock density */
        stockDensity = Integer.parseInt(SystemProperties.get(PROP_LCD_DENSITY));

        /** Stock density pref */
        mStockDensity = (Preference) findPreference(PREF_STOCK_DENSITY);
        mStockDensity.setOnPreferenceChangeListener(this);
        updateStockDensitySummary();

        /** Reboot */
        mReboot = findPreference(PREF_REBOOT);

        /** Clear Market data */
        mClearMarketData = findPreference(PREF_CLEAR_MARKET_DATA);

        /** Open Market */
        mOpenMarket = findPreference(PREF_OPEN_MARKET);

        /** Custom density */
        mCustomDensity = (ListPreference) findPreference(PREF_LCD_DENSITY);
        mCustomDensity.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mClearMarketData.setSummary("");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mReboot) {
			HotReboot();
            return true;
        } else if (preference == mClearMarketData) {
            ActivityManager am = (ActivityManager)
                    getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            am.clearApplicationUserData("com.android.vending",
                    new ClearUserDataObserver());
            return true;
        } else if (preference == mOpenMarket) {
            Intent openMarket = new Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_APP_MARKET)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName activityName = openMarket.resolveActivity(getActivity()
                    .getPackageManager());
            if (activityName != null) {
                getActivity().getApplicationContext().startActivity(openMarket);
            } else {
                preference.setSummary(getResources().getString(
                            R.string.open_market_summary_could_not_open));
            }
            return true;
        } else if (mStockDensity.equals(preference)) {
            setLcdDensity(stockDensity, false);
            updateStockDensitySummary();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public Dialog onCreateDialog(int dialogId) {
        LayoutInflater factory = LayoutInflater.from(getActivity().getApplicationContext());

        switch (dialogId) {
            case DIALOG_DENSITY:
                final View textEntryView = factory.inflate(
                        R.layout.alert_dialog_lcd, null);
                return new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.set_custom_density_title))
                        .setView(textEntryView)
                        .setPositiveButton(getResources().getString(R.string.set_custom_density_set), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText dpi = (EditText) textEntryView.findViewById(R.id.dpi_edit);
                                Editable text = dpi.getText();
                                Log.i(TAG, text.toString());

                                try {
                                    newDensityValue = Integer.parseInt(text.toString());
                                    showDialog(DIALOG_WARN_DENSITY);
                                } catch (Exception e) {
                                    mCustomDensity.setSummary(getResources().getString(R.string.custom_density_summary_invalid));
                                    updateStockDensitySummary();
                                }

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                            }
                        }).create();
            case DIALOG_WARN_DENSITY:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.custom_density_dialog_title))
                        .setMessage(
                                getResources().getString(R.string.custom_density_dialog_summary))
                        .setCancelable(false)
                        .setNeutralButton(getResources().getString(R.string.custom_density_dialog_button_got), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setLcdDensity(newDensityValue, false);
                                dialog.dismiss();
                                mCustomDensity.setSummary(newDensityValue + "");
                                updateStockDensitySummary();

                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.custom_density_dialog_button_reboot), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setLcdDensity(newDensityValue, true);
                                updateStockDensitySummary();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .create();
        }
        return null;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCustomDensity) {
            String strValue = (String) newValue;
            if (strValue.equals(getResources().getString(R.string.custom_density))) {
                showDialog(DIALOG_DENSITY);
            } else {
                newDensityValue = Integer.parseInt((String) newValue);
                showDialog(DIALOG_WARN_DENSITY);
            }
            return true;
        }
        return false;
    }

	private String getStockDensity() {
		return SystemProperties.get(PROP_LCD_DENSITY, "unknown");
	}

	private String getCustomDensity() {
		return SystemProperties.get("persist.sys.density", "unknown");
	}

	private void updateStockDensitySummary() {
		StringBuilder b = new StringBuilder().append("Stock density: ")
				.append(getStockDensity()).append("\n")
				.append("Custom density: ").append(getCustomDensity());
		mStockDensity.setSummary(b.toString());
	}

    private void setLcdDensity(int newDensity, boolean reboot) {
        SystemProperties.set("persist.sys.density", String.valueOf(newDensity));
        if (reboot)
            HotReboot();
    }

    private void HotReboot() {
            PowerManager pm = (PowerManager) getActivity()
                    .getSystemService(Context.POWER_SERVICE);
            pm.reboot("hotreboot");
    }

    class ClearUserDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            mHandler.sendEmptyMessage(MSG_DATA_CLEARED);
        }
    }
}
