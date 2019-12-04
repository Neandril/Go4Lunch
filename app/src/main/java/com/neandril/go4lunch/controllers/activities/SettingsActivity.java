package com.neandril.go4lunch.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.utils.UserHelper;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    // DESIGN
    @BindView(R.id.settings_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.profile_thumbnail) ImageView mProfileThumbnail;
    @BindView(R.id.editText_user_name) EditText mUserNameEditText;
    @BindView(R.id.btn_save) Button mBtnSave;

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

        this.configureToolbar();
        this.configureSpinner();
        this.configureUi();

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

    private void configureUi() {

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
           User user = documentSnapshot.toObject(User.class);

           if (Objects.requireNonNull(user).getUser_profile_picture() != null) {
               Glide.with(this).load(user.getUser_profile_picture()).into(mProfileThumbnail);
           }

           if (user.getUser_name() != null) {
               mUserNameEditText.setText(user.getUser_name());
           }

        });
    }

    @OnClick(R.id.btn_save)
    void btnSaveOnClick() {
        String new_UserName = mUserNameEditText.getText().toString();

       UserHelper.updateUsername(new_UserName, this.getCurrentUser().getUid());
    }
}
