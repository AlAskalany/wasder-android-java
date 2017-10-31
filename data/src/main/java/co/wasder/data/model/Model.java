package co.wasder.data.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Model {

    @SuppressWarnings("unused")
    private static double rating;
    @SuppressWarnings("unused")
    private static String text;

    @SuppressWarnings("unused")
    public static FirestoreItem FirestoreItem() {
        return FirestoreItem("Anonymous", "Cairo", "Brunch", FirebaseAuth.getInstance()
                .getCurrentUser()
                .getPhotoUrl()
                .toString(), "AD", 2, 2, 2, "Feed text");
    }

    public static FirestoreItem FirestoreItem(String name, String city, String category, String profilePhotoUrl, String photo, int price, int numRatings, double avgRating, String feedText) {
        return new FirestoreItem(name, city, category, profilePhotoUrl, photo, price, numRatings,
                avgRating, feedText);
    }

    @SuppressWarnings("unused")
    public static Rating Rating() {
        return Rating(FirebaseAuth.getInstance().getCurrentUser(), 2, "Asd");
    }

    @SuppressWarnings("unused")
    private static Rating Rating(FirebaseUser user, @SuppressWarnings("SameParameterValue")
            double rating, @SuppressWarnings("SameParameterValue") String text) {
        Model.rating = rating;
        Model.text = text;
        return new Rating(user, rating, text);
    }
}
