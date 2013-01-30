
package org.eos.controlcenter;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.eos.controlcenter.PreferenceListFragment.OnPreferenceAttachedListener;
import org.eos.controlcenter.ViewPagerAdapter;
import org.eos.controlcenter.TitlePageIndicator;

public class Main extends FragmentActivity implements OnPreferenceAttachedListener, OnActivityRequestedListener {
    private static final String TAG = "EosControlCenter";

    ViewPager mPager;
    TitlePageIndicator mIndicator;
    ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pager);

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mAdapter = new ViewPagerAdapter(getApplicationContext(),
                getSupportFragmentManager());
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.turnOnEosUI(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.turnOffEosUI(getApplicationContext());
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
            new NavigationBarHandler(root, (OnActivityRequestedListener)this);
            return;
        } else if (xmlId == R.xml.statusbar) {
            Log.i(TAG, "Statusbar settings is attached");
            new StatusbarHandler(root);
            return;
        } else if (xmlId == R.xml.system_settings) {
            Log.i(TAG, "System settings is attached");
            new SystemHandler(root, (OnActivityRequestedListener)this);
            return;
        } else if (xmlId == R.xml.info) {
            Log.i(TAG, "Info is attached");
            return;
        }
    }
    
    private void startSingleFragmentActivity(String tag) {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(Utils.INCOMING_FRAG_KEY, tag);
        startActivity(intent);
    }

    @Override
    public void onActivityRequested(String tag) {
            startSingleFragmentActivity(tag);
        }
}
