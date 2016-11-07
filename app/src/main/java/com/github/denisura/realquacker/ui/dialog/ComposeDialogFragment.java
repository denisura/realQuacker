package com.github.denisura.realquacker.ui.dialog;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.data.database.AppContract;
import com.github.denisura.realquacker.data.database.AppProvider;
import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.data.network.TwitterApi;
import com.github.denisura.realquacker.data.network.TwitterService;
import com.github.denisura.realquacker.data.sync.QuackerSyncAdapter;
import com.github.denisura.realquacker.utils.AccountUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ComposeDialogFragment extends DialogFragment {

    private Unbinder unbinder;

    private static final String ARG_TITLE = "title";
    @BindView(R.id.notes)
    public TextInputEditText mNotes;

    @BindView(R.id.counter)
    public TextView mCounter;


    public ComposeDialogFragment() {
    }

    public static ComposeDialogFragment newAddInstance(Context context) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, context.getResources().getString(R.string.compose));
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_compose, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().getString(ARG_TITLE));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCounter.setText(String.valueOf(140 - s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        mNotes.addTextChangedListener(mTextEditorWatcher);
        return rootView;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_compose, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNotes.getWindowToken(), 0);

        if (id == R.id.action_save) {
            Account account = AccountUtils.getCurrentAccount(getContext());
            if (account != null) {
                AccountManager accountManager =
                        (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);

                String token = accountManager.getUserData(account, "token");
                String tokenSecret = accountManager.getUserData(account, "tokenSecret");

                TwitterApi _apiService = TwitterService.newService(token, tokenSecret);
                Call<Tweet> call = _apiService.postStatus(mNotes.getText().toString());


                call.enqueue(new Callback<Tweet>() {
                    @Override
                    public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                        Tweet tweet = response.body();
                        Timber.d("Posted tweet");

                        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

                        ContentProviderOperation.Builder builder;

                        builder = ContentProviderOperation.newInsert(AppProvider.Tweets.CONTENT_URI);
                        builder.withValues(tweet.toContentValues());
                        batch.add(builder.build());

                        builder = ContentProviderOperation.newInsert(AppProvider.Users.CONTENT_URI);
                        builder.withValues(tweet.getUser().toContentValues());
                        batch.add(builder.build());

                        int operations = batch.size();
                        if (operations > 0) {
                            try {
                                getContext().getContentResolver().applyBatch(AppContract.CONTENT_AUTHORITY, batch);
                            } catch (RemoteException | OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }

                        QuackerSyncAdapter.syncHomeTimelineImmediately(account);
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<Tweet> call, Throwable t) {
                        //Handle failure
                    }
                });


            }

//            // handle confirmation button click here
//            mTodoItem.setTask(mTask.getText().toString());
//            mTodoItem.setNotes(mNotes.getText().toString());
//            Timber.d("save %s %s %s", mTodoItem.getTask(), mTodoItem.getNotes(), mTodoItem.getDueDate());
//
//            if (mTodoItem.getId() == 0) {
//                Timber.d("Increase priority for all tasks");
//                getContext().getContentResolver()
//                        .update(
//                                AppProvider.Priority.UP_CONTENT_URI,
//                                new ContentValues(), null, null
//                        );
//            }
//
//            getContext().getContentResolver()
//                    .insert(
//                            AppProvider.Tasks.CONTENT_URI,
//                            mTodoItem.getContentValues()
//                    );
            return true;
        } else if (id == android.R.id.home) {
            // handle close button click here
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNotes.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mNotes, InputMethodManager.SHOW_IMPLICIT);
    }
}