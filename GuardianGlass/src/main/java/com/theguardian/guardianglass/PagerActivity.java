package com.theguardian.guardianglass;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;


public class PagerActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);
        CardScrollView pager = (CardScrollView) findViewById(R.id.pager);
        pager.setAdapter(new PageAdapter());
    }

    private class PageAdapter extends CardScrollAdapter{

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return new CardPageFragment();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView txt = new TextView(getApplicationContext());
            txt.setTextColor(Color.WHITE);
            txt.setTextSize(50);
            txt.setText("Test Test");
            return txt;
        }

        @Override
        public int findIdPosition(Object o) {
            return 0;
        }

        @Override
        public int findItemPosition(Object o) {
            return 0;
        }

    }

    private class CardPageFragment extends android.support.v4.app.Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);
            return inflater.inflate(R.layout.card_page_layout, null);
        }
    }
}
