package com.wasder.wasder.jobservices;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */

public class FirestoreQueryRunnable implements Runnable {

    private Query query;
    private String collectionName;

    public FirestoreQueryRunnable(Query query, String collectionName) {
        this.query = query;
        this.collectionName = collectionName;
    }

    @Override
    public void run() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        query = firestore.collection(collectionName);
    }
}
