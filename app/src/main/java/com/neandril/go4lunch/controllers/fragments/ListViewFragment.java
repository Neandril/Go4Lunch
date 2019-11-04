package com.neandril.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseFragment;

public class ListViewFragment extends BaseFragment {

    private static final String TAG = ListViewFragment.class.getSimpleName();

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_listview; }

    @Override
    protected void configureFragment() {

    }
}
