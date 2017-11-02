package co.wasder.wasder.Util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Keep;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import co.wasder.wasder.R;
import co.wasder.wasder.model.FirestoreItem;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */
@Keep
public class FirestoreItemUtil {

    @SuppressWarnings("unused")
    public static final String TAG = "FirestoreItemUtil";

    @SuppressWarnings("unused")
    public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60, TimeUnit
            .SECONDS, new LinkedBlockingQueue<Runnable>());

    @SuppressWarnings("unused")
    public static final String RESTAURANT_URL_FMT = "https://storage.googleapis" + "" + "" + ""
            + ".com/firestorequickstarts.appspot.com/food_%d.png";

    public static final int MAX_IMAGE_NUM = 22;

    public static final String[] NAME_FIRST_WORDS = {"John", "David", "Buz", "Laura", "Mike",
            "Sam's", "World Famous", "Google", "The Best",};

    public static final String[] NAME_SECOND_WORDS = {"Norman", "Kelley", "Lauren", "Al' " +
            "Mark", "Mendley", "Gamer", "Alex",};


    /**
     * Create a random FirestoreItem POJO.
     */
    public static FirestoreItem getRandom(Context context) {
        FirestoreItem firestoreItem = new FirestoreItem();
        Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories_items);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uId = user.getUid();
            String name = user.getDisplayName();
            firestoreItem.setUid(uId);
            firestoreItem.setName(name);
            Uri profilePhotoUri = user.getPhotoUrl();
            String profilePhotoUrl = null;
            if (profilePhotoUri != null) {
                profilePhotoUrl = profilePhotoUri.toString();
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    firestoreItem.setProfilePhoto(profilePhotoUrl);
                }
            }
        }

        firestoreItem.setPhoto(getRandomImageUrl(random));
        firestoreItem.setAvgRating(getRandomRating(random));
        firestoreItem.setNumRatings(random.nextInt(20));

        return firestoreItem;
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
            FirestoreItem event = getRandom(context);

            // Add a new document to the events collection
            events.add(event);
        }
    }
}
