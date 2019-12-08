package com.neandril.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.activities.MainActivity;
import com.neandril.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationsReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationsReceiver";
    private ArrayList<String> mMessageContent;
    private String currentUserId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Alarm triggered");

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            String restaurantId = Objects.requireNonNull(user).getRestaurantId();
            String restaurantName = user.getRestaurantName();
            currentUserId = user.getUser_id();

            mMessageContent = new ArrayList<>();
            mMessageContent.add("Reminder : ");
            mMessageContent.add("Your restaurant : " + restaurantName);
            mMessageContent.add("with: ");

            if (restaurantId != null) {
                UserHelper.getAllUsers().whereEqualTo("restaurantId", restaurantId).addSnapshotListener((queryDocumentSnapshots, e) -> {
                    ArrayList<String> workmatesList = new ArrayList<>();

                    for (DocumentSnapshot snapshot : Objects.requireNonNull(queryDocumentSnapshots)) {
                        User workmate = snapshot.toObject(User.class);

                        String userName = Objects.requireNonNull(workmate).getUser_name();
                        String userId = workmate.getUser_id();
                        Log.d(TAG, "onReceive: " + userId + " , " + userName);

                        if (!userId.equals(currentUserId)) {
                            workmatesList.add(userName);
                            // mMessageContent.add(userName);
                        }
                    }

                    Log.d(TAG, "onReceive: RESULTS: " + mMessageContent);

                    createNotifications(context, "Your are going to lunch to : ", restaurantName, workmatesList);
                });

            }
        });

    }

    private void createNotifications(Context context, String message, String restaurant, ArrayList<String> workmatesList) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String workmates = "";
        for (int i = 0; i < workmatesList.size(); i++) {
            workmates = workmatesList.get(i) + ", ";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "default");
        notification
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message + restaurant + "\nwith " +  workmates + "\nhave fun")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message + restaurant + "with " + workmates + " have fun"))
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }

    protected FirebaseUser getCurrentUser() {
        Log.d(TAG, "getCurrentUser: CurrentUser logged in Firebase");
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
