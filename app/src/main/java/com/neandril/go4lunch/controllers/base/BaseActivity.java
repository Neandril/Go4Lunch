package com.neandril.go4lunch.controllers.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neandril.go4lunch.R;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    // Get objects
    protected abstract int getActivityLayout();
    protected abstract View getConstraintLayout();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getActivityLayout());

        Log.d(TAG, "onCreate: BaseActivity");

        ButterKnife.bind(this);
    }

    public void showSnackBar(String message) {
        Log.d(TAG, "showSnackBar: message : " + message);
        Snackbar.make(getConstraintLayout(), message, Snackbar.LENGTH_LONG).show();
    }

    // ***************************
    // PERMISSIONS
    // ***************************

    protected FirebaseUser getCurrentUser() {
        Log.d(TAG, "getCurrentUser: CurrentUser logged in Firebase");
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected boolean isCurrentUsedLogged() {
        Log.d(TAG, "isCurrentUsedLogged: " + this.getCurrentUser());
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener(){
        return e -> Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
    }
}
