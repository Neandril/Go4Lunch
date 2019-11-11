package com.neandril.go4lunch.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

public class DetailViewModel extends ViewModel {

    private final Repository repository;
    private MutableLiveData<Detail> mutableLiveData;

    public DetailViewModel() {
        repository = Repository.getInstance();
    }

    public void init(String placeid) {
        if (mutableLiveData != null) {
            return;
        }

        mutableLiveData = repository.getRestaurant(placeid);
    }

    public LiveData<Detail> getRepository() {
        return mutableLiveData;
    }
}
