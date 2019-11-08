package com.neandril.go4lunch.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.neandril.go4lunch.api.GoogleApiCall;
import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static Repository repository;
    private GoogleApiInterface googleApiInterface;

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    private Repository() {
        googleApiInterface = GoogleApiCall.retrofit.create(GoogleApiInterface.class);
    }

    // Nearby request
    public MutableLiveData<PlacesDetail> getPlaces(String location) {
        MutableLiveData<PlacesDetail> datas = new MutableLiveData<>();
        googleApiInterface.getNearbyPlaces(location).enqueue(new Callback<PlacesDetail>() {
            @Override
            public void onResponse(Call<PlacesDetail> call, Response<PlacesDetail> response) {
                if (response.isSuccessful()) {
                    datas.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<PlacesDetail> call, Throwable t) {
                datas.setValue(null);
            }
        });

        return datas;
    }

    // Place detail request
    public MutableLiveData<Detail> getRestaurant(String placeId) {
        MutableLiveData<Detail> datas = new MutableLiveData<>();
        googleApiInterface.getDetailInfos(placeId).enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.isSuccessful()) {
                    Log.e("getRestaurant", "onResponse: " + response.body());
                    datas.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {

            }
        });

        return datas;
    }
}
