package com.neandril.go4lunch.controllers.fragments;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseFragment;
import com.neandril.go4lunch.models.DetailViewModel;
import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.view.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class ListViewFragment extends BaseFragment {

    private static final String TAG = ListViewFragment.class.getSimpleName();

    private View mListView;

    @BindView(R.id.fragment_listView_recyclerView) RecyclerView mRecyclerView;

    private List<PlacesDetail> mPlaces;
    private RestaurantAdapter mAdapter;
    private PlacesViewModel placesViewModel;
    private DetailViewModel detailViewModel;


    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_listview; }

    @Override
    protected void configureFragment() {

        this.configureRecyclerView();
        this.updateList();
    }

    private void configureRecyclerView() {
        this.mPlaces = new ArrayList<>();
        this.mAdapter = new RestaurantAdapter(mPlaces);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

    private void updateList() {
        this.mPlaces.clear();

        placesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlacesViewModel.class);
        detailViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DetailViewModel.class);

        final Observer<PlacesDetail> observer = (PlacesDetail placesDetail) -> {
            mPlaces.add(placesDetail);
            mAdapter.notifyDataSetChanged();
            for (int i = 0; i < mPlaces.get(0).getResults().size(); i++) {
                Log.e(TAG, "updateList: Name: " + mPlaces.get(0).getResults().get(i).getName() + " - " + mPlaces.get(0).getResults().get(i).getVicinity());
            }

        };

        placesViewModel.getRepository().observe(this, observer);

    }
}
