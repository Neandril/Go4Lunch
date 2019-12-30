package com.neandril.go4lunch.api;

import com.neandril.go4lunch.BuildConfig;
import com.neandril.go4lunch.models.Predictions.Prediction;
import com.neandril.go4lunch.models.Predictions.PredictionsModel;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.details.DetailsResult;
import com.neandril.go4lunch.models.places.PlacesDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiInterface {

    String key = BuildConfig.ApiKey;

    @GET("nearbysearch/json?sensor=true&radius=5000&type=restaurant&key=" + key)
    Call<PlacesDetail> getNearbyPlaces(
            @Query("location") String location);

    @GET("details/json?&key=" + key)
    Call<Detail> getDetailInfos(
            @Query("placeid") String placeid);

    @GET("autocomplete/json?radius=5000&type=establishment&key=" + key)
    Call<PredictionsModel> getPredictions (
            @Query("input") String input,
            @Query("location") String location);
}
