package org.sperixlabs.androidhotspotapi;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //adding viewpager
        ViewPager viewPager =  findViewById(R.id.pager);
        // Assign created adapter to viewPager
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        //adding tabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        // This method setup all required method for TabLayout with Viewpager
        tabLayout.setupWithViewPager(viewPager);

    }

    // implementing tabAdapter
    public static class PagerAdapter extends FragmentPagerAdapter {
        // As we are implementing two tabs
        private static final int NUM_ITEMS = 3;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        // For each tab different fragment is returned
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Main();
                case 1:
                    return new Clients();
                case 2:
                    return new Config();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "Main";
            }else if(position == 1){
                return "Clients";
            }else if(position == 2){
                return "Settings";
            }else {
                return "Tab " + (position + 1);
            }
        }
    }

}
