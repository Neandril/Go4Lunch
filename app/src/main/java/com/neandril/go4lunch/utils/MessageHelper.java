package com.neandril.go4lunch.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.neandril.go4lunch.models.Message;
import com.neandril.go4lunch.models.User;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(){
        return ChatHelper.getChatCollection().orderBy("dateCreated").limit(50);
    }

    // --- CREATE ---
    public static void createMessageForChat(String txtMessage, User sender) {
        Message message = new Message(txtMessage, sender);

        ChatHelper.getChatCollection()
                .document("1")
                .set(message);
    }
}
