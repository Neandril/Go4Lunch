package com.neandril.go4lunch;

import com.neandril.go4lunch.models.details.Period;
import com.neandril.go4lunch.utils.Utility;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilityUnitTests implements Utility {

    List<Period> periods;

    @Test
    public void should_ratingConvertedInto3stars() {
        assertEquals(0.0f, convertRating(0.0d), 0.0);
        assertEquals(0.9f, convertRating(1.5d),0.0);
        assertEquals(2.85f, convertRating(4.75d), 0.0);
        assertEquals(3.0f, convertRating(5.0d), 0.0);
    }

    @Test
    public void should_distanceBetweenUserAndRestaurant_computedCorrectly() {
        // Coords of a restaurant in Brest
        double targetLat = 48.443126;
        double targetLng = -4.413226;
        // Coords few meters aways from above restaurant
        double userLat = 48.442296;
        double userLng = -4.411621;

        assertEquals("150m", distance(userLat, targetLat, userLng, targetLng));
    }


}