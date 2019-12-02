package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.utils.UserHelper;

import java.util.Collections;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Identifier for sign-in
    private static final int RC_SIGN_IN = 123;

    // Layout
    @BindView(R.id.login_constraint_layout) ConstraintLayout mConstraintLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate");
        super.onCreate(savedInstanceState);

        if (this.isCurrentUsedLogged()) {
            this.enterInTheApp();
        } else {
            Log.e("Login", "Not logged");
        }
    }

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getActivityLayout() { return R.layout.activity_login; }

    @Override
    protected View getConstraintLayout() { return mConstraintLayout; }

    // ***************************
    // ACTIONS
    // ***************************

    @OnClick(R.id.google_button)
    public void onClickGoogleButton() {
        this.googleSignIn();
    }

    @OnClick(R.id.facebook_button)
    public void onClickFacebookButton() {
        this.facebookSignIn();
    }

    // ***************************
    // AUTHENTIFICATIONS
    // ***************************

    private void googleSignIn() {
        Log.d(TAG, "googleSignIn: Google SignIn called");
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Collections.singletonList(
                                new AuthUI.IdpConfig.GoogleBuilder().build() // GOOGLE
                        )
                )
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.lunch_icon)
                .build(),
            RC_SIGN_IN
        );
    }

    private void facebookSignIn() {
        Log.d(TAG, "googleSignIn: Facebook SignIn called");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build() // FACEBOOK
                                )
                        )
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.lunch_icon)
                        .build(),
                RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (data != null) {
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
        }
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleResponseAfterSignIn: ");
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                showSnackBar("Success");
                this.enterInTheApp();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar("Canceled");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar("No Internet");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar("Unknown error");
                } else if (response.getError().getErrorCode() == ErrorCodes.DEVELOPER_ERROR) {
                    showSnackBar("Adress email already exist");
                }
            }
        }
    }

    private void createUserInFirestore() {
        if (getCurrentUser() != null) {
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            
            UserHelper.createUser(uid, username, urlPicture, "", "", null);
        }
    }

    // ***************************
    // UI
    // ***************************
    // Enter the application
    public void enterInTheApp() {
        Log.d(TAG, "enterInTheApp: Enter in the application");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
