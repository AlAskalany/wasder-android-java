package co.wasder.wasder.arch.data.source.firestoredb;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Map;

import co.wasder.wasder.arch.data.model.DataModel;

/**
 * Created by Ahmed AlAskalany on 11/8/2017.
 * Navigator
 */

public class FirestoreHelper {

    /**
     *
     */
    public void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    /**
     * @param db  FirebaseFirestore Database {@link FirebaseFirestore}
     * @param col Collection
     * @param doc Document
     * @return Document Reference {@link DocumentReference}
     */
    public DocumentReference docReference(FirebaseFirestore db, String col, String doc) {
        return db.collection(col).document(col);
    }

    /**
     * @param db  FirebaseFirestore Database {@link FirebaseFirestore}
     * @param col Collection
     * @return Collection Reference {@link CollectionReference}
     */
    public CollectionReference collectionReference(FirebaseFirestore db, String col) {
        return db.collection(col);
    }

    /**
     * @param db      FirebaseFirestore Database {@link FirebaseFirestore}
     * @param rootCol Root collection
     * @param rootDoc Root document
     * @param col     Collection
     * @param doc     Document
     * @return Document Reference {@link DocumentReference}
     */
    public DocumentReference subcollectionReference(FirebaseFirestore db, String rootCol, String
            rootDoc, String col, String doc) {
        return db.collection(rootCol).document(rootDoc).collection(col).document(doc);
    }

    /**
     * @param db        FirebaseFirestore Database {@link FirebaseFirestore}
     * @param colAndDoc Collection and Document "collection/document"
     * @return Document Reference {@link DocumentReference}
     */
    public DocumentReference docReferenceAlternate(FirebaseFirestore db, String colAndDoc) {
        return db.document(colAndDoc);
    }

    /**
     * @param db   FirebaseFirestore Database {@link FirebaseFirestore}
     * @param col  Collection
     * @param doc  Document name or id
     * @param data HashMap {@link Map}
     */
    public void setDocument(FirebaseFirestore db, String col, String doc, Map<String, Object>
            data, OnCompleteListener<Void> onCompleteListener) {
        String TAG = "setDocument";
        db.collection(col).document(doc).set(data).addOnCompleteListener(onCompleteListener);
    }


    /**
     * @param db               FirebaseFirestore Database {@link FirebaseFirestore}
     * @param col              Collection
     * @param doc              Document
     * @param model            DataModel {@link DataModel}
     * @param completeListener {@link OnCompleteListener}
     */
    public void addCustomClass(FirebaseFirestore db, String col, String doc, DataModel model,
                               OnCompleteListener<Void> completeListener) {
        db.collection(col).document(doc).set(model).addOnCompleteListener(completeListener);
    }

    /**
     * @param db               FirebaseFirestore Database {@link FirebaseFirestore}
     * @param col              Collection
     * @param data             data HashMap {@link Map}
     * @param completeListener completeListener {@link OnCompleteListener}
     */
    public void addDocument(FirebaseFirestore db, String col, Map<String, Object> data,
                            OnCompleteListener<DocumentReference> completeListener) {
        db.collection(col).add(data).addOnCompleteListener(completeListener);
    }

    public DocumentReference newDocument(FirebaseFirestore db, String col, Map<String, Object>
            data) {
        // [START new_document]

        return db.collection(col).document();
    }

    public class NewDocument {

        private DocumentReference ref;

        public NewDocument(FirebaseFirestore db, String col) {
            this.ref = db.collection(col).document();
        }

        public void invoke(Map<String, Object> data) {
            ref.set(data);
        }
    }
}
