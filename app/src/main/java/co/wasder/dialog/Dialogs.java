package co.wasder.dialog;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Dialogs {

    public static PostsFilterDialogFragment PostsFilterDialogFragment() {
        return new PostsFilterDialogFragment();
    }

    @SuppressWarnings("unused")
    public static AddEventDialogFragment AddEventDialogFragment() {
        return new AddEventDialogFragment();
    }

    public static AddPostDialogFragment AddPostDialogFragment() {
        return new AddPostDialogFragment();
    }

}
