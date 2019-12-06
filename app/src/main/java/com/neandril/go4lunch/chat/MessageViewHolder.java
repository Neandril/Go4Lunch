package com.neandril.go4lunch.chat;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.fragment_chat_item_root_view) RelativeLayout rootview;
    @BindView(R.id.fragment_chat_item_message_container_text_message_container_text_view) TextView mTextViewMessage;
    @BindView(R.id.fragment_chat_item_message_container_text_view_date) TextView mDateTextView;
    @BindView(R.id.fragment_chat_item_profile_container_profile_image) ImageView mProfileImageView;
    @BindView(R.id.fragment_chat_item_message_container_image_sent_cardview_image) ImageView mImageSentImageView;
    @BindView(R.id.fragment_chat_item_message_container_text_message_container) LinearLayout mMessageContainer;
    @BindView(R.id.fragment_chat_item_profile_container) LinearLayout mProfileContainer;

    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
    }

    public void updateWithMessage(Message message, String currentUserId) {

        // Check if current user is the sender
        Boolean isCurrentUser = message.getUserSender().getUser_id().equals(currentUserId);

        // Update message textview
        this.mTextViewMessage.setText(message.getMessage());
        this.mTextViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date textview
        if (message.getDateCreated() != null)
            this.mDateTextView.setText(this.convertDateToHour(message.getDateCreated()));

        // Profile picture
        if (message.getUserSender().getUser_profile_picture() != null) {
            Glide
                .with(mProfileImageView.getContext())
                .load(message.getUserSender().getUser_profile_picture())
                .transform(new RoundedCorners(50))
                .into(mProfileImageView);
        }

        // Image sent
        if (message.getUrlImage() != null) {
            Glide
                .with(mImageSentImageView.getContext())
                .load(message.getUrlImage())
                .transform(new RoundedCorners(50))
                .into(mImageSentImageView);
            this.mImageSentImageView.setVisibility(View.VISIBLE);
        } else {
            this.mImageSentImageView.setVisibility(View.GONE);
        }

        // Message bubble color
        ((GradientDrawable) mMessageContainer.getBackground()).setColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);

        // Alignement
        this.updateDesignDependingUser(isCurrentUser);

    }

    private void updateDesignDependingUser(Boolean isSender) {
        // Profile container
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.mProfileContainer.setLayoutParams(paramsLayoutHeader);

        // Message container
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.fragment_chat_item_profile_container);
        this.mMessageContainer.setLayoutParams(paramsLayoutContent);

        // Cardview item sent
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.fragment_chat_item_message_container_text_message_container);
        this.mImageSentImageView.setLayoutParams(paramsImageView);

        this.rootview.requestLayout();
    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }
}
