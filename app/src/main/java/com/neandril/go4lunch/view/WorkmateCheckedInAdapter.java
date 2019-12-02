package com.neandril.go4lunch.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.controllers.activities.RestaurantActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateCheckedInAdapter extends FirestoreRecyclerAdapter<User, WorkmateCheckedInAdapter.WorkmateCheckedInViewHolder> {

    public WorkmateCheckedInAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateCheckedInViewHolder workmateCheckedInViewHolder, int i, @NonNull User user) {
        workmateCheckedInViewHolder.updateList(user);
    }

    @NonNull
    @Override
    public WorkmateCheckedInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_workmates_recyclerview_item, parent, false);
        return new WorkmateCheckedInViewHolder(view);
    }

    class WorkmateCheckedInViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_workmate_name) TextView tv_workmate_name;
        @BindView(R.id.iv_workmate_picture) ImageView iv_workmate_picture;

        public WorkmateCheckedInViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void updateList(User user) {
            if (!user.getRestaurantName().equals("")) {
                this.tv_workmate_name.setText(itemView.getContext().getString(
                        R.string.user_is_joining,
                        user.getUser_name()));
            }

            if (user.getUser_profile_picture() != null) {
                Glide.with(iv_workmate_picture.getContext())
                        .load(user.getUser_profile_picture())
                        .transform(new RoundedCorners(50))
                        .into(iv_workmate_picture);
            } else {
                Glide.with(iv_workmate_picture.getContext()).load(R.mipmap.ic_launcher).into(iv_workmate_picture);
            }

        }
    }
}
