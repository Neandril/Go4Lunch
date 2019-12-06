package com.neandril.go4lunch.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.util.Listener;
import com.neandril.go4lunch.R;
import com.neandril.go4lunch.models.Message;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageViewHolder> {

    public interface listener {
        void onDataChanged();
    }

    private final String idCurrentUser;
    private Listener callback;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, String idCurrentUser) {
        super(options);
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {
        messageViewHolder.updateWithMessage(message, this.idCurrentUser);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_recyclerview_item, parent, false));
    }

}
