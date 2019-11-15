package com.neandril.go4lunch.utils;

/**
 * Class holding informations needed to be retrieved
 */

public class Singleton {

    private static final Singleton instance = new Singleton();
    private Double userLat;
    private Double userLng;

    private Singleton() {
    }

    public static Singleton getInstance() {
        return instance;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLng() {
        return userLng;
    }

    public void setUserLng(Double userLng) {
        this.userLng = userLng;
    }
}
