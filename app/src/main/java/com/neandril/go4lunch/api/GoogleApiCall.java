package com.neandril.go4lunch.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleApiCall {

    private static String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
