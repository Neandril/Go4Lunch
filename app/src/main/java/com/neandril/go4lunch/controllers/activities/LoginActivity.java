package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.neandril.go4lunch.R;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    // Identifier for sign-in
    private static final int RC_SIGN_IN = 123;

    // Widgets
    private Button googleButton;
    private ConstraintLayout constraintLayout; // Retrieve the layout for snackbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        constraintLayout = findViewById(R.id.login_constraint_layout);
        googleButton = findViewById(R.id.google_button);

        configureClickListeners();
    }

    /**
     * ACTIONS
     */
    // Handle clicks
    private void configureClickListeners() {
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Login", "Google Button clicked");
                googleSignIn();
            }
        });
    }

    /**
     * AUTHENTIFICATION
     */
    private void googleSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(
                        Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        )
                )
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.ic_launcher_background)
                .build(),
            RC_SIGN_IN
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            this.handleResponseAfterSignIn(requestCode, resultCode, data);
        }
    }

    // Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                showSnackBar(this.constraintLayout, "Success");
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.constraintLayout, "Canceled");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.constraintLayout, "No Internet");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.constraintLayout, "Unknown error");
                }
            }
        }
    }

    /**
     * UI
     */
    // Snackbar message
    private void showSnackBar(ConstraintLayout constraintLayout, String message){
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
