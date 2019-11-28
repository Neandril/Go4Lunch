package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.controllers.fragments.MapViewFragment;
import com.neandril.go4lunch.models.DetailViewModel;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.utils.UserHelper;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class RestaurantActivity extends BaseActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName();

    public String placeId;
    public String mWebsite;
    public String mPhone;
    public String mRestaurantName;
    private DetailViewModel viewModel;

    @BindView(R.id.activity_restaurant_main_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.tv_restaurantName) TextView tvRestaurantName;
    @BindView(R.id.tv_restaurantAddress) TextView tvRestaurantAddress;
    @BindView(R.id.restaurant_background_picture) ImageView ivRestaurantBackgroundImg;
    @BindView(R.id.floatting_action_button) FloatingActionButton mFab;
    @BindView(R.id.cardview) CardView mCardView;
    @BindView(R.id.cv_workmate_picture) ImageView ivWorkmatePicture;
    @BindView(R.id.cv_workmate_name) TextView tvWorkmateName;
    @BindView(R.id.restaurant_like_button) ImageButton mLikeButton;

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant;
    }

    @Override
    protected View getConstraintLayout() {
        return mConstraintLayout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        placeId = intent.getStringExtra(MapViewFragment.RESTAURANT_TAG);

        Log.d(TAG, "onCreate: Marker retrieved :" + placeId);
        this.retrieveRestaurantDetails();
        this.updateFabStatus();
        this.checkLikes();
    }

    // ***************************
    // REQUEST
    // ***************************

    /**
     * Request
     */
    private void retrieveRestaurantDetails() {
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.init(placeId);
        final Observer<Detail> observer = this::updateUI;

        viewModel.getRepository().observe(this, observer);
    }

    // ***************************
    // UI
    // ***************************

    /**
     * Update the UI
     * @param detail - Restaurant details
     */
    private void updateUI(Detail detail) {
        mRestaurantName = detail.getResult().getName();
        if (detail.getResult().getName() != null) {
            tvRestaurantName.setText(mRestaurantName);
        }

        if (detail.getResult().getVicinity() != null) {
            tvRestaurantAddress.setText(detail.getResult().getVicinity());
        }

        if (detail.getResult().getPhotos() != null) {
            String photoRef = detail.getResult().getPhotos().get(0).getPhotoReference();
            String request = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&key=" +
                    BuildConfig.ApiKey +
                    "&photoreference=" + photoRef;

            Glide.with(this).load(request).into(ivRestaurantBackgroundImg);
        }

        if (detail.getResult().getWebsite() != null) {
            mWebsite = detail.getResult().getWebsite();
        } else {
            mWebsite = null;
        }

        mPhone = detail.getResult().getFormattedPhoneNumber();
    }

    // ***************************
    // ACTIONS
    // ***************************

    /**
     * Handle website click
     */
    @OnClick(R.id.restaurant_website_button)
    public void onWebsiteButton() {
        Log.d(TAG, "onWebsiteButton: " + mWebsite);
        if (mWebsite != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mWebsite));
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_website_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle Like click
     */
    @OnClick(R.id.restaurant_like_button)
    public void onLikeButton() {

        Log.d(TAG, "onLikeButton: Try to like : " + placeId);

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                User user = Objects.requireNonNull(task.getResult()).toObject(User.class);
                List<String> likes = Objects.requireNonNull(user).getRestaurantLikedList();

                if (user.getRestaurantLikedList() != null) {
                    if (likes.contains(placeId)) {
                        Log.d(TAG, "onLikeButton: Already Liked");
                    } else {
                        Log.d(TAG, "onLikeButton: Not liked yet");
                    }
                } else {
                    // likes.add(placeId);
                    Log.d(TAG, "onLikeButton: Attempt to like: " + placeId);
                }

            }
        });
    }

    /**
     * Handle Phone click
     */
    @OnClick(R.id.restaurant_call_button)
    public void onPhoneButton() {
        Log.d(TAG, "onPhoneButton: " + mPhone);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone));
        startActivity(intent);
    }

    /**
     * Handle FAB click
     */
    @OnClick(R.id.floatting_action_button)
    public void onFabClick() {
        Log.d(TAG, "onFabClick: ");

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);

            if (Objects.requireNonNull(user).getRestaurantId().equals(placeId)) {
                // If the firebase restaurant and the placeId are the same, remove the booking
                UserHelper.updateRestaurantId(RestaurantActivity.this.getCurrentUser().getUid(), "");
                UserHelper.updateRestaurantName(RestaurantActivity.this.getCurrentUser().getUid(), "");
                mFab.setImageResource(R.drawable.fab_red_cross);
            } else {
                // If not, it's possible to book the restaurant
                UserHelper.updateRestaurantId(RestaurantActivity.this.getCurrentUser().getUid(), placeId);
                UserHelper.updateRestaurantName(RestaurantActivity.this.getCurrentUser().getUid(), mRestaurantName);
                mFab.setImageResource(R.drawable.fab_green_checkmark);
            }
        });
    }

    // ***************************
    // CONFIGURATIONS
    // ***************************

    /**
     * Compare the placeId actually displayed with the Id on Firestore
     * and update the FAB accordingly
     */
    private void updateFabStatus() {
        Log.d(TAG, "updateFabStatus: ");

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            String restaurantId = Objects.requireNonNull(user).getRestaurantId();

            if (restaurantId != null) {
                if (restaurantId.equals(placeId)) {
                    mFab.setImageResource(R.drawable.fab_green_checkmark);
                    tvWorkmateName.setText(RestaurantActivity.this.getCurrentUser().getDisplayName());

                    if (RestaurantActivity.this.getCurrentUser().getPhotoUrl() != null) {
                        Glide.with(this).load(RestaurantActivity.this.getCurrentUser().getPhotoUrl()).into(ivWorkmatePicture);
                    } else {
                        Glide.with(this).load(R.drawable.background_pic).into(ivWorkmatePicture);
                    }
                } else {
                    mFab.setImageResource(R.drawable.fab_red_cross);
                }
            }

        }).addOnFailureListener(this.onFailureListener());
    }

    /**
     * Check likes
     */
    public void checkLikes() {
        UserHelper.getUserLikes(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "checkLikes: " + Objects.requireNonNull(task.getResult()).getDocuments());
                if (task.getResult().isEmpty()) {
                    Log.e(TAG, "checkLikes: No likes for this user");
                } else {
                    for (DocumentSnapshot restaurant : task.getResult()) {
                        if (restaurant.getId().equals(placeId)) {
                            Log.e(TAG, "checkLikes: don't like");
                            break;
                        } else {
                            Log.e(TAG, "checkLikes: like");
                        }
                    }
                }
            }
        });
    }
}
