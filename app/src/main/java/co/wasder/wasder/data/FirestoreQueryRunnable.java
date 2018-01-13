package co.wasder.wasder.data;

import android.support.annotation.Keep;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/** Created by Ahmed AlAskalany on 10/18/2017. Navigator */
@Keep
@SuppressWarnings("unused")
class FirestoreQueryRunnable implements Runnable {

    public final String collectionName;

    @SuppressWarnings("unused")
    public Query query;

    @SuppressWarnings("unused")
    public FirestoreQueryRunnable(final Query query, final String collectionName) {
        this.query = query;
        this.collectionName = collectionName;
    }

    @Override
    public void run() {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        query = firestore.collection(collectionName);
    }
}
