package co.wasder.wasder;

import android.support.annotation.Keep;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Dialogs {

    public static FirestoreItemFilterDialogFragment PostsFilterDialogFragment() {
        return new FirestoreItemFilterDialogFragment();
    }

    public static AddFirestoreItemDialogFragment AddPostDialogFragment() {
        return new AddFirestoreItemDialogFragment();
    }

}
