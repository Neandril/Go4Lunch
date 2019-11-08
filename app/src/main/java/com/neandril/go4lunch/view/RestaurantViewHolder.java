package com.neandril.go4lunch.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.models.places.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "RestaurantViewHolder";

    @BindView(R.id.tv_recyclerView_restaurantName) TextView mRestaurantName;
    @BindView(R.id.tv_recyclerView_restaurantAddress) TextView mRestaurantAddress;
    @BindView(R.id.tv_recyclerView_OpenningTime) TextView mOpenningTime;
    @BindView(R.id.tv_distance) TextView mDistance;
    @BindView(R.id.tv_nb_of_participants) TextView mNumberOfParticipants;
    @BindView(R.id.imageView_ppl) ImageView mImageViewPpl;
    @BindView(R.id.imageView_restaurant_thumbnail) ImageView mRestaurantThumbnail;
    @BindView(R.id.ratingBar) RatingBar mRatingBar;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateRestaurantsList(Result result, RequestManager glide) {
        Log.d(TAG, "updateUi: ");

        this.mRestaurantName.setText(result.getName());
        this.mRestaurantAddress.setText(result.getVicinity());

        if (result.getRating() != null) {
            int rating = (int) Math.round(result.getRating() * 3/5);
            switch (rating) {
                case 0:
                    mRatingBar.setRating(0);
                    break;
                case 1:
                    mRatingBar.setRating(1);
                    break;
                case 2:
                    mRatingBar.setRating(2);
                    break;
                case 3:
                    mRatingBar.setRating(3);
                    break;
                default:
                    break;
            }
        } else {

            mRatingBar.setRating(0);

        }

        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                mOpenningTime.setText("Open now");
            } else {
                mOpenningTime.setText("Closed");
            }
        } else {
            mOpenningTime.setText("Openning time unavailaible");
        }

        if (result.getPhotos() != null) {
            Log.e(TAG, "updateRestaurantsList: photosRef:");
        }


/*        if (result.getPhotos() != null) {
            String photoRef = result.getPhotos().get(0).getPhotoReference();
            String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                    BuildConfig.ApiKey +
                    "&photoreference=" + photoRef;
            glide.load(request).into(mRestaurantThumbnail);
        }*/


    }
}
