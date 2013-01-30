
package org.eos.controlcenter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;
import android.view.View;

import org.teameos.jellybean.settings.EOSUtils;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Pair<Fragment, String>> mFragments;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mFragments = new ArrayList<Pair<Fragment, String>>();
        mFragments.add(Pair.create(
                (Fragment) InterfaceSettings.newInstance(R.xml.interface_settings), "Interface"));

        if (EOSUtils.hasNavBar(context)) {
            mFragments.add(Pair.create(
                    (Fragment) NavigationFragment.newInstance(R.xml.navigation_bar), "Navigation"));
        }

        mFragments.add(Pair.create((Fragment) StatusbarSettings.newInstance(R.xml.statusbar),
                "Statusbar"));
        mFragments.add(Pair.create((Fragment) SystemSettings.newInstance(R.xml.system_settings),
                "System"));
        mFragments.add(Pair.create((Fragment) Info.newInstance(R.xml.info), "Info"));
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return mFragments.get(position).second;
    }

    @Override
    public int getCount()
    {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return mFragments.get(position).first;
    }

    @Override
    public Object instantiateItem(View pager, int position)
    {
        return null;
    }

    @Override
    public void destroyItem(View pager, int position, Object view)
    {
    }

    @Override
    public void finishUpdate(View view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }
}
