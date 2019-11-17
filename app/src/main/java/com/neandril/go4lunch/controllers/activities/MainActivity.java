package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.controllers.fragments.ListViewFragment;
import com.neandril.go4lunch.controllers.fragments.MapViewFragment;
import com.neandril.go4lunch.controllers.fragments.WorkmatesFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Widgets
    @BindView(R.id.activity_main_constraint_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.bottom_navigation_view) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private LatLngBounds mLatLngBounds;

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected View getConstraintLayout() {
        return mConstraintLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFragment(new MapViewFragment());

        Places.initialize(this, BuildConfig.ApiKey);
        PlacesClient placesClient = Places.createClient(this);

        configureToolbar();
        configureDrawer();
        configureNavigationView();
        configureBottomNavigationView();

        getUserInformations();
    }

    // ***************************
    // CONFIGURATIONS
    // ***************************
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: Toolbar configuration");
        setSupportActionBar(mToolbar);
        if (getActionBar() != null) {
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    private void configureDrawer() {
        Log.d(TAG, "configureDrawer: Drawer configuration");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        Log.d(TAG, "configureNavigationView: NavigationView configuration");
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void configureBottomNavigationView() {
        Log.d(TAG, "configureBottomNavigationView: BottomNavigationView configuration");
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    // Handle click on menus
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d(TAG, "onNavigationItemSelected: Navigation items");
        switch (menuItem.getItemId()) {
            case R.id.navigation_map:
                showFragment(new MapViewFragment());
                break;
            case R.id.navigation_list:
                showFragment(new ListViewFragment());
                break;
            case R.id.navigation_workmates:
                showFragment(new WorkmatesFragment());
                break;

            case R.id.nav_your_lunch:
                Intent yourLunch = new Intent(this, YourLunchActivity.class);
                startActivity(yourLunch);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.nav_logout:
                signOutUserFromFirebase();
                break;
        }
        return true;
    }

    public LatLngBounds getLatLngBounds() {
        return mLatLngBounds;
    }

    public void setmLatLngBounds(LatLngBounds latLngBounds) {
        this.mLatLngBounds = latLngBounds;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Log.e(TAG, "onOptionsItemSelected: Search clicked");

/*                RectangularBounds rectangularBounds = RectangularBounds.newInstance(getLatLngBounds().southwest, getLatLngBounds().northeast);

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setCountry("fr")
                        .setLocationBias(rectangularBounds)
                        .setHint("Search restaurants")
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);*/


                // Initialize the AutocompleteSupportFragment.
                AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

                // Specify the types of place data to return.
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

                // Set up a PlaceSelectionListener to handle the response.
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i(TAG, "An error occurred: " + status);
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d(TAG, "onActivityResult: Place name: " + place.getName() + ", Place id: " + place.getId());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d(TAG, "onActivityResult: Status: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: Operation canceled");
            }
        }
    }

    // Create the searchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Menu created");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchViewItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: Query: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: NewText: " + newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // ***************************
    // ACTIONS
    // ***************************

    // Method launching fragments
    private void showFragment(Fragment fragment) {
        Log.d(TAG, "showFragment: Run selected fragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    // Handle back click to close menu
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: Back button pressed");
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ***************************
    // REQUESTS
    // ***************************

    private void signOutUserFromFirebase() {
        Log.d(TAG, "signOutUserFromFirebase: Log out user from Firebase");
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted());
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(){
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted: ");
        return aVoid -> finish();
    }

    // ***************************
    // UI
    // ***************************

    private void getUserInformations() {
        Log.d(TAG, "getUserInformations: Retrieving users informations");
        mNavigationView.setNavigationItemSelectedListener(this);

        String email;
        String username;

        View headerView = mNavigationView.getHeaderView(0);
        ImageView profileThumbnail = headerView.findViewById(R.id.nav_header_profile_thumbnail);
        TextView tvName = headerView.findViewById(R.id.nav_header_user_name);
        TextView tvMail = headerView.findViewById(R.id.nav_header_user_email);

        // Fill profile informations into dedicated fields
        if (this.getCurrentUser() != null) {
            // If profile picture is found, glide'it
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileThumbnail);
            }

            // Get email and username and display them on the header
            Log.e(TAG, "getUserInformations: mail : " + this.getCurrentUser().getEmail());
            email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                    getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ?
                    getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            tvMail.setText(email);
            tvName.setText(username);
        }
    }
}
