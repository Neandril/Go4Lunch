package com.neandril.go4lunch.api;

import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiInterface {

    //TODO: Manage Api Key via gradle file
    @GET("nearbysearch/json?sensor=true&radius=5000&type=restaurant&key=AIzaSyBkjaxczMoqyJzCQnRRIJgeJoubLGdSEK0")
    Call<PlacesDetail> getNearbyPlaces(
            @Query("location") String location);

    @GET("details/json?&key=AIzaSyBkjaxczMoqyJzCQnRRIJgeJoubLGdSEK0")
    Call<Detail> getDetailInfos(
            @Query("placeid") String placeid);
}
