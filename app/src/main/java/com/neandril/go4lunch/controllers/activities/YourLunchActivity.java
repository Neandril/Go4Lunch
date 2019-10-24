package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.neandril.go4lunch.R;

public class YourLunchActivity extends AppCompatActivity {

    private static final String TAG = YourLunchActivity.class.getSimpleName();

    // DESIGN
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_lunch);

        mToolbar = findViewById(R.id.toolbar);

        configureToolbar();
    }

    /**
     * UI
     */
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: Toolbar configuration");
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
