package com.neandril.go4lunch.view.fragments;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.view.activities.RestaurantActivity;
import com.neandril.go4lunch.view.base.BaseFragment;
import com.neandril.go4lunch.viewmodels.PlacesViewModel;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.view.ItemClickSupport;
import com.neandril.go4lunch.utils.Singleton;
import com.neandril.go4lunch.view.adapters.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class ListViewFragment extends BaseFragment {

    private static final String TAG = ListViewFragment.class.getSimpleName();
    private static final String RESTAURANT_TAG = "restaurantId";

    @BindView(R.id.fragment_listView_recyclerView) RecyclerView mRecyclerView;

    private List<PlacesDetail> mPlaces;
    private RestaurantAdapter mAdapter;
    private PlacesViewModel viewModel;

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_listview; }

    @Override
    protected void configureFragment() {

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.updateList();
    }

    // ***************************
    // UI
    // ***************************

    private void configureRecyclerView() {
        this.mPlaces = new ArrayList<>();
        this.mAdapter = new RestaurantAdapter(mPlaces);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

    public void updateList() {
        this.mPlaces.clear();
        String location = Singleton.getInstance().getPosition();

        try {
            viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlacesViewModel.class);
            viewModel.getPlacesLiveData().observe(this, placesDetail -> {
                mPlaces.add(placesDetail);
                mAdapter.notifyDataSetChanged();
            });
            viewModel.getPlaces(location);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_listview_recyclerview_item)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    String placeId = mAdapter.getmPlaceList().get(0).getResults().get(position).getPlaceId();
                    Log.e(TAG, "onItemClicked: " + placeId);
                    Intent intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra(RESTAURANT_TAG, placeId);
                    startActivity(intent);
                });
    }

    public void updatePredictions(PlacesDetail placesDetails) {
        mPlaces.clear();

        mPlaces.add(placesDetails);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        updateList();
        super.onResume();
    }
}
