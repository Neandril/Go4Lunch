package com.neandril.go4lunch.models;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String USER_NAME = "user_name";
    private static final String RESTAURANT_ID = "restaurantId";
    private static final String RESTAURANT_NAME = "restaurantName";
    private static final String RESTAURANT_VICINITY = "restaurantVicinity";
    private static final String RESTAURANT_LIKED_LIST = "restaurantLikedList";
    private static final String PICTURE_URL = "user_profile_picture";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid,
                                        String username,
                                        String urlPicture,
                                        String restaurantId,
                                        String restaurantName,
                                        String restaurantVicinity,
                                        ArrayList<String> restaurantLikedList) {
        User userToCreate = new User(uid, username, urlPicture, restaurantId, restaurantName, restaurantVicinity, restaurantLikedList);
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection().orderBy(RESTAURANT_NAME, Query.Direction.DESCENDING).orderBy(USER_NAME);
    }

    public static Task<QuerySnapshot> getRestaurant(String restaurantId) {
        return UserHelper.getUsersCollection().whereEqualTo(RESTAURANT_ID, restaurantId).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update(USER_NAME, username);
    }

    public static Task<Void> updateRestaurantId(String uid, String restaurantId) {
        return UserHelper.getUsersCollection().document(uid).update(RESTAURANT_ID, restaurantId);
    }

    public static Task<Void> updateRestaurantName(String uid, String restaurantName) {
        return UserHelper.getUsersCollection().document(uid).update(RESTAURANT_NAME, restaurantName);
    }

    public static Task<Void> updateRestaurantVicinity(String uid, String restaurantVicinity) {
        return UserHelper.getUsersCollection().document(uid).update(RESTAURANT_VICINITY, restaurantVicinity);
    }

    public static Task<Void> updateRestaurantLiked(String uid, List<String> like) {
        return UserHelper.getUsersCollection().document(uid).update(RESTAURANT_LIKED_LIST, like);
    }

    public static Task<Void> updateProfilePicture(String urlImage, String uid) {
        return UserHelper.getUsersCollection().document(uid).update(PICTURE_URL, urlImage);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
