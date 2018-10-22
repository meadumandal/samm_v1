package com.umandalmead.samm_v1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.umandalmead.samm_v1.RouteTabs.Route2;
import com.umandalmead.samm_v1.RouteTabs.Route3;

/**
 * Created by eleazerarcilla on 07/01/2018.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {

    int NTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs)
    {
        super(fm);
        this.NTabs = NumberOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Route1 route1 = new Route1();
                return route1;
            case 1:
                Route2 route2 = new Route2();
                return  route2;
            case 2:
                Route3 route3 = new Route3();
                return route3;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 0;
    }
}
