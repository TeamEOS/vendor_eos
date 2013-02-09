
package org.eos.controlcenter;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.eos.controlcenter.PreferenceListFragment.OnPreferenceAttachedListener;
import org.eos.controlcenter.ViewPagerAdapter;
import org.eos.controlcenter.TitlePageIndicator;
import org.eos.controlcenter.TitlePageIndicator.OnMyPageChangedListener;
import org.teameos.jellybean.settings.EOSConstants;
import org.teameos.jellybean.settings.EOSUtils;

import java.util.ArrayList;

public class Main extends FragmentActivity
        implements OnPreferenceAttachedListener,
        OnActivityRequestedListener,
        OnMyPageChangedListener {

    private static final String TAG = "EosControlCenter";
    private static final String TITLE = "EOS Control Center";

    boolean isLargeLandscape;

    IntentFilter filter;
    BroadcastReceiver mEosUiReceiver;
    ArrayList<BroadcastReceiver> mReceivers = new ArrayList<BroadcastReceiver>();

    ViewPager mPager;
    TitlePageIndicator mIndicator;
    ViewPagerAdapter mAdapter;
    ActionBar mBar;

    TextView mTitle;
    ImageView mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if this is grouper or any large screen, and we're in landscape, go to
        // dual pane
        isLargeLandscape = EOSUtils.isLargeScreen() && EOSUtils.isLandscape(Main.this);
        boolean isXLarge = EOSUtils.isXLargeScreen();
        if (isLargeLandscape || isXLarge) {
            Intent intent = new Intent(Main.this, DualPaneActivity.class);
            if (isLargeLandscape) {
                intent.putExtra(Utils.INCOMING_LAST_FRAG_VIEWED, Utils.LAST_FRAG_VIEWED);
            }
            startActivity(intent);

            // we can end XLarge screens here because they never see
            // this activity. Large must finish onStart() due to
            // a npe from the PreferenceListFragement making a getActivity()
            // call after we are already finished
            if (isXLarge) {
                finish();
                return;
            }
        } else {
            setContentView(R.layout.main_pager);

            mPager = (ViewPager) findViewById(R.id.view_pager);
            mAdapter = new ViewPagerAdapter(Main.this,
                    getSupportFragmentManager());
            mPager.setAdapter(mAdapter);

            mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(mPager);
            mIndicator.setOnMyPageChangedListener((OnMyPageChangedListener) Main.this);

            mBar = getActionBar();
            mBar.setTitle(TITLE);

            int lastViewed = getIntent().getIntExtra(Utils.INCOMING_LAST_FRAG_VIEWED, 0);
            mPager.setCurrentItem(lastViewed, true);

            mEosUiReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction()
                            .equals(EOSConstants.INTENT_SETTINGS_RESTART_INTERFACE_SETTINGS)) {
                        Utils.turnOnEosUI(getApplicationContext());
                    }
                }
            };

            filter = new IntentFilter();
            filter.addAction(EOSConstants.INTENT_SETTINGS_RESTART_INTERFACE_SETTINGS);
            registerReceivers();
            Utils.turnOnEosUI(getApplicationContext());
        }
    }

    void registerReceivers() {
        unregisterReceivers();
        this.registerReceiver(mEosUiReceiver, filter);
        mReceivers.add(mEosUiReceiver);
    }

    void unregisterReceivers() {
        for (BroadcastReceiver r : mReceivers) {
            this.unregisterReceiver(r);
        }
        mReceivers.clear();
    }

    private void startSingleFragmentActivity(String tag) {
        Intent intent = new Intent(Main.this, SingleFragmentActivity.class);
        intent.putExtra(Utils.INCOMING_FRAG_KEY, tag);
        startActivity(intent);
    }
    
    private void startTextDialogFragment(Bundle b) {
        TextInfoFragment textFragment = TextInfoFragment.newInstance(b);
        textFragment.show(getFragmentManager(), b.getString(Utils.TEXT_FRAGMENT_TITLE_KEY));        
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // so we kill large screens here
        if (isLargeLandscape) {
            finish();
            return;
        }
        registerReceivers();
        Utils.turnOnEosUI(getApplicationContext());
        PackageServerActivity.startPackageServer(getPackageManager());
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceivers();
        // Utils.turnOffEosUI(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_themes:
                Intent intent = new Intent()
                        .setAction(Intent.ACTION_MAIN)
                        .setClassName("com.tmobile.themechooser",
                                "com.tmobile.themechooser.ThemeChooser");
                Main.this.startActivity(intent);
                break;
            case R.id.action_menu_roster:
                startSingleFragmentActivity(Utils.INFO_TITLE);
                break;
            case R.id.action_menu_xda:
                Intent xda_thread = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Utils.getXdaUrl(Main.this,
                                EOSUtils.getDevice())));
                startActivity(xda_thread);
                break;
            case R.id.action_menu_changelog:
                Bundle b = new Bundle();
                b.putString(Utils.TEXT_FRAGMENT_TITLE_KEY, "Changelog");
                b.putInt(Utils.TEXT_FRAGMENT_TEXT_RES_KEY, R.raw.change_log);
                startTextDialogFragment(b);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
        if (root == null) {
            Log.i(TAG, "Root preference screen is null!");
            return;
        } else if (xmlId == R.xml.interface_settings) {
            Log.i(TAG, "Interface settings is attached");
            new InterfaceHandler(root);
            return;
        } else if (xmlId == R.xml.navigation_bar) {
            Log.i(TAG, "NavigationBar settings is attached");
            new NavigationHandler(root, (OnActivityRequestedListener) Main.this);
            return;
        } else if (xmlId == R.xml.statusbar) {
            Log.i(TAG, "Statusbar settings is attached");
            new StatusbarHandler(root);
            return;
        } else if (xmlId == R.xml.system_settings) {
            Log.i(TAG, "System settings is attached");
            new SystemHandler(root, (OnActivityRequestedListener) Main.this);
            return;
        }
    }

    @Override
    public void onActivityRequested(String tag) {
        startSingleFragmentActivity(tag);
    }

    @Override
    public void onMyPageChanged(int position) {
        Utils.LAST_FRAG_VIEWED = position;
    }
}
