package com.neandril.go4lunch.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseFragment;

public class MapViewFragment extends BaseFragment {

    public MapViewFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mapview, container, false);
    }
}
