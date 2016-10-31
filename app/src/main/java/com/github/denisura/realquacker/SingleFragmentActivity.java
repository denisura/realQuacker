package com.github.denisura.realquacker;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    public Fragment mActivityFragment;
    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

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

        FragmentManager fm = getSupportFragmentManager();
        mActivityFragment = fm.findFragmentById(R.id.fragment_container);

        if (mActivityFragment == null) {
            mActivityFragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, mActivityFragment)
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}