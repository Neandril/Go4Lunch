package com.neandril.go4lunch.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

public class PlacesViewModel extends ViewModel {

    private MutableLiveData<PlacesDetail> mutableLiveData;

    Repository repository = Repository.getInstance();

    public void init(String location) {
        if (mutableLiveData != null) {
            return;
        }

        mutableLiveData = repository.getPlaces(location);
    }

    //TODO: rename
    public LiveData<PlacesDetail> getRepository() {
        return mutableLiveData;
    }
}
