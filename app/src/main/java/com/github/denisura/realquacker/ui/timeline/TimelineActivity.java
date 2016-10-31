package com.github.denisura.realquacker.ui.timeline;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.SingleFragmentActivity;
import com.github.denisura.realquacker.data.sync.QuackerSyncAdapter;
import com.github.denisura.realquacker.ui.dialog.ComposeDialogFragment;
import com.github.denisura.realquacker.utils.AccountUtils;

import butterknife.OnClick;
import timber.log.Timber;

public class TimelineActivity extends SingleFragmentActivity {

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_timeline;
    }

    @Override
    protected Fragment createFragment() {
        return TimelineFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        Account account = AccountUtils.getCurrentAccount(this);
        if (account == null) {
            return;
        }
        getSupportActionBar().setTitle(account.name);
        QuackerSyncAdapter.syncImmediately(account);
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
}
