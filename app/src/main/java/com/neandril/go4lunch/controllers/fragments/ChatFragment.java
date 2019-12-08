package com.neandril.go4lunch.controllers.fragments;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.Query;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.chat.MessageAdapter;
import com.neandril.go4lunch.controllers.base.BaseFragment;
import com.neandril.go4lunch.models.Message;
import com.neandril.go4lunch.models.User;
import com.neandril.go4lunch.utils.MessageHelper;
import com.neandril.go4lunch.utils.UserHelper;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatFragment extends BaseFragment implements MessageAdapter.listener {

    @BindView(R.id.chat_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.textView_recyclerView_empty) TextView mTextViewEmpty;
    @BindView(R.id.chat_input_editText) TextInputEditText mInputEditText;
    @BindView(R.id.imageView_preview) ImageView mPreview;


    private MessageAdapter messageAdapter;
    private User currentUser;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void configureFragment() {
        this.configureRecyclerView();
    }

    private void configureRecyclerView() {
        // Instanciate a new Firestore query object
        Query query = MessageHelper.getAllMessageForChat();

        // Configure Firestore recycler options
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();

        this.messageAdapter = new MessageAdapter(options, this.getCurrentUser().getUid());
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mRecyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(this.messageAdapter);
    }

    @Override
    public void onDataChanged() {
        mTextViewEmpty.setVisibility(this.messageAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btn_chat_message_send)
    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(mInputEditText.getText()) && currentUser != null) {
            MessageHelper.createMessageForChat(Objects.requireNonNull(mInputEditText.getText()).toString(), currentUser);
            this.mInputEditText.setText("");
        }

    }

    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
           currentUser = documentSnapshot.toObject(User.class);
        });
    }
}
