package com.neandril.go4lunch.controllers.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected abstract int getFragmentLayout();
    protected abstract void configureFragment();

    public BaseFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        this.configureFragment();
        return (view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
