package com.neandril.go4lunch.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.Predictions.Prediction;
import com.neandril.go4lunch.models.Predictions.PredictionsModel;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private static Repository repository;
    private GoogleApiInterface googleApiInterface;

    public static Repository getInstance(GoogleApiInterface apiInterface) {
        if (repository == null) {
            repository = new Repository(apiInterface);
        }
        return repository;
    }

    private Repository(GoogleApiInterface apiInterface) {
        googleApiInterface = apiInterface;
    }


    // ***************************
    // REQUESTS
    // ***************************

    // Nearby request
    public void getPlaces(String location, PlacesCallback callback) {
        googleApiInterface.getNearbyPlaces(location).enqueue(new Callback<PlacesDetail>() {
            @Override
            public void onResponse(@NonNull Call<PlacesDetail> call, @NonNull Response<PlacesDetail> response) {
                if (response.isSuccessful()) {
                    PlacesDetail body = response.body();
                    callback.onSuccess(body);
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlacesDetail> call, @NonNull Throwable t) {
                callback.onError();
            }
        });
    }

    // Place detail request
    public void getRestaurant(String placeId, RestaurantDetailsCallback callback) {

        googleApiInterface.getDetailInfos(placeId).enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {
                if (response.isSuccessful()) {
                    Detail body = response.body();
                    callback.onSuccess(body);
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                callback.onError();
            }
        });
    }

    // Place detail request
    public Detail getRestaurantExecute(String placeId) {

        try {
            Response<Detail> detailResponse = googleApiInterface.getDetailInfos(placeId).execute();
            if (detailResponse.isSuccessful()) {
                return detailResponse.body();
            } else {
                return null;
            }

        } catch (IOException e) {
            return null;
        }
    }

    // Predictions request
    public void getPredictions(String input, String location, PredictionCallback callback) {

        googleApiInterface.getPredictions(input, location).enqueue(new Callback<PredictionsModel>() {
            @Override
            public void onResponse(@NonNull Call<PredictionsModel> call, @NonNull Response<PredictionsModel> response) {
                if (response.isSuccessful()) {
                    PredictionsModel body = response.body();
                    List<Prediction> predictions = Objects.requireNonNull(body).getPredictions();

                    for (Prediction prediction : predictions) {
                        getRestaurant(prediction.getPlaceId(), new RestaurantDetailsCallback() {
                            @Override
                            public void onSuccess(Detail detail) {
                                Log.d("getPrediction", "onSuccess: " + detail.getResult().getName());
                            }

                            @Override
                            public void onError() { }
                        });
                    }
                    callback.onSuccess(body);

                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PredictionsModel> call, @NonNull Throwable t) {
                callback.onError();
            }
        });
    }

    // ***************************
    // CALLBACKS
    // ***************************
    public interface PredictionCallback {
        void onSuccess(PredictionsModel model);
        void onError();
    }

    public interface RestaurantDetailsCallback {
        void onSuccess(Detail detail);
        void onError();
    }

    public interface PlacesCallback {
        void onSuccess(PlacesDetail placesDetail);
        void onError();
    }

}
