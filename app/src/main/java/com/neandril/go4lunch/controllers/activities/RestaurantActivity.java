package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;

import butterknife.BindView;

public class RestaurantActivity extends BaseActivity {

    private static final String TAG = RestaurantActivity.class.getSimpleName();

    @BindView(R.id.activity_restaurant_main_layout) CoordinatorLayout mCoordinatorLayout;

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
    }
}
