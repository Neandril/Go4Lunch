package com.neandril.go4lunch.utils;

import com.google.firebase.firestore.Query;

public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---

    public static Query getAllMessageForChat(){
        return ChatHelper.getChatCollection().orderBy("dateCreated").limit(50);
    }
}
