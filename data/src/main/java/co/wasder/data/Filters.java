package co.wasder.data;

import android.support.annotation.Keep;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Filters {

    public static FirestoreItemFilters PostsFilters() {
        return new FirestoreItemFilters();
    }
}
