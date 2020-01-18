package com.neandril.go4lunch.view.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.neandril.go4lunch.utils.NotificationReceiver;

import java.util.Calendar;

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

    // ***************************
    // ALARMS
    // ***************************
    protected void configureAlarm() {
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.HOUR_OF_DAY) > 12)
            calendar.add(Calendar.DATE, 1);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    protected void stopAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        alarmManager.cancel(pendingIntent);
    }
}
