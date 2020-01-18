package com.neandril.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.api.GoogleApiCall;
import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.Repository;

public class DetailViewModel extends ViewModel {

    private Repository repository = Repository.getInstance(GoogleApiCall.retrofit.create(GoogleApiInterface.class));
    private MutableLiveData<Detail> detailLiveData = new MutableLiveData<>();

    public LiveData<Detail> getDetailsLiveData() {
        return detailLiveData;
    }

    public void getDetails(String placeId) {
        Log.d("DetailsViewModel", "getPredictions: input: " + placeId);
        repository.getRestaurant(placeId, new Repository.RestaurantDetailsCallback() {
            @Override
            public void onSuccess(Detail model) {
                detailLiveData.setValue(model);
            }

            @Override
            public void onError() {
                detailLiveData.setValue(null);
            }
        });
    }
}
