package com.neandril.go4lunch.controllers.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.utils.UserHelper;
import com.neandril.go4lunch.utils.Utility;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    public static final int RC_CHOOSE_PHOTO = 200;
    private Uri localImage;

    // DESIGN
    @BindView(R.id.settings_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.profile_thumbnail) ImageView mProfileThumbnail;
    @BindView(R.id.editText_user_name) EditText mUserNameEditText;
    @BindView(R.id.btn_save) Button mBtnSave;
    @BindView(R.id.btn_delete_account) Button mBtnDeleteAccount;
    @BindView(R.id.spinner_lang) Spinner mSpinner;

    Utility utility = new Utility();

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
        this.getLocale();
        this.configureUi();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
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

    // ***************************
    // ACTIONS
    // ***************************

    @OnClick(R.id.btn_delete_account)
    void btnDeleteAcountOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_account_title);
        builder.setMessage(R.string.delete_account);
        builder.setPositiveButton(
                R.string.positive_button,
                (dialog, which) -> {
                    if (this.getCurrentUser() != null) {
                        UserHelper.deleteUser(this.getCurrentUser().getUid());
                        Log.d(TAG, "btnDeleteAcountOnClick: account deleted");
                        finish();
                    }
                }
        ).setNegativeButton(
                R.string.negative_button,
                (dialog, which) -> dialog.cancel()
        );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick(R.id.btn_save)
    void btnSaveOnClick() {
        String new_UserName = mUserNameEditText.getText().toString();

        UserHelper.updateUsername(new_UserName, this.getCurrentUser().getUid());

        String locale = "";
        int spinner_pos = mSpinner.getSelectedItemPosition();
        switch (spinner_pos) {
            case 0:
                locale = "en";
                break;
            case 1:
                locale = "fr";
                break;
        }
        setLocale(locale);
        Log.d(TAG, "btnSaveOnClick: Spinner: " + locale);

        showSnackBar(getResources().getString(R.string.changes_saved));
    }

    @OnClick(R.id.profile_thumbnail)
    void profileThumbnailOnClick() {
        Log.d(TAG, "profileThumbnailOnClick: Clicked! ");

        // Get storage read permission
        Dexter
                .withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                Log.d(TAG, "onPermissionGranted: Granted");
                chooseLocalPicture();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Log.d(TAG, "onPermissionDenied: Denied");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                Log.d(TAG, "onPermissionRationaleShouldBeShown: Rationale");
            }
        })
                .check();
    }

    private void chooseLocalPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleResponse: ");
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                this.localImage = data.getData();
                Glide.with(this).load(localImage).into(mProfileThumbnail);
                uploadProfilePictureAndUpdateUser();
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfilePictureAndUpdateUser() {
        String uuid = UUID.randomUUID().toString();
        String userId = this.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(uuid);
        storageReference.putFile(localImage).addOnSuccessListener(taskSnapshot -> {

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d(TAG, "onSuccess: Uri: " + uri.toString());
                UserHelper.updateProfilePicture(uri.toString(), userId);
            });

        }).addOnFailureListener(this.onFailureListener());
    }

    private void setLocale(String lang) {
        Log.d(TAG, "setLocale: Lang: " + lang);
        Locale locale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        conf.setLocale(new Locale(lang.toLowerCase()));
        res.updateConfiguration(conf, metrics);

        utility.setLocaleInPrefs(this, lang);

        restart(this);
    }

    private void getLocale() {
        Log.d(TAG, "getLocale: current locale: " + utility.retriveLocaleFromPrefs(this));
        String locale = utility.retriveLocaleFromPrefs(this);

        if (locale.equals("fr")) {
            mSpinner.setSelection(1);
        } else {
            mSpinner.setSelection(0);
        }
    }

    private void restart(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
