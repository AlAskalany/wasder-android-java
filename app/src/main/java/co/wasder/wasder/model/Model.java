package co.wasder.wasder.model;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Model {

    public static Event Event(String uId, String userName, String eventName, String city, String
            category, String photo, int price, int numRatings, double avgRating) {
        return new Event(uId, userName, eventName, city, category, photo, price, numRatings,
                avgRating);
    }

    public static Post Post(String name, String city, String category, String photo, int price,
                            int numRatings, double avgRating) {
        return new Post(name, city, category, photo, price, numRatings, avgRating);
    }

    public static Rating Rating(FirebaseUser user, double rating, String text) {
        return new Rating(user, rating, text);
    }
}
