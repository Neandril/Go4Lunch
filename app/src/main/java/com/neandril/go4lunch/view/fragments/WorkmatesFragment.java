package com.neandril.go4lunch.view.fragments;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.view.base.BaseFragment;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.models.UserHelper;
import com.neandril.go4lunch.view.adapters.WorkmateAdapter;

import butterknife.BindView;

public class WorkmatesFragment extends BaseFragment {

    private static final String TAG = WorkmatesFragment.class.getSimpleName();

    @BindView(R.id.fragment_workmates_recyclerview) RecyclerView mRecyclerView;

    // ***************************
    // BASE METHODS
    // ***************************

    @Override
    protected int getFragmentLayout() { return R.layout.fragment_workmates; }

    @Override
    protected void configureFragment() {
        this.configureRecyclerView();
    }

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: Workmates");

        // Instanciate a new Firestore query object
        Query query = UserHelper.getAllUsers();

        // Configure Firestore recycler options
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();

        // Attach the adapter to the recyclerview
        WorkmateAdapter mAdapter = new WorkmateAdapter(options);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }
}
