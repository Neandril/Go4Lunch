package com.neandril.go4lunch.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.User;

import butterknife.BindView;
import butterknife.ButterKnife;

class WorkmateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_workmate_name) TextView tv_workmate_name;
    @BindView(R.id.iv_workmate_picture) ImageView iv_workmate_picture;

    WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void updateWorkmateList(User user) {

        if (!user.getRestaurantName().equals("")) {
            this.tv_workmate_name.setText(itemView.getContext().getString(
                    R.string.user_is_eating_here,
                    user.getUser_name(),
                    user.getRestaurantName()));
        } else {
            this.tv_workmate_name.setText(itemView.getContext().getString(
                    R.string.user_hasnt_decided_yet,
                    user.getUser_name()));
            this.tv_workmate_name.setAlpha((float) 0.5);
            iv_workmate_picture.setAlpha((float) 0.5);
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
