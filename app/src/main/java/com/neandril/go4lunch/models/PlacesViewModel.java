package com.neandril.go4lunch.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

public class PlacesViewModel extends ViewModel {

    private MutableLiveData<PlacesDetail> mutableLiveData;

    public void init(String location) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getPlaces(location);
    }

    public LiveData<PlacesDetail> getRepository() {
        return mutableLiveData;
    }
}
