package com.neandril.go4lunch;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

public final class ApplicationInit extends Application {
    private Context appContext;

    public void onCreate() {
        super.onCreate();
        this.appContext = this.getApplicationContext();
    }

    @Nullable
    public final Context getAppContext() {
        return this.appContext;
    }
}
