package com.neandril.go4lunch.utils;

import com.neandril.go4lunch.models.details.Period;

import java.util.Calendar;
import java.util.List;

/**
 * Utility Interface containing many usefull methods
 */

public class Utility {

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
     * Get the day of today
     * @return - today
     */
    public Integer dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        // 1 = Monday, ... , 7 = Sunday
        return calendar.get(Calendar.DAY_OF_WEEK)-1;
    }

    public String openingTime(boolean isOpen, List<Period> periodList) {

        return null;
    }

    public Integer getWeekday() {
        Calendar calendar = Calendar.getInstance();
        // Maps Api : Sunday = 0
        return calendar.get(Calendar.DAY_OF_WEEK) -1;
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
        //Rounded
        return Math.round(Math.sqrt(distance)) + "m";
    }

}
