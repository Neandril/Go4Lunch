package com.neandril.go4lunch.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.User;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, WorkmateViewHolder> {

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder workmateViewHolder, int i, @NonNull User user) {
        workmateViewHolder.updateWorkmateList(user);
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmates_recyclerview_item,
                parent, false);
        return new WorkmateViewHolder(view);
    }
}
