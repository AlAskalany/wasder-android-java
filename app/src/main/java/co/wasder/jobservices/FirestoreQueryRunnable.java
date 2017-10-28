package co.wasder.jobservices;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */

@SuppressWarnings("unused")
class FirestoreQueryRunnable implements Runnable {

    private final String collectionName;
    @SuppressWarnings("unused")
    private Query query;

    @SuppressWarnings("unused")
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
