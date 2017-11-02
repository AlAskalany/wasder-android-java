package co.wasder.wasder.dialog;

import android.support.annotation.Keep;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Dialogs {

    public static FIrestoreItemFilterDialogFragment PostsFilterDialogFragment() {
        return new FIrestoreItemFilterDialogFragment();
    }

    public static AddFirestoreItemDialogFragment AddPostDialogFragment() {
        return new AddFirestoreItemDialogFragment();
    }

}
