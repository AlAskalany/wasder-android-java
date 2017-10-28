package co.wasder.wasder.model;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Model {

    private static double rating;
    private static String text;

    @SuppressWarnings("unused")
    @NonNull
    public static Event Event() {
        return Event("ASD", "Anonymous", "Event", "Cairo", "Brunch", "AD", 2, 2, 2);
    }

    @NonNull
    public static Event Event(String uId, String userName, String eventName, String city, String category, String photo, int price, int numRatings, double avgRating) {
        return new Event(uId, userName, eventName, city, category, photo, price, numRatings, avgRating);
    }

    @SuppressWarnings("unused")
    public static Post Post() {
        return Post("Anonymous", "Cairo", "Brunch", "AD", 2, 2, 2);
    }

    public static Post Post(String name, String city, String category, String photo, int price,
                            int numRatings, double avgRating) {
        return new Post(name, city, category, photo, price, numRatings, avgRating);
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
