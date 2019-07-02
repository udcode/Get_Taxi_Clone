package com.avi_ud.gettaxi1.controller;

import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avi_ud.gettaxi1.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    OrderActivity orderActivity;
    OrdersHistory ordersHistory;
    private ViewPager mViewPager;
    IntentFilter mIntentFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orderActivity = new OrderActivity();
        ordersHistory = new OrdersHistory();
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(final ViewPager viewPager) {
        final SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(orderActivity, "Order");
        adapter.addFragment(ordersHistory, "Orders History");
        //adapter.addFragment(new Tab3Fragment(), "TAB3");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(adapter.getPageTitle(i).equals("Orders History"))
                        ((OrdersHistory)adapter.getItem(i)).EnteringPage();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
    @Override
    public void onBackPressed(){
       // ordersHistory.resetFragment();
        MainActivity.super.onBackPressed();
    }

}
