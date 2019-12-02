package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    // DESIGN
    @BindView(R.id.settings_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected View getConstraintLayout() {
        return mConstraintLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureToolbar();
        configureSpinner();

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

    private void configureSpinner() {
        Spinner spinner = findViewById(R.id.spinner_lang);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }
}
