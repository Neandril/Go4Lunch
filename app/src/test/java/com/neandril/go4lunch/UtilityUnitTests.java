package com.neandril.go4lunch;

import com.neandril.go4lunch.utils.Utility;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilityUnitTests {

    private Utility utility = new Utility();

    @Test
    public void should_ratingConvertedInto3stars() {
        assertEquals(0.0f, utility.convertRating(0.0d), 0.0);
        assertEquals(0.9f, utility.convertRating(1.5d),0.0);
        assertEquals(2.85f, utility.convertRating(4.75d), 0.0);
        assertEquals(3.0f, utility.convertRating(5.0d), 0.0);
    }

    @Test
    public void should_distanceBetweenUserAndRestaurant_computedCorrectly() {
        // Coords of a restaurant in Brest
        double targetLat = 48.443126;
        double targetLng = -4.413226;
        // Coords few meters aways from the above restaurant
        double userLat = 48.442296;
        double userLng = -4.411621;

        assertEquals("150m", utility.distance(userLat, targetLat, userLng, targetLng));
    }


}