package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.neandril.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    // DESIGN
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.toolbar);

        configureToolbar();
    }


    /**
     * UI
     */
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: Toolbar configuration");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
