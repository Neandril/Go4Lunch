package com.neandril.go4lunch;

import androidx.test.rule.ActivityTestRule;

import com.neandril.go4lunch.controllers.activities.MainActivity;
import com.neandril.go4lunch.models.PlacesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ViewModels {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init() {

    }

    @Test
    public void firstTest() {
        PlacesViewModel viewModel;


    }

}
