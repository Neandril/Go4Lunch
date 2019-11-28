package com.neandril.go4lunch.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.neandril.go4lunch.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String COLLECTION_LIKED = "liked";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static CollectionReference getLikedCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_LIKED);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String restaurantId, String restaurantName, List<String> restaurantLikedList) {
        User userToCreate = new User(uid, username, urlPicture, restaurantId, restaurantName, restaurantLikedList);
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<DocumentSnapshot> getRestaurantLiked(String restaurantId) {
        return UserHelper.getLikedCollection().document(restaurantId).get();
    }

    public static Task<QuerySnapshot> getUserLikes(String userId) {
        return UserHelper.getLikedCollection().whereEqualTo(userId, true).get();
    }

    public static Query getAllUsers() {
        return UserHelper.getUsersCollection().orderBy("restaurantName", Query.Direction.DESCENDING).orderBy("user_name");
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateRestaurantId(String uid, String restaurantId) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantId", restaurantId);
    }

    public static Task<Void> updateRestaurantName(String uid, String restaurantName) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantName", restaurantName);
    }

    public static Task<Void> updateRestaurantLiked(String uid, List<String> like) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantLikeList", like);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
