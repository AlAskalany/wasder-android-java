package co.wasder.wasder.ui;

import android.support.annotation.Keep;

/** Created by Ahmed AlAskalany on 10/13/2017. Wasder AB */
@Keep
public class Dialogs {

    public static FirestoreItemFilterDialogFragment PostsFilterDialogFragment() {
        return new FirestoreItemFilterDialogFragment();
    }

    public static addFirestoreItemDialogFragment AddPostDialogFragment() {
        return new addFirestoreItemDialogFragment();
    }
}
