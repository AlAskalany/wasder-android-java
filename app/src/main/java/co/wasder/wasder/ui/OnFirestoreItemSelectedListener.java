package co.wasder.wasder.ui;

import android.arch.lifecycle.LifecycleObserver;
import android.support.annotation.Keep;
import android.view.View;

import com.firebase.ui.firestore.ChangeEventListener;

import co.wasder.wasder.data.base.BaseModel;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public interface OnFirestoreItemSelectedListener extends ChangeEventListener, LifecycleObserver {

    void onFirestoreItemSelected(BaseModel event, View itemView);
}
