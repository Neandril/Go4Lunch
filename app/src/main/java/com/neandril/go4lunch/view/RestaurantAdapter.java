package com.neandril.go4lunch.view;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.details.Detail;
import com.neandril.go4lunch.models.places.PlacesDetail;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private static final String TAG = "RestaurantAdapter";

    private List<PlacesDetail> mPlaceList;
    private RequestManager mGlide;

    public RestaurantAdapter(List<PlacesDetail> mPlaceList) {
        this.mPlaceList = mPlaceList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_listview_recyclerview_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.updateRestaurantsList(mPlaceList.get(0).getResults().get(position), mGlide);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mPlaceList.get(0).getResults().size());
        return mPlaceList.get(0).getResults().size();
    }
}
