package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.firestore.DocumentSnapshot;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.utils.UserHelper;
import com.neandril.go4lunch.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    Utility utility = new Utility();

    // Identifier for sign-in
    private static final int RC_SIGN_IN = 123;

    // Layout
    @BindView(R.id.login_constraint_layout) ConstraintLayout mConstraintLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate");
        super.onCreate(savedInstanceState);

        // Update language
        String language = Locale.getDefault().getLanguage();
        String storedLocale = utility.retriveLocaleFromPrefs(getBaseContext());

        if (!language.equals(storedLocale)) {
            Locale locale = new Locale(storedLocale);
            Resources res = getResources();
            DisplayMetrics metrics = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(locale);
            conf.setLocale(new Locale(storedLocale.toLowerCase()));
            res.updateConfiguration(conf, metrics);
        }

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

    @OnClick(R.id.mail_button)
    public void onClickEmailButton() {
        this.emailSignIn();
    }

    @OnClick(R.id.twitter_button)
    public void onClickTwitterButton() {
        this.twitterSignIn();
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

    private void emailSignIn() {
        Log.d(TAG, "emailSignIn: e-Mail SignIn called");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.EmailBuilder().build() // EMAIL
                                )
                        )
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.lunch_icon)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void twitterSignIn() {
        Log.d(TAG, "twitterSignIn: Twitter SignIn called");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Collections.singletonList(
                                        new AuthUI.IdpConfig.TwitterBuilder().build() // TWITTER
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
                showSnackBar(getString(R.string.connexion_successful));

            } else { // ERRORS
                if (response == null) {
                    showSnackBar(getString(R.string.connexion_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(getString(R.string.connexion_no_network));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getString(R.string.error_unknown_error));
                } else if (response.getError().getErrorCode() == ErrorCodes.PROVIDER_ERROR) {
                    showSnackBar(getString(R.string.connexion_provider_error));
                }
            }
        }
    }

    private void createUserInFirestore() {
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               DocumentSnapshot documentSnapshot = task.getResult();
               if (Objects.requireNonNull(documentSnapshot).exists()) {
                   Log.d(TAG, "createUserInFirestore: Existing user");
                   this.enterInTheApp();
               } else {
                   Log.d(TAG, "createUserInFirestore: User don't exists");
                   if (getCurrentUser() != null) {
                       String username = this.getCurrentUser().getDisplayName();
                       String uid = this.getCurrentUser().getUid();
                       String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
                       ArrayList<String> restaurantLikeList = new ArrayList<>();

                       UserHelper.createUser(uid, username, urlPicture, "", "", "", restaurantLikeList);

                       this.enterInTheApp();
                   }
               }
           }
        }).addOnFailureListener(this.onFailureListener());
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
