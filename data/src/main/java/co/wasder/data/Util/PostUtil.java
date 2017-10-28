package co.wasder.data.Util;

import android.content.Context;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.wasder.data.R;
import co.wasder.data.model.Post;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class PostUtil {

    @SuppressWarnings("unused")
    private static final String TAG = "PostUtil";

    @SuppressWarnings("unused")
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @SuppressWarnings("unused")
    private static final String RESTAURANT_URL_FMT = "https://storage.googleapis" + "" + "" +
            ".com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {"John", "David", "Buz", "Laura", "Mike",
            "Sam's", "World Famous", "Google", "The Best",};

    private static final String[] NAME_SECOND_WORDS = {"Norman", "Kelley", "Lauren", "Al' " +
            "Mark", "Mendley", "Gamer", "Alex",};


    /**
     * Create a random Post POJO.
     */
    public static Post getRandom(Context context) {
        Post post = new Post();
        Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories_posts);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        post.setName(getRandomName(random));
        post.setCity(getRandomString(cities, random));
        post.setCategory(getRandomString(categories, random));
        post.setPhoto(getRandomImageUrl(random));
        post.setPrice(getRandomInt(prices, random));
        post.setAvgRating(getRandomRating(random));
        post.setNumRatings(random.nextInt(20));

        return post;
    }


    /**
     * Get a random image.
     */
    @SuppressWarnings("SameReturnValue")
    public static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        @SuppressWarnings("unused") int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        //return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id);
        return "f07fafef-219a-4d10-90b5-bcabc1c82348";
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(Post post) {
        return getPriceString(post.getPrice());
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " " + getRandomString
                (NAME_SECOND_WORDS, random);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    @SuppressWarnings("WeakerAccess")
    public static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    public static void onAddItemsClicked(Context context) {
        // Get a reference to the events collection
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference events = mFirestore.collection("restaurants");

        for (int i = 0; i < 10; i++) {
            // Get a random events POJO
            Post event = getRandom(context);

            // Add a new document to the events collection
            events.add(event);
        }
    }
}
