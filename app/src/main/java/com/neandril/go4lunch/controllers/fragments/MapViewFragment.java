package com.neandril.go4lunch.controllers.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.activities.RestaurantActivity;
import com.neandril.go4lunch.controllers.base.BaseFragment;
import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.places.PlacesDetail;

import java.util.Objects;

import butterknife.BindView;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback , GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapViewFragment.class.getSimpleName();

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
    // LOCATIONS
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

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeName + " : " + vicinity)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        .setTag(id);
            }
        } else {
            Log.e(TAG, "updateUiWithMarkers: no result found");
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: ");
        // call getLastLocation
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Display the current location on the device screen
                // Toast.makeText(getContext(), "Latitude is :" + location.getLatitude() + " and Longitude is: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                position = latitude + "," + longitude;

                // buildRetrofitRequest(latitude, longitude);
                cameraUpdate(latLng);

                viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(PlacesViewModel.class);
                viewModel.init(position);
                final Observer<PlacesDetail> observer = (PlacesDetail placesDetail) -> {
                    Log.e(TAG, "onChanged: " + placesDetail.getResults().size());
                    updateUiWithMarkers(placesDetail);
                };

                viewModel.getRepository().observe(this, observer);

                //executeRetrofitRequest(position, placesDetail.getNextPageToken());

            } else {
                Toast.makeText(getContext(), "Cannot get user current location at the moment", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: Marker:" + marker.getSnippet());

        String test = marker.getTitle();

        Log.e(TAG, "onMarkerClick: Clicked on : Id: " + marker.getId() + ", tag: " + marker.getTag() + ", result: " + test);

        Intent intent = new Intent(getContext(), RestaurantActivity.class);
        intent.putExtra("restaurantId", Objects.requireNonNull(marker.getTag()).toString());
        startActivity(intent);
        return true;
    }
}
