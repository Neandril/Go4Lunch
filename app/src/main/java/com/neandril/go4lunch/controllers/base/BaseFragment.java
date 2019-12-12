package com.neandril.go4lunch.controllers.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected abstract int getFragmentLayout();
    protected abstract void configureFragment();

    public BaseFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        Log.d(TAG, "onCreateView: Fragment created");
        ButterKnife.bind(this, view);
        this.configureFragment();
        return (view);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Fragment destroyed");
        super.onDestroy();
    }
}
