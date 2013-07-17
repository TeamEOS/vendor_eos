package com.cfx.settings;

import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.cfx.settings.activities.DensityChanger;

public class Main extends Activity implements OnActivityRequestedListener {
	static final String KEY_TARGET_FRAGMENT = "key_target_fragment";
	static final String INTENT_INTERFACE = "intent_interface";
	static final String INTENT_SYSTEM = "intent_system";
	static final String INTENT_STYLE = "intent_style";
	static boolean cameFromSettings = false;

	List<String> mFragmentsTitleList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			savedInstanceState.remove("android:fragments");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mFragmentsTitleList = new ArrayList<String>();
		String targetFrag = "";
		Bundle b = getIntent().getExtras();
		if (b != null) {
			String target = b.getString(KEY_TARGET_FRAGMENT);
			if (target != null) {
				if (INTENT_INTERFACE.equals(target)) {
					targetFrag = getString(R.string.interface_settings_title);
				} else if (INTENT_SYSTEM.equals(target)) {
					targetFrag = getString(R.string.system_settings_title);
				} else if (INTENT_STYLE.equals(target)) {
					targetFrag = getString(R.string.style_settings_title);
				} else {
					// fallthru -- should never get here
					targetFrag = getString(R.string.interface_settings_title);
				}
				cameFromSettings = true;
			}
		} else {
			targetFrag = getString(R.string.app_name);
			cameFromSettings = false;
		}
		Fragment f = getFragmentByTitle(targetFrag);
		// Bundle args = f.getArguments();
		// if (args == null) args = new Bundle();
		// args.putBoolean("started_from_shortcut", cameFromSettings);
		// f.setArguments(args);
		initializeFragment(f, targetFrag);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Main.this.onBackPressed();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			getFragmentManager().popBackStack();
			mFragmentsTitleList.remove(mFragmentsTitleList.size() - 1);
			updateActionBarTitle();
		} else {
			if (cameFromSettings) {
				Intent settingsIntent = new Intent().setAction(
						android.provider.Settings.ACTION_SETTINGS).setFlags(
						Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityAsUser(settingsIntent, new UserHandle(
						UserHandle.USER_CURRENT));
				finish();
			}
		}
	}

	private void updateActionBarTitle() {
		String title;
		if (mFragmentsTitleList.size() > 0) {
			title = mFragmentsTitleList.get(mFragmentsTitleList.size() - 1);
		} else {
			title = getString(R.string.app_name);
		}
		getActionBar().setTitle(title);
	}

	private void replaceFragment(Fragment f, String title) {
		getFragmentManager().beginTransaction().replace(R.id.container, f)
				.addToBackStack(null).commit();
		mFragmentsTitleList.add(title);
		updateActionBarTitle();
	}

	private void initializeFragment(Fragment f, String title) {
		getFragmentManager().beginTransaction().add(R.id.container, f).commit();
		mFragmentsTitleList.add(title);
		updateActionBarTitle();
	}

	Fragment getFragmentByTitle(String title) {
		if ((getString(R.string.interface_settings_title)).equals(title)) {
			return InterfaceSettings.newInstance();
		} else if ((getString(R.string.style_settings_title)).equals(title)) {
			return StyleSettings.newInstance();
		} else if ((getString(R.string.system_settings_title)).equals(title)) {
			return SystemSettings.newInstance();
		} else if (getString(R.string.cfx_lcd_density_wizard_title).equals(
				title)) {
			return DensityChanger.newInstance();
		} else if (getString(R.string.app_name).equals(title)) {
			return MainFragment.newInstance();
		} else if (getString(R.string.cfx_power_menu_title).equals(title)) {
			return PowerMenu.newInstance();
		} else {
			return MainFragment.newInstance();
		}
	}

	@Override
	public void onActivityRequested(String tag) {
		replaceFragment(getFragmentByTitle(tag), tag);
	}
}
