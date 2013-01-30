
package org.eos.controlcenter;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;

public class SingleFragmentActivity extends Activity implements OnActivityRequestedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (savedInstanceState == null) {

            Bundle b = getIntent().getExtras();
            String newFrag = (String) b.get(Utils.INCOMING_FRAG_KEY);
            if (newFrag == null) {
                finish();
                return;
            }
            Fragment f;
            if (newFrag.equals(Utils.SOFTKEY_FRAG_TAG)) {
                f = (Fragment) SoftKeyActions.newInstance();
            } else if (newFrag.equals(Utils.SEARCH_PANEL_FRAG_TAG)) {
                f = (Fragment) SearchPanelActions.newInstance();
            } else if (newFrag.equals(Utils.PERFORMANCE_FRAG_TAG)) {
                f = (Fragment) Performance.newInstance();
            } else if (newFrag.equals(Utils.PRIVACY_FRAG_TAG)) {
                f = (Fragment) Privacy.newInstance();
            } else {
                finish();
                return;
            }
            showFragment(f);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.turnOnEosUI(getApplicationContext());
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
    public void onActivityRequested(String tag) {
        Fragment f;
        if (Utils.PRIVACY_LOG_PACKAGES.equals(tag)) {
            f = LoggerPackages.newInstance();
        } else {
            return;
        }
        replaceFragment(f);
    }

    private void showFragment(Fragment f) {
        getFragmentManager().beginTransaction()
        .add(R.id.container, f)
        .commit();
    }

    private void replaceFragment(Fragment f) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, f)
                .addToBackStack(null)
                .commit();
    }
}
