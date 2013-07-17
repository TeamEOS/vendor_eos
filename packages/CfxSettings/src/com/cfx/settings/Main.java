package com.cfx.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.app.Fragment;
import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import com.cfx.settings.activities.DensityChanger;

public class Main extends Activity implements OnActivityRequestedListener {
	static final String KEY_TARGET_FRAGMENT = "key_target_fragment";
	static final String INTENT_INTERFACE = "intent_interface";
	static final String INTENT_SYSTEM = "intent_system";
	static final String INTENT_STYLE = "intent_style";

	List<String> mFragmentsTitleList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			savedInstanceState.remove("android:fragments");
		}
		super.onCreate(savedInstanceState);

		mFragmentsTitleList = new ArrayList<String>();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			String target = b.getString(KEY_TARGET_FRAGMENT);
			if (target != null) {
				String theTag;
				if (INTENT_INTERFACE.equals(target)) {
					theTag = getString(R.string.interface_settings_title);
				} else if (INTENT_SYSTEM.equals(target)) {
					theTag = getString(R.string.system_settings_title);
				} else if (INTENT_STYLE.equals(target)) {
					theTag = getString(R.string.style_settings_title);
				} else {
					// fallthru -- should never get here
					theTag = getString(R.string.interface_settings_title);
				}
				onActivityRequested(theTag);
			}
		} else {
			setContentView(R.layout.main);
			mFragmentsTitleList.add(getString(R.string.app_name));
			getFragmentManager().beginTransaction()
					.add(R.id.container, MainFragment.newInstance()).commit();
		}
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
			finish();
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

	@Override
	public void onActivityRequested(String tag) {
		if ((getString(R.string.interface_settings_title)).equals(tag)) {
			replaceFragment(InterfaceSettings.newInstance(), tag);
		} else if ((getString(R.string.style_settings_title)).equals(tag)) {
			replaceFragment(StyleSettings.newInstance(), tag);
		} else if ((getString(R.string.system_settings_title)).equals(tag)) {
			replaceFragment(SystemSettings.newInstance(), tag);
		} else if (getString(R.string.cfx_lcd_density_wizard_title).equals(tag)) {
			replaceFragment(DensityChanger.newInstance(), tag);
		}
	}
}
