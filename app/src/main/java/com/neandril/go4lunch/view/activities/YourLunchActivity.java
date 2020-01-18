package com.neandril.go4lunch.view.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.view.base.BaseActivity;

import butterknife.BindView;

public class YourLunchActivity extends BaseActivity {

    private static final String TAG = YourLunchActivity.class.getSimpleName();

    // DESIGN
    @BindView(R.id.your_lunch_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // ***************************
    // BASE METHODS
    // ***************************

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_your_lunch;
    }

    @Override
    protected View getConstraintLayout() {
        return mConstraintLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureToolbar();
    }

    // ***************************
    // UI
    // ***************************
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: Toolbar configuration");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
