package com.neandril.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import retrofit2.mock.Calls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LiveDataTests {

    private PlacesViewModel placesViewModel;
    private Repository mockRepository;

    @Before
    public void init() {
        mockRepository = Mockito.mock(Repository.class);
        placesViewModel = new PlacesViewModel();
    }

    @Test
    public void firstTest() {
        double userLat = 48.442296;
        double userLng = -4.411621;
        String location = "" + userLat + "," + userLng;

        placesViewModel.getPlaces(location);


        MutableLiveData<PlacesDetail> placesDetailLiveData = new MutableLiveData<>();
        when(placesViewModel.getPlacesLiveData()).thenReturn(placesDetailLiveData);

        Repository.PlacesCallback mockCallback = Mockito.mock(Repository.PlacesCallback.class);
        verify(mockRepository).getPlaces(location, mockCallback);
        verifyNoMoreInteractions(mockRepository);
    }

}
