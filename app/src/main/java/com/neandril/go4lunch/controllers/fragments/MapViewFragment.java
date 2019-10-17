package com.neandril.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.base.BaseFragment;

import java.util.Objects;

import butterknife.BindView;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_CHECK_SETTINGS = 920;

    @BindView(R.id.map) MapView mapView;

    /**
     * BASE METHODS
     */
    @Override
    protected int getFragmentLayout() { return R.layout.fragment_mapview; }

    @Override
    protected void configureFragment() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        // requestPermissions();
        this.configureMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationSettings();
    }

    /**
     * CONFIGURATIONS
     */

    private void configureMap() {
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.getMapAsync(this);
        }

        getChildFragmentManager().beginTransaction().replace(R.id.fragment_mapView, mMapFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * LOCATIONS
     */

    private void getUserCurrentLocation() {
        // Manage permissions
        Dexter
                .withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        getLastKnownLocation();
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

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        // call getLastLocation
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Display the current location on the device screen
                Toast.makeText(getContext(), "Latitude is :" + location.getLatitude() + " and Longitude is: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                cameraUpdate(latLng);
            } else {
                Toast.makeText(getContext(), "Cannot get user current location at the moment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cameraUpdate(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }

    /**
     * PERMISSIONS
     */

    private void getLocationSettings() {
        // Create a location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(9000);
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we will not show the dialog.
                    break;
            }
        });

        task.addOnSuccessListener(Objects.requireNonNull(getActivity()), locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getUserCurrentLocation();
        });
    }
}
