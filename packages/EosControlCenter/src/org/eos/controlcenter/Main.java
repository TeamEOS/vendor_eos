
package org.eos.controlcenter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.ArrayList;

public class Main extends Activity {

    private static Activity mActivity;
    private static ArrayList<String> mFragmentsTitleList = new ArrayList<String>();
    
    private static TextView mTitle;
    private Spinner mNavigation;
    private ImageView mIcon;

    public static boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (Activity) this;

        int screenLayout = Resources.getSystem().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            setContentView(R.layout.main_twopane);
            mTwoPane = true;
        } else {
            setContentView(R.layout.main);
        }

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
        getActionBar().setCustomView(R.layout.action_bar);
        
        mTitle = (TextView) getActionBar().getCustomView().findViewById(R.id.titleTextView);       
        mNavigation = (Spinner) getActionBar().getCustomView().findViewById(R.id.navigationSpinner);
        mIcon = (ImageView) getActionBar().getCustomView().findViewById(R.id.iconImageView);

        String[] tabsList = {"Interface", "System", "Information"};    	
    	ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(this,
    			R.layout.navigation_spinner_textview, tabsList);
    	spinnerDataAdapter.setDropDownViewResource(R.layout.navigation_spinner_dropdown_textview);	    		
    	mNavigation.setAdapter(spinnerDataAdapter);
        mNavigation.setOnItemSelectedListener(new OnNavigationItemSelectedListener());

        mIcon.setOnClickListener(new OnClickListener(){
            public void onClick(View view) {
                onIconPressed();
            }
        });
    }

    public static void showFragment(String title, Fragment fragment) {
        if (!mTwoPane) {
            mActivity.getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            mFragmentsTitleList.add(title);
            updateActionBarTitle();
        } else {
            mActivity.getFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment)
                    .commit();
            mFragmentsTitleList.add(title);
            updateActionBarTitle();
        }
    }

    private void onIconPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            mFragmentsTitleList.remove(mFragmentsTitleList.size() - 1);
            updateActionBarTitle();
        } else {
            finish();
        }
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

    public static void updateActionBarTitle() {
        if (mFragmentsTitleList.size() > 0) {
            mTitle.setText(mFragmentsTitleList.get(mFragmentsTitleList.size() - 1));
        } else {
            mTitle.setText("EOS Control Center");
        }
    }
    
    private class SpaceHolder extends Fragment {
    }
    
    private final class OnNavigationItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mFragmentsTitleList.clear();

            Fragment newFragment = null;
            if (position == 0) {
                newFragment = new Interface();
            } else if (position == 1) {
                newFragment = new System();
            } else if (position == 2) {
                newFragment = new Info();
            }

            if (!mTwoPane) {
                getFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
            } else {
                if (position != 2) {
                    getFragmentManager().beginTransaction().replace(R.id.container, newFragment).commit();
                    getFragmentManager().beginTransaction().replace(R.id.detail_container, new SpaceHolder()).commit();
                } else {
                    getFragmentManager().beginTransaction().replace(R.id.container, new RomLinks()).commit();
                    getFragmentManager().beginTransaction().replace(R.id.detail_container, newFragment).commit();
                }
            }
            
            mFragmentsTitleList.add("EOS Control Center");
            updateActionBarTitle();
		}
		 
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}	 
	}
}
