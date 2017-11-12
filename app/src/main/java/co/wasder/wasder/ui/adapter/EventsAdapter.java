package co.wasder.wasder.ui.adapter;

import android.arch.lifecycle.LifecycleObserver;
import android.support.annotation.Keep;
import android.view.View;

import com.firebase.ui.firestore.ChangeEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public interface EventsAdapter extends ChangeEventListener, LifecycleObserver {

    interface OnEventSelected {

        void onEventSelected(DocumentSnapshot event, View itemView);

    }
}
