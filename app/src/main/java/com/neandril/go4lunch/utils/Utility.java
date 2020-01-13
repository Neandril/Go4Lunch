package com.neandril.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing many usefull methods
 */

public class Utility {

    private static final String PREFS = "com.neandril.go4lunch.prefs";
    private static final String LOCALE_KEY = "locale";
    private static final String PREFS_LIKES = "likes";
    private static final String PREFS_TOGGLE = "toggle";
    private final Gson gson = new Gson();

    /**
     * Convert 5-stars rating into 3-stars
     * @param rating - retrieved from Google
     * @return - float value
     */
    public float convertRating(double rating) {
        rating = (rating * 3) / 5;
        return (float) rating;
    }

    /**
     * Compute distance between 2 points
     * @param userLat - user latitude
     * @param targetLat - restaurant latitude
     * @param userLng - user longitude
     * @param targetLng - target longitude
     * @return - result of the operation superceded by " m" for meters
     */

    public String distance(double userLat, double targetLat, double userLng, double targetLng) {

        final int earth = 6371; // Earth radius

        double latDistance = Math.toRadians(targetLat - userLat);
        double lonDistance = Math.toRadians(targetLng - userLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earth * c * 1000; // convert to meters

        double height = 0.0 - 0.0;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        // Rounded
        return Math.round(Math.sqrt(distance)) + "m";
    }

    /**
     * PREFERENCES - sharedpreferences ; get and set locale ;
     * @param context - context
     * @return - prefs
     */
    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public String retriveLocaleFromPrefs(Context context) {
        return getSharedPreferences(context).getString(LOCALE_KEY, "en");
    }

    public void setLocaleInPrefs(Context context, String locale) {
        getSharedPreferences(context)
                .edit()
                .putString(LOCALE_KEY, locale)
                .apply();
    }

    /**
     * PREFERENCES - sharedpreferences ; get and set likes list ;
     * @param context - context
     */
    public void setLikeListInPrefs(Context context, List<String> likeList) {
        String json = gson.toJson(likeList);
        getSharedPreferences(context)
                .edit()
                .putString(PREFS_LIKES, json)
                .apply();
    }

    public List<String> retriveLikeList(Context context) {
        String json = getSharedPreferences(context).getString(PREFS_LIKES, "[]");
        ArrayList<String> list;
        if (json == null || json.isEmpty()) {
            list = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            list = gson.fromJson(json, type);
        }

        return list;
    }

    /**
     * PREFERENCES - sharedpreferences ; get and set notifications toggle ;
     * @param context - context
     * @param toggle - boolean
     */
    public void setNotifToggleInPrefs(Context context, boolean toggle) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(PREFS_TOGGLE, toggle)
                .apply();
    }

    public boolean retrieveToggleFromPrefs(Context context) {
        return getSharedPreferences(context).getBoolean(PREFS_TOGGLE, true);
    }
}
