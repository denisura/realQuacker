package com.github.denisura.realquacker;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.denisura.realquacker.ui.timeline.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class TabViewPagerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.view_pager)
    public ViewPager mViewPager;

    @BindView(R.id.tab_layout)
    public TabLayout mTabLayout;

    protected abstract ViewPagerAdapter createViewPagerAdapter();

    protected Unbinder unbinder;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setAdapter(createViewPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onDestroy() {
        mViewPager.setAdapter(null);
        unbinder.unbind();
        super.onDestroy();
    }
}