package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;

import butterknife.BindView;

public class YourLunchActivity extends BaseActivity {

    private static final String TAG = YourLunchActivity.class.getSimpleName();

    // DESIGN
    private Toolbar mToolbar;
    @BindView(R.id.your_lunch_layout) CoordinatorLayout mCoordinatorLayout;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_your_lunch;
    }

    @Override
    protected View getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_lunch);

        mToolbar = findViewById(R.id.toolbar);

        configureToolbar();
    }

    // ***************************
    // UI
    // ***************************
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: Toolbar configuration");
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
