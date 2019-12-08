package com.neandril.go4lunch.controllers.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.activities.RestaurantActivity;
import com.neandril.go4lunch.controllers.base.BaseFragment;
import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.ItemClickSupport;
import com.neandril.go4lunch.view.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class ListViewFragment extends BaseFragment {

    private static final String TAG = ListViewFragment.class.getSimpleName();
    public static final String RESTAURANT_TAG = "restaurantId";

    @BindView(R.id.fragment_listView_recyclerView) RecyclerView mRecyclerView;

    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private List<PlacesDetail> mPlaces;
    private RestaurantAdapter mAdapter;

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

    private void configureRecyclerView() {
        this.mPlaces = new ArrayList<>();
        this.mAdapter = new RestaurantAdapter(mPlaces);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.mRecyclerView.setAdapter(mAdapter);
    }

    private void updateList() {
        this.mPlaces.clear();

        try {
            PlacesViewModel placesViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlacesViewModel.class);

            final Observer<PlacesDetail> observer = (PlacesDetail placesDetail) -> {
                mPlaces.add(placesDetail);
                mAdapter.notifyDataSetChanged();
                for (int i = 0; i < mPlaces.get(0).getResults().size(); i++) {
                    Log.e(TAG, "updateList: Name: " + mPlaces.get(0).getResults().get(i).getName() + " - " + mPlaces.get(0).getResults().get(i).getVicinity());
                }

            };
            placesViewModel.getRepository().observe(this, observer);
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

}
