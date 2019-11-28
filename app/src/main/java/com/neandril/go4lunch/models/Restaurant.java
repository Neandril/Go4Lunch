package com.neandril.go4lunch.models;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private String restaurantUid;
    private String restaurantName;
    private String restaurantVicinity;
    private String restaurantUrlPhoto;
    private String restaurantPhone;
    private String restaurantWebsite;
    private List<String> restaurantLike;

    public Restaurant() { }

    public Restaurant(String restaurantUid, String restaurantName, String restaurantVicinity, String restaurantUrlPhoto, String restaurantPhone, String restaurantWebsite) {
        this.restaurantUid = restaurantUid;
        this.restaurantName = restaurantName;
        this.restaurantVicinity = restaurantVicinity;
        this.restaurantUrlPhoto = restaurantUrlPhoto;
        this.restaurantPhone = restaurantPhone;
        this.restaurantWebsite = restaurantWebsite;
        this.restaurantLike = new ArrayList<>();
    }

    public String getRestaurantUid() {
        return restaurantUid;
    }

    public void setRestaurantUid(String restaurantUid) {
        this.restaurantUid = restaurantUid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantVicinity() {
        return restaurantVicinity;
    }

    public void setRestaurantVicinity(String restaurantVicinity) {
        this.restaurantVicinity = restaurantVicinity;
    }

    public String getRestaurantUrlPhoto() {
        return restaurantUrlPhoto;
    }

    public void setRestaurantUrlPhoto(String restaurantUrlPhoto) {
        this.restaurantUrlPhoto = restaurantUrlPhoto;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getRestaurantWebsite() {
        return restaurantWebsite;
    }

    public void setRestaurantWebsite(String restaurantWebsite) {
        this.restaurantWebsite = restaurantWebsite;
    }

    public List<String> getRestaurantLike() {
        return restaurantLike;
    }

    public void setRestaurantLike(List<String> restaurantLike) {
        this.restaurantLike = restaurantLike;
    }
}
