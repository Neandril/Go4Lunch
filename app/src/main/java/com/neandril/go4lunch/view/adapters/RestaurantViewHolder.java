package com.neandril.go4lunch.view.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.places.PlacesResult;
import com.neandril.go4lunch.utils.Singleton;
import com.neandril.go4lunch.models.UserHelper;
import com.neandril.go4lunch.utils.Utility;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = RestaurantViewHolder.class.getSimpleName();

    @BindView(R.id.tv_recyclerView_restaurantName) TextView mRestaurantName;
    @BindView(R.id.tv_recyclerView_restaurantAddress) TextView mRestaurantAddress;
    @BindView(R.id.tv_recyclerView_OpenningTime) TextView mOpenningTime;
    @BindView(R.id.tv_distance) TextView mDistance;
    @BindView(R.id.tv_nb_of_participants) TextView mNumberOfParticipants;
    @BindView(R.id.imageView_ppl) ImageView mImageViewPpl;
    @BindView(R.id.imageView_restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;

    private double lat = Singleton.getInstance().getUserLat();
    private double lng = Singleton.getInstance().getUserLng();

    RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateRestaurantsList(PlacesResult result) {
        Log.d(TAG, "updateUi: ");

        Utility utility = new Utility();

        // Restaurant name and address
        this.mRestaurantName.setText(result.getName());
        this.mRestaurantAddress.setText(result.getVicinity());

        // Get the rating 3-stars indexed
        if (result.getRating() != null) {
            mRatingBar.setRating(utility.convertRating(result.getRating()));
        } else {
            mRatingBar.setRating(0);
        }

        // Aperture of restaurant
        mOpenningTime.setTextColor(Color.BLACK);
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                mOpenningTime.setText(itemView.getContext().getString(R.string.open_now));
                mOpenningTime.setTextColor(Color.GREEN);
            } else {
                mOpenningTime.setText(itemView.getContext().getString(R.string.closed));
                mOpenningTime.setTextColor(Color.RED);
            }
        } else {
            mOpenningTime.setText(itemView.getContext().getString(R.string.opening_time_unavailable));
        }

        // Distance
        try {
            String resultDistance = utility.distance(
                    lat,
                    result.getGeometry().getLocation().getLat(),
                    lng,
                    result.getGeometry().getLocation().getLng());
            mDistance.setText(resultDistance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Try to retrieve the restaurant's picture
        try {
            if (result.getPhotos().get(0).getPhotoReference() != null) {
                Log.e(TAG, "updateRestaurantsList: " + result.getName() + ": ok");
                String photoRef = result.getPhotos().get(0).getPhotoReference();
                String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                        BuildConfig.ApiKey +
                        "&photoreference=" + photoRef;
                Glide.with(mRestaurantThumbnail.getContext()).load(request).into(mRestaurantThumbnail);
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "updateRestaurantsList: " + e.getMessage());
            Glide.with(mRestaurantThumbnail.getContext()).load(R.drawable.background_pic).into(mRestaurantThumbnail);
        }

        // Display number of participant
        mImageViewPpl.setVisibility(View.INVISIBLE);
        mNumberOfParticipants.setText("");
        UserHelper.getRestaurant(result.getPlaceId()).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               if (Objects.requireNonNull(task.getResult()).size() > 0) {
                   mImageViewPpl.setVisibility(View.VISIBLE);
                   mNumberOfParticipants.setText(itemView.getContext().getString(
                           R.string.nb_of_participants,
                           task.getResult().size()));
               } else {
                   mImageViewPpl.setVisibility(View.INVISIBLE);
               }
           } else {
               mImageViewPpl.setVisibility(View.INVISIBLE);
           }
        });
    }
}
