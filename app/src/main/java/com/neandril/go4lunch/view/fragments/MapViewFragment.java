package com.neandril.go4lunch.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.view.activities.RestaurantActivity;
import com.neandril.go4lunch.view.base.BaseFragment;
import com.neandril.go4lunch.viewmodels.PlacesViewModel;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Singleton;
import com.neandril.go4lunch.models.UserHelper;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();
    public static final String RESTAURANT_TAG = "restaurantId";

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 15f;
    private double latitude,longitude;
    private String position;
    private PlacesViewModel viewModel;

    @BindView(R.id.map) MapView mapView;

    // ***************************
    // BASE METHODS
    // ***************************
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_mapview; }

    @Override
    protected void configureFragment() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        this.configureMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Resuming");
        getLocationSettings();
    }

    // ***************************
    // CONFIGURATIONS
    // ***************************

    private void configureMap() {
        Log.d(TAG, "configureMap: Map configuration");
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
        }

        getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Entering onMapReady callback");
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
    }

    // ***************************
    // UI
    // ***************************

    private void updateUiWithMarkers(PlacesDetail placesDetail) {
        Log.e(TAG, "updateUiWithMarkers: size :" + placesDetail.getResults().size());

        if (placesDetail.getResults().size() != 0 || placesDetail.getResults() != null) {
            for (int i = 0; i < placesDetail.getResults().size(); i++) {
                Double lat = placesDetail.getResults().get(i).getGeometry().getLocation().getLat();
                Double lng = placesDetail.getResults().get(i).getGeometry().getLocation().getLng();
                String placeName = placesDetail.getResults().get(i).getName();
                String vicinity = placesDetail.getResults().get(i).getVicinity();
                String id = placesDetail.getResults().get(i).getPlaceId();
                LatLng latLng = new LatLng(lat, lng);

                Log.e(TAG, "updateUiWithMarkers: Name: " + placeName);

                // Get all users, then, if the query size is positive, place a green marker (an orange one otherwise)
                Query query = UserHelper.getAllUsers().whereEqualTo("restaurantId", id);
                query.addSnapshotListener((queryDocumentSnapshots, e) -> {

                    try {
                        if (Objects.requireNonNull(queryDocumentSnapshots).size() == 0) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(placeName + " : " + vicinity)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange)))
                                    .setTag(id);
                        } else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(placeName + " : " + vicinity)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green)))
                                    .setTag(id);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

            }
        } else {
            Log.e(TAG, "updateUiWithMarkers: no result found");
        }
    }

    // Update the map accordingly to predictions
    public void updateUiWithPredictions(PlacesDetail placesDetail) {
        Log.d(TAG, "updateUiWithPredictions: ");
        mMap.clear();
        updateUiWithMarkers(placesDetail);
    }

    // Update the map
    public void updateMap() {
        Log.d(TAG, "updateMap: ");
        mMap.clear();
        getLastKnownLocation();
    }

    // ***************************
    // LOCATION
    // ***************************

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: ");
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Display the current location on the device screen
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                position = latitude + "," + longitude;

                cameraUpdate(latLng);

                Singleton.getInstance().setUserLat(latitude);
                Singleton.getInstance().setUserLng(longitude);
                Singleton.getInstance().setPosition(position);

                // Request
                viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlacesViewModel.class);
                viewModel.getPlacesLiveData().observe(this, this::updateUiWithMarkers);
                viewModel.getPlaces(position);

            } else {
                Toast.makeText(getContext(), R.string.cannot_get_location, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cameraUpdate(LatLng latLng) {
        Log.d(TAG, "cameraUpdate: Moving the camera to : lat : " + latLng.latitude + ", lng : " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    // ***************************
    // PERMISSIONS
    // ***************************

    // Check locations settings of the phone
    private void getLocationSettings() {
        Log.d(TAG, "getLocationSettings: ");
        // Create a location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(9000);
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Get current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // Check if current location settings are convenient
        SettingsClient client = LocationServices.getSettingsClient(Objects.requireNonNull(getContext()));
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // Add callback to location request task
        task.addOnFailureListener(Objects.requireNonNull(getActivity()), e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                // Location Settings are not satisfied
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Google Play Services not installed, or GPS disabled
                    Toast.makeText(getContext(), R.string.common_status_code_resolution_required, Toast.LENGTH_LONG).show();
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Toast.makeText(getContext(), R.string.location_status_code_change_unavailable, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        task.addOnSuccessListener(Objects.requireNonNull(getActivity()), locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            getPermissions();
        });
    }

    // Ask for permission (using Dexter)
    private void getPermissions() {
        Log.d(TAG, "getPermissions: Getting user location");
        // Manage permissions
        Dexter
                .withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d(TAG, "onPermissionGranted: Granted");
                        getLastKnownLocation();
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        mMap.setMyLocationEnabled(true);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Log.e("Dexter", "Permission Denied");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Log.e("Dexter", "Permission Rationale");
                    }
                })
                .check();
    }

    // ***************************
    // ACTIONS
    // ***************************

    // Handle click on customized MyLocation Button
    @OnClick(R.id.fab_mylocation)
    void myLocationOnClick() {
        Log.d(TAG, "myLocationOnClick: clicked!");
        getLastKnownLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: Marker:" + marker.getSnippet());

        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        intent.putExtra(RESTAURANT_TAG, Objects.requireNonNull(marker.getTag()).toString());
        startActivity(intent);
        return true;
    }
}
