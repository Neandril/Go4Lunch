package com.neandril.go4lunch.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

public class DetailViewModel extends ViewModel {

    private MutableLiveData<Detail> mutableLiveData;

    public void init(String placeid) {
        if (mutableLiveData != null) {
            return;
        }
        Repository repository = Repository.getInstance();
        mutableLiveData = repository.getRestaurant(placeid);
    }

    public LiveData<Detail> getRepository() {
        return mutableLiveData;
    }
}
