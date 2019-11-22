package com.neandril.go4lunch.models;

public class User {
    private String user_id;
    private String user_name;
    private String user_profile_picture;
    private String restaurantId;
    private String restaurantName;

    // Required for Firestore
    public User() { }

    public User(String user_id, String user_name, String user_profile_picture, String restaurantId, String restaurantName) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile_picture = user_profile_picture;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_picture() {
        return user_profile_picture;
    }

    public void setUser_profile_picture(String user_profile_picture) {
        this.user_profile_picture = user_profile_picture;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
