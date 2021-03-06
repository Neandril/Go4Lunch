package com.neandril.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.api.GoogleApiCall;
import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.models.Repository;

public class PlacesViewModel extends ViewModel {

    private Repository repository = Repository.getInstance(GoogleApiCall.retrofit.create(GoogleApiInterface.class));
    private MutableLiveData<PlacesDetail> placesDetailLiveData = new MutableLiveData<>();

    public LiveData<PlacesDetail> getPlacesLiveData() {
        return placesDetailLiveData;
    }

    public void getPlaces(String location) {
        Log.d("DetailsViewModel", "getPredictions: input: " + location);
        repository.getPlaces(location, new Repository.PlacesCallback() {
            @Override
            public void onSuccess(PlacesDetail model) {
                placesDetailLiveData.setValue(model);
            }

            @Override
            public void onError() {
                placesDetailLiveData.setValue(null);
            }
        });
    }
}
