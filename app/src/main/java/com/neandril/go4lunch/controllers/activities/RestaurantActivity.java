package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.api.GoogleApiCall;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.models.DetailViewModel;
import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.Restaurant;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;

import java.util.Objects;

import butterknife.BindView;

public class RestaurantActivity extends BaseActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName();

    public String placeId;
    private DetailViewModel viewModel;

    @BindView(R.id.activity_restaurant_main_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.tv_restaurantName) TextView mRestaurantName;
    @BindView(R.id.tv_restaurantAddress) TextView mRestaurantAddress;
    @BindView(R.id.restaurant_background_picture) ImageView mRestaurantBackgroundImg;


    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant;
    }

    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        placeId = intent.getStringExtra("restaurantId");

        Log.e(TAG, "onCreate: Marker retrieved :" + placeId);
        retrieveRestaurantDetails();
    }

    private void retrieveRestaurantDetails() {
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.init(placeId);
        final Observer<Detail> observer = (Detail detail) -> {
            Log.e(TAG, "retrieveRestaurantDetails: name: " + detail.getResult().getName());
            updateUI(detail);
        };

        viewModel.getRepository().observe(this, observer);
    }

    private void updateUI(Detail detail) {
        mRestaurantName.setText(detail.getResult().getName());
        mRestaurantAddress.setText(detail.getResult().getVicinity());
        String type = detail.getResult().getTypes().get(1);
        Log.e(TAG, "updateUI: Type:" + type);

        String photoRef = detail.getResult().getPhotos().get(0).getPhotoReference();
        String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=AIzaSyBkjaxczMoqyJzCQnRRIJgeJoubLGdSEK0&photoreference=" + photoRef;

        if (photoRef != null) {
            Glide.with(this).load(request).into(mRestaurantBackgroundImg);
        }
    }
}
