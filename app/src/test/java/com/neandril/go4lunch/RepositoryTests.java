package com.neandril.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.neandril.go4lunch.api.GoogleApiInterface;
import com.neandril.go4lunch.models.PlacesViewModel;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;
import com.neandril.go4lunch.utils.Repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import retrofit2.Call;
import retrofit2.mock.Calls;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class RepositoryTests {

    private GoogleApiInterface mockApiInterface;
    private Repository repository;

    @Before
    public void setUp() {
        mockApiInterface = Mockito.mock(GoogleApiInterface.class);
        repository = Repository.getInstance(mockApiInterface);
    }

    @After
    public void tearDown() {
        mockApiInterface = null;
        repository = null;
    }

    @Test
    public void liveDataTest() {
        double userLat = 48.442296;
        double userLng = -4.411621;
        String location = "" + userLat + userLng;

        PlacesViewModel placesViewModel = new PlacesViewModel();

        placesViewModel.getPlaces(location);

        LiveData<PlacesDetail> livedata = new MutableLiveData<>();
        when(placesViewModel.getPlacesLiveData()).thenReturn(livedata);

        placesViewModel.getPlaces(location);

        verify(placesViewModel).getPlaces(location);

    }

    @Test
    public void shouldReturn_listOfPlaces() {
        // Fake location
        double userLat = 48.442296;
        double userLng = -4.411621;
        String location = "" + userLat + userLng;

        // Mocks
        PlacesDetail response = new PlacesDetail();
        Call<PlacesDetail> call = Calls.response(response);
        Repository.PlacesCallback mockCallback = Mockito.mock(Repository.PlacesCallback.class);

        // When
        when(mockApiInterface.getNearbyPlaces(any())).thenReturn(call);

        // Then
        repository.getPlaces(location, mockCallback);

        // Verify
        verify(mockApiInterface).getNearbyPlaces(location);
        verify(mockCallback).onSuccess(response);
        verifyNoMoreInteractions(mockApiInterface);

    }

    @Test
    public void shouldReturn_details() {
        // Fake placeId
        String placeId = "ChIJR8D5w_clEUgRjmBlkT7QKmc";

        // Mocks
        Repository.RestaurantDetailsCallback mockCallback = Mockito.mock(Repository.RestaurantDetailsCallback.class);
        Detail response = new Detail();
        Call<Detail> call = Calls.response(response);

        // When
        when(mockApiInterface.getDetailInfos(any())).thenReturn(call);

        // Then
        repository.getRestaurant(placeId, mockCallback);

        // Verify
        verify(mockApiInterface).getDetailInfos(placeId);
        verify(mockCallback).onSuccess(response);
        verifyNoMoreInteractions(mockApiInterface);
    }
}
