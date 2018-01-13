package co.wasder.wasder.ui.util;

import android.support.annotation.Keep;

import co.wasder.wasder.ui.dialogfragment.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialogfragment.FirestoreItemFilterDialogFragment;

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
