package com.github.denisura.realquacker.ui.timeline;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.TabViewPagerActivity;
import com.github.denisura.realquacker.ui.dialog.ComposeDialogFragment;
import com.github.denisura.realquacker.ui.profile.ProfileActivity;
import com.github.denisura.realquacker.utils.AccountUtils;

import butterknife.OnClick;
import timber.log.Timber;

public class TimelineActivity extends TabViewPagerActivity {

    Account mAccount;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ViewPagerAdapter createViewPagerAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(TimelineFragment.newInstance(), getResources().getString(R.string.tab_timeline));
        adapter.addFrag(MentionsFragment.newInstance(), getResources().getString(R.string.tab_mentions));
        return adapter;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAccount = AccountUtils.getCurrentAccount(this);
    }

    @OnClick(R.id.fab)
    public void onFabCLick(View view) {
        Timber.d("OnFabClick");
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeDialogFragment newFragment = ComposeDialogFragment.newAddInstance(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                Intent intent = ProfileActivity.newIntent(this, mAccount.name);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
}
