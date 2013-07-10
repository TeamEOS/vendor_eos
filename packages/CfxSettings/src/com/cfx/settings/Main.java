package com.cfx.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class Main extends Activity implements OnActivityRequestedListener {
	TextView mTitle;
	ActionBar mBar;

	List<String> mFragmentsTitleList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			savedInstanceState.remove("android:fragments");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mFragmentsTitleList = new ArrayList<String>();
		mFragmentsTitleList.add(getString(R.string.interface_settings_title));
		mFragmentsTitleList.add(getString(R.string.style_settings_title));
		mFragmentsTitleList.add(getString(R.string.system_settings_title));

		mBar = getActionBar();
		mFragmentsTitleList.add(getString(R.string.app_name));
		showFragment(MainFragment.newInstance());
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
		if (mFragmentsTitleList.size() > 0) {
			mBar.setTitle(mFragmentsTitleList.get(mFragmentsTitleList.size() - 1));
		} else {
			mBar.setTitle(getString(R.string.app_name));
		}
	}

	private void replaceFragment(Fragment f, String title) {
		getFragmentManager().beginTransaction().replace(R.id.container, f)
		        .addToBackStack(null)
				.commit();
		mFragmentsTitleList.add(title);
		updateActionBarTitle();
		mBar.setDisplayHomeAsUpEnabled(true);
	}

	private void showFragment(Fragment f) {
		getFragmentManager().beginTransaction().add(R.id.container, f).commit();
		mBar.setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public void onActivityRequested(String tag) {
		if ((getString(R.string.interface_settings_title)).equals(tag)) {
			replaceFragment(InterfaceSettings.newInstance(), tag);
		} else if ((getString(R.string.style_settings_title)).equals(tag)) {
			replaceFragment(StyleSettings.newInstance(), tag);
		} else if ((getString(R.string.system_settings_title)).equals(tag)) {
			replaceFragment(SystemSettings.newInstance(), tag);
		}
	}
}
