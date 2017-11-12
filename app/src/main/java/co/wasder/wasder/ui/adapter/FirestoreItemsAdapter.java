package co.wasder.wasder.ui.adapter;

import android.arch.lifecycle.LifecycleObserver;
import android.support.annotation.Keep;
import android.view.View;

import com.firebase.ui.firestore.ChangeEventListener;

import co.wasder.wasder.data.model.AbstractFirestoreItem;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public interface FirestoreItemsAdapter extends ChangeEventListener, LifecycleObserver {

    interface OnFirestoreItemSelected {

        void onFirestoreItemSelected(AbstractFirestoreItem event, View itemView);

    }
}
