package com.neandril.go4lunch.controllers.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseActivity;
import com.neandril.go4lunch.controllers.fragments.ListViewFragment;
import com.neandril.go4lunch.controllers.fragments.MapViewFragment;
import com.neandril.go4lunch.controllers.fragments.WorkmatesFragment;
import com.neandril.go4lunch.models.RestaurantAutocompleteModel;
import com.neandril.go4lunch.utils.UserHelper;
import com.neandril.go4lunch.view.AutocompleteAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    // Widgets
    @BindView(R.id.activity_main_constraint_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.bottom_navigation_view) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.layout_autocomplete) LinearLayout mLayoutAutocomplete;
    @BindView(R.id.autocompleteTextView) AutoCompleteTextView mAutoCompleteTextView;

    private LatLngBounds mLatLngBounds;
    private String restaurantName;
    private String restaurantVicinity;

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

    private void configurePredictions(Editable str) {
        Log.d(TAG, "configurePredictions: " + str.toString());
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds rectangularBounds = RectangularBounds.newInstance(getLatLngBounds().southwest, getLatLngBounds().northeast);

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(rectangularBounds)
                .setCountry("FR")
                .setQuery(str.toString())
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<RestaurantAutocompleteModel> restaurants = new ArrayList<>();

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                restaurantName = prediction.getPrimaryText(STYLE_BOLD).toString();
                restaurantVicinity = prediction.getSecondaryText(null).toString();

                restaurants.add(new RestaurantAutocompleteModel(
                        prediction.getPlaceId(),
                        restaurantName,
                        restaurantVicinity));
            }

            AutocompleteAdapter adapter = new AutocompleteAdapter(this, restaurants);
            mAutoCompleteTextView.setAdapter(adapter);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
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
        if ((this.mDrawerLayout.isDrawerOpen(GravityCompat.START) || mLayoutAutocomplete.getVisibility() == View.VISIBLE)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mLayoutAutocomplete.setVisibility(View.GONE);
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

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }*/


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, R.string.search_restaurants)
                .setIcon(R.drawable.ic_search);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setOnMenuItemClickListener(item -> {

            if (mLayoutAutocomplete.getVisibility() == View.GONE) {
                mLayoutAutocomplete.setVisibility(View.VISIBLE);
            } else {
                mLayoutAutocomplete.setVisibility(View.GONE);
            }

            mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 2) {
                        Log.e(TAG, "onQueryTextChange: NewText: " + s);
                        configurePredictions(s);
                    }
                }
            });

            return true;
        });

        return super.onPrepareOptionsMenu(menu);
    }
}
