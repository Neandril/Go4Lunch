package com.neandril.go4lunch;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.neandril.go4lunch.controllers.activities.MainActivity;
import com.neandril.go4lunch.controllers.activities.YourLunchActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.NavigationViewActions.navigateTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Test interactions between menus
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class MenuNavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void before() {
        activityTestRule.getActivity();
    }

    /**
     * Check bottom navigation items
     */
    @Test
    public void bottomNavigationItem_shouldContainsItems() {
        // Check Map View
        onView(
                allOf(
                        withId(R.id.largeLabel),
                        withText("Map View")
                )
        ).check(
                matches(isDisplayed())
        );

        // Check List View
        onView(
                allOf(
                        withId(R.id.smallLabel),
                        withText("List View")
                )
        ).check(
                matches(isDisplayed())
        );

        // Check Workmates
        onView(
                allOf(
                        withId(R.id.smallLabel),
                        withText("Workmates")
                )
        ).check(
                matches(isDisplayed())
        );
    }

    /**
     * Check bottom menu items click
     */
    @Test
    public void bottomNavigation_itemsClick_shouldDiaplayFragments() throws InterruptedException {
        //TODO: Use idling resources instead of sleep
        Thread.sleep(1000);
        // Check List View Fragment
        onView(
                allOf(
                        withId(R.id.navigation_list),
                        withContentDescription("List View")
                )
        ).perform(click());

        onView(withId(R.id.fragment_listView)).check(matches(isDisplayed()));

        // Check Workmates Fragment
        onView(
                allOf(
                        withId(R.id.navigation_workmates),
                        withContentDescription("Workmates")
                )
        ).perform(click());

        onView(withId(R.id.fragment_workmates)).check(matches(isDisplayed()));

        // Check Map View Fragment
        onView(
                allOf(
                        withId(R.id.navigation_map),
                        withContentDescription("Map View")
                )
        ).perform(click());

        onView(withId(R.id.fragment_mapView)).check(matches(isDisplayed()));
    }

    @Test
    public void navDrawer_itemsClick_shouldOpenCorrectAcitivties() {
        // Prepare next activity
        Instrumentation.ActivityMonitor activityMonitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(YourLunchActivity.class.getName(), null, false);

        // Check "Your Lunch" drawer item
        onView(
                withContentDescription(R.string.navigation_drawer_open)
        ).perform(click());

        onView(
                withId(R.id.nav_view)
        ).perform(navigateTo(R.id.nav_your_lunch));

        onView(withId(R.id.your_lunch_layout)).check(matches(isDisplayed()));
        Activity nextActivity = InstrumentationRegistry
                .getInstrumentation()
                .waitForMonitor(activityMonitor);
        onView(withContentDescription(getToolbarNavigationContentDescription(nextActivity, R.id.toolbar))).perform(click());

        // Check "Settings" drawer item
        onView(
                withContentDescription(R.string.navigation_drawer_open)
        ).perform(click());

        onView(
                withId(R.id.nav_view)
        ).perform(navigateTo(R.id.nav_settings));

        onView(withId(R.id.settings_layout)).check(matches(isDisplayed()));
    }

    // Retrieve ContentDescription of the home button in the toolbar
    // This avoid being dependant of the locale.
    public static String getToolbarNavigationContentDescription(
        @NonNull Activity activity, @IdRes int toolbarId) {
        Toolbar toolbar = activity.findViewById(toolbarId);
        if (toolbar != null) {
            return (String) toolbar.getNavigationContentDescription();
        } else {
            throw new RuntimeException("No toolbar found.");
        }
    }
}
