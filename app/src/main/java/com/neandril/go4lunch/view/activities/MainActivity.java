package com.neandril.go4lunch.view.activities;

import android.app.AlertDialog;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.view.base.BaseActivity;
import com.neandril.go4lunch.view.fragments.ListViewFragment;
import com.neandril.go4lunch.view.fragments.MapViewFragment;
import com.neandril.go4lunch.view.fragments.WorkmatesFragment;
import com.neandril.go4lunch.viewmodels.PredictionsViewModel;
import com.neandril.go4lunch.models.RestaurantAutocompleteModel;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Singleton;
import com.neandril.go4lunch.models.UserHelper;
import com.neandril.go4lunch.utils.Utility;

import java.util.Objects;

import butterknife.BindView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    // TAGs and consts
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String RESTAURANT_TAG = "restaurantId";

    // Widgets
    @BindView(R.id.activity_main_constraint_layout) ConstraintLayout mConstraintLayout;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.bottom_navigation_view) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    // Variables
    private Menu menu;
    private SearchView.SearchAutoComplete mSearchAutocomplete;
    private String mPosition;
    private PredictionsViewModel predictionsViewModel;

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
        configurePredictions();

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
        setTitle(R.string.title_main);
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
                menu.findItem(R.id.action_search).setVisible(true);
                break;
            case R.id.navigation_list:
                showFragment(new ListViewFragment());
                menu.findItem(R.id.action_search).setVisible(true);
                break;
            case R.id.navigation_workmates:
                showFragment(new WorkmatesFragment());
                menu.findItem(R.id.action_search).setVisible(false);
                break;

            case R.id.nav_your_lunch:
                startYourLunchActivy();
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

    // Handle back click to close menu, or exit the app
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: Back button pressed");
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Create an alertDialog to ask the user if he want to exit the app
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.alert_dialog_title);
            builder.setMessage(R.string.alert_dialog_message);
            builder.setPositiveButton(
                    R.string.positive_button,
                    (dialog, which) -> {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
            ).setNegativeButton(
                    R.string.negative_button,
                    (dialog, which) -> dialog.cancel()
            );

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    // ***************************
    // UI
    // ***************************

    private void getUserInformations() {
        Log.d(TAG, "getUserInformations: Retrieving users informations");
        mNavigationView.setNavigationItemSelectedListener(this);

        String email;

        View headerView = mNavigationView.getHeaderView(0);
        ImageView profileThumbnail = headerView.findViewById(R.id.nav_header_profile_thumbnail);
        TextView tvName = headerView.findViewById(R.id.nav_header_user_name);
        TextView tvMail = headerView.findViewById(R.id.nav_header_user_email);

        // Fill profile informations into dedicated fields
        if (this.getCurrentUser() != null) {
            // Get email and username and display them on the header
            email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                    getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();

            tvMail.setText(email);

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
               User user = documentSnapshot.toObject(User.class);

               if (Objects.requireNonNull(user).getUser_name() != null) {
                   tvName.setText(user.getUser_name());
               } else {
                   tvName.setText(R.string.info_no_username_found);
               }

               if (user.getUser_profile_picture() != null) {
                   Glide.with(this)
                           .load(user.getUser_profile_picture())
                           .apply(RequestOptions.circleCropTransform())
                           .into(profileThumbnail);
               } else {
                   Glide.with(this)
                           .load(R.mipmap.ic_launcher)
                           .apply(RequestOptions.circleCropTransform())
                           .into(profileThumbnail);
               }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        mSearchAutocomplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        mSearchAutocomplete.setHint(R.string.search_restaurants);

        // Handle click event : start RestaurantActivity with the Id of the picked restaurant
        mSearchAutocomplete.setOnItemClickListener((adapterView, view, itemIndex, id) -> {
            RestaurantAutocompleteModel queryString = (RestaurantAutocompleteModel)adapterView.getItemAtPosition(itemIndex);
            mSearchAutocomplete.setText(queryString.getRestaurantName());

            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_TAG, queryString.getRestaurantId());
            startActivity(intent);
        });

        // Below event is triggered when submit search query.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                if (newText.length() > 2 ) {
                    // Retrieve coords
                    mPosition = Singleton.getInstance().getPosition();
                    Log.d(TAG, "onQueryTextChange: input: " + newText + ", location: " + mPosition);
                    getPredictions(newText, mPosition);
                } else if (newText.length() < 2) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if (fragment instanceof ListViewFragment) {
                        ((ListViewFragment) Objects.requireNonNull(fragment)).updateList();
                    } else if (fragment instanceof MapViewFragment) {
                        ((MapViewFragment) fragment).updateMap();
                    }
                }

                return false;
            }
        });

        return true;
    }

    public void configurePredictions() {
        predictionsViewModel = ViewModelProviders.of(this).get(PredictionsViewModel.class);
        predictionsViewModel.getPredictionLiveData().observe(this, placesResults -> {
            PlacesDetail placesDetail = new PlacesDetail();
            placesDetail.setResults(placesResults);

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment instanceof ListViewFragment) {
                ((ListViewFragment) fragment).updatePredictions(placesDetail);
            } else if (fragment instanceof MapViewFragment) {
                ((MapViewFragment) fragment).updateUiWithPredictions(placesDetail);
            }
        });
    }

    public void getPredictions(String input, String location) {
        Log.d(TAG, "getPredictions: input: " + input + ", location: " + location);
        predictionsViewModel.getPredictions(input, location);
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
        Log.d(TAG, "updateUIAfterRESTRequestsCompleted: logged off");
        return aVoid -> finish();
    }

    /**
     * Start your lunch with the restaurant picked
     */
    private void startYourLunchActivy() {
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            String restaurantId = Objects.requireNonNull(user).getRestaurantId();

            if (!restaurantId.equals("")) {
                Intent intent = new Intent(this, RestaurantActivity.class);
                intent.putExtra(RESTAURANT_TAG, restaurantId);
                startActivity(intent);
            } else {
                showSnackBar(getResources().getString(R.string.no_restaurant_picked));
            }
        });
    }
}
