package com.neandril.go4lunch.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neandril.go4lunch.api.GoogleApiCall;
import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.Predictions.Prediction;
import com.neandril.go4lunch.models.Predictions.PredictionsModel;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.details.PhotoDetails;
import com.neandril.go4lunch.models.places.PhotoPlaces;
import com.neandril.go4lunch.models.places.PlacesResult;
import com.neandril.go4lunch.utils.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PredictionsViewModel extends ViewModel {

    private Repository repository = Repository.getInstance(GoogleApiCall.retrofit.create(GoogleApiInterface.class));
    private MutableLiveData<List<PlacesResult>> predictionLiveData = new MutableLiveData<>();

    public LiveData<List<PlacesResult>> getPredictionLiveData() {
        return predictionLiveData;
    }

    public void getPredictions(String input, String location) {

        repository.getPredictions(input, location, new Repository.PredictionCallback() {
            @Override
            public void onSuccess(PredictionsModel model) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    List<PlacesResult> results = new ArrayList<>();

                    for (Prediction prediction : model.getPredictions()) {
                        PlacesResult placesResult = new PlacesResult();
                        Detail detail = repository.getRestaurantExecute(prediction.getPlaceId());
                        placesResult.setPlaceId(detail.getResult().getPlaceId());
                        placesResult.setName(detail.getResult().getName());
                        placesResult.setVicinity(detail.getResult().getVicinity());
                        placesResult.setGeometry(detail.getResult().getGeometry());
                        placesResult.setRating(detail.getResult().getRating());
                        placesResult.setOpeningHours(detail.getResult().getOpeningHours());

                        if (detail.getResult().getPhotos() != null) {
                            List<PhotoPlaces> photoPlaces = new ArrayList<>();
                            for (PhotoDetails photo : detail.getResult().getPhotos()) {
                                PhotoPlaces placesPhoto = new PhotoPlaces();
                                placesPhoto.setHeight(photo.getHeight());
                                placesPhoto.setWidth(photo.getWidth());
                                placesPhoto.setPhotoReference(photo.getPhotoReference());
                                photoPlaces.add(placesPhoto);
                            }
                            placesResult.setPhotos(photoPlaces);
                        }

                        results.add(placesResult);
                    }

                    predictionLiveData.postValue(results);
                });
            }

            @Override
            public void onError() {
                predictionLiveData.setValue(null);
            }
        });
    }
}
