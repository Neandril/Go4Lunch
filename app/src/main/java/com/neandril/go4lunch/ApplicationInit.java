package com.neandril.go4lunch;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

public final class ApplicationInit extends Application {

    private static final String TAG = ApplicationInit.class.getSimpleName();

    private Context appContext;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: Entry Point of the application");
        this.appContext = this.getApplicationContext();
    }

    @Nullable
    public final Context getAppContext() {
        return this.appContext;
    }
}
