package com.neandril.go4lunch.models;

// Model used for the autocomplete adapter

public class RestaurantAutocompleteModel {

    private String restaurantId;
    private String restaurantName;
    private String restaurantVicinity;

    public RestaurantAutocompleteModel(String restaurantId, String restaurantName, String restaurantVicinity) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantVicinity = restaurantVicinity;
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

    public String getRestaurantVicinity() {
        return restaurantVicinity;
    }

    public void setRestaurantVicinity(String restaurantVicinity) {
        this.restaurantVicinity = restaurantVicinity;
    }
}
