package com.xlog.xloguser.finaldriverapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.widget.TextView;
/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class tabPagerAdapter extends FragmentStatePagerAdapter {

    String [] tabArray =  new String[]{"Current Trip", "Dashboard"};
    public tabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];

    }

    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                Map_fragment map_fragment = new Map_fragment();
                return map_fragment;

            case 1:
                Dashboard_fragment dashboard_fragment = new Dashboard_fragment();
                return dashboard_fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
