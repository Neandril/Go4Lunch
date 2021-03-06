package com.neandril.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.UserHelper;
import com.neandril.go4lunch.view.activities.MainActivity;
import com.neandril.go4lunch.view.activities.RestaurantActivity;
import com.neandril.go4lunch.models.User;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();
    public static final String RESTAURANT_TAG = "restaurantId";

    private String currentUserId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Alarm triggered");

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            String restaurantId = Objects.requireNonNull(user).getRestaurantId();
            String restaurantName = user.getRestaurantName();
            String restaurantVicinity = user.getRestaurantVicinity();

            currentUserId = user.getUser_id();

            if (restaurantId != null) {
                UserHelper.getAllUsers().whereEqualTo(RESTAURANT_TAG, restaurantId).addSnapshotListener((queryDocumentSnapshots, e) -> {
                    ArrayList<String> workmatesList = new ArrayList<>();

                    for (DocumentSnapshot snapshot : Objects.requireNonNull(queryDocumentSnapshots)) {
                        User workmate = snapshot.toObject(User.class);

                        String userName = Objects.requireNonNull(workmate).getUser_name();
                        String userId = workmate.getUser_id();

                        if (!userId.equals(currentUserId)) {
                            workmatesList.add(userName);
                        }
                    }

                    createNotifications(context, restaurantId, restaurantName, restaurantVicinity, workmatesList);

                });
            }
        });
    }

    private void createNotifications(Context context,
                                     String restaurantId,
                                     String restaurant,
                                     String restaurantVicinity,
                                     ArrayList<String> workmatesList) {
        Log.d(TAG, "createNotifications: ");

        // Create the inbox style (i.e. : multi-lined notification)
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        PendingIntent pendingIntent;
        if (restaurantId.equals("")) { // If no restaurant picked, create an intent to the MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(RESTAURANT_TAG, restaurantId);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            inboxStyle.addLine(context.getString(R.string.notif_no_choice));
        } else { // If a restaurant is picked, create an itent to the Restaurant Activity
            // Create intent with the placeId to run the restaurant activity when notification is clicked
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_TAG, restaurantId);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            inboxStyle.addLine(context.getString(R.string.notif_your_lunch) + restaurant);
            inboxStyle.addLine(restaurantVicinity);

            // Display or not workmates
            if (workmatesList.size() > 0) {
                inboxStyle.addLine(context.getString(R.string.notif_with));
                for (int i = 0; i < workmatesList.size(); i++) {
                    inboxStyle.addLine(workmatesList.get(i));
                }
            } else {
                inboxStyle.addLine(context.getString(R.string.notif_nobody));
            }
        }

        // Manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setStyle(inboxStyle)
                .setContentIntent(pendingIntent);
        // Remove the notification when clicked
        builder.setAutoCancel(true);

        // Create channel for build > oreo)
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Finally, show notification on the  screen
            notificationManager.notify(1, builder.build());
        }
    }

    protected FirebaseUser getCurrentUser() {
        Log.d(TAG, "getCurrentUser: CurrentUser logged in Firebase");
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
