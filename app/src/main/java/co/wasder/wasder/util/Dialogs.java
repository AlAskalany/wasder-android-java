package co.wasder.wasder.util;

import android.support.annotation.Keep;

import co.wasder.wasder.dialogfragment.AddFirestoreItemDialogFragment;
import co.wasder.wasder.dialogfragment.FirestoreItemFilterDialogFragment;

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
