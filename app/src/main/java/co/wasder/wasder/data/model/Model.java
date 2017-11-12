package co.wasder.wasder.data.model;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import co.wasder.wasder.Util.FirebaseUtil;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class Model {

    @SuppressWarnings("unused")
    private static double rating;
    @SuppressWarnings("unused")
    private static String text;

    @SuppressWarnings("unused")
    public static FeedModel FirestoreItem() {
        return FirestoreItem("ASD", FirebaseUtil.getCurrentUser()
                .getPhotoUrl()
                .toString(), "AD", 2, 2, "Feed text");
    }

    public static FeedModel FirestoreItem(final String uId, final String profilePhotoUrl, final String photo,
                                          final int numRatings, final double avgRating, final String feedText) {
        return new FeedModel(uId, profilePhotoUrl, photo, numRatings, avgRating, feedText);
    }

    @SuppressWarnings("unused")
    public static Rating Rating() {
        return Rating(FirebaseUtil.getCurrentUser(), 2, "Asd");
    }

    @SuppressWarnings("unused")
    public static Rating Rating(@NonNull final FirebaseUser user, @SuppressWarnings("SameParameterValue") final
    double rating, @SuppressWarnings("SameParameterValue") final String text) {
        Model.rating = rating;
        Model.text = text;
        return new Rating(user, rating, text);
    }

    public static Event Event(final String uId, final String title, final String profilePhotoUrl, final String photo, final int
            numRatings,
                              final double avgRating, final String feedText) {
        return new Event(uId, title, profilePhotoUrl, photo, numRatings, avgRating, feedText);
    }
}
