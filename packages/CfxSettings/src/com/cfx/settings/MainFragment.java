package com.cfx.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class MainFragment extends PreferenceFragment {
	OnActivityRequestedListener mListener;

	public static MainFragment newInstance(Bundle args) {
		MainFragment frag = new MainFragment();
		frag.setArguments(args);
		return frag;
	}

	public static MainFragment newInstance() {
		return new MainFragment();
	}

	public MainFragment(Bundle args) {
		newInstance(args);
	}

	public MainFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);
		mListener = (OnActivityRequestedListener) getActivity();

		findPreference(getString(R.string.interface_settings_title))
				.setOnPreferenceClickListener(
						new Preference.OnPreferenceClickListener() {

							@Override
							public boolean onPreferenceClick(
									Preference preference) {
								mListener
										.onActivityRequested(getString(R.string.interface_settings_title));
								return true;
							}
						});
		findPreference(getString(R.string.style_settings_title))
				.setOnPreferenceClickListener(
						new Preference.OnPreferenceClickListener() {

							@Override
							public boolean onPreferenceClick(
									Preference preference) {
								mListener
										.onActivityRequested(getString(R.string.style_settings_title));
								return true;
							}
						});
		findPreference(getString(R.string.system_settings_title))
				.setOnPreferenceClickListener(
						new Preference.OnPreferenceClickListener() {

							@Override
							public boolean onPreferenceClick(
									Preference preference) {
								mListener
										.onActivityRequested(getString(R.string.system_settings_title));
								return true;
							}
						});
	}
}
