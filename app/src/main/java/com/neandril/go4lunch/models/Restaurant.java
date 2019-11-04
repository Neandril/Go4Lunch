package com.neandril.go4lunch.models;

public class Restaurant {

    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private String restaurantOpenningTime;
    private String restaurantPhotoUrl;
    private String restaurantWebSiteUrl;
    private String restaurantType;
    private String restaurantLat;
    private String restaurantLng;
    private String restaurantPhone;

    public Restaurant(String restaurantId, String restaurantName,
                      String restaurantAddress, String restaurantOpenningTime,
                      String restaurantPhotoUrl, String restaurantWebSiteUrl,
                      String restaurantType, String restaurantLat, String restaurantLng,
                      String restaurantPhone) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantOpenningTime = restaurantOpenningTime;
        this.restaurantPhotoUrl = restaurantPhotoUrl;
        this.restaurantWebSiteUrl = restaurantWebSiteUrl;
        this.restaurantType = restaurantType;
        this.restaurantLat = restaurantLat;
        this.restaurantLng = restaurantLng;
        this.restaurantPhone = restaurantPhone;
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

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantOpenningTime() {
        return restaurantOpenningTime;
    }

    public void setRestaurantOpenningTime(String restaurantOpenningTime) {
        this.restaurantOpenningTime = restaurantOpenningTime;
    }

    public String getRestaurantPhotoUrl() {
        return restaurantPhotoUrl;
    }

    public void setRestaurantPhotoUrl(String restaurantPhotoUrl) {
        this.restaurantPhotoUrl = restaurantPhotoUrl;
    }

    public String getRestaurantWebSiteUrl() {
        return restaurantWebSiteUrl;
    }

    public void setRestaurantWebSiteUrl(String restaurantWebSiteUrl) {
        this.restaurantWebSiteUrl = restaurantWebSiteUrl;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getRestaurantLat() {
        return restaurantLat;
    }

    public void setRestaurantLat(String restaurantLat) {
        this.restaurantLat = restaurantLat;
    }

    public String getRestaurantLng() {
        return restaurantLng;
    }

    public void setRestaurantLng(String restaurantLng) {
        this.restaurantLng = restaurantLng;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }
}
