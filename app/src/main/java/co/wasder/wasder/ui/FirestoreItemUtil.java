package co.wasder.wasder.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
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
import co.wasder.wasder.data.FeedModel;

/** Created by Ahmed AlAskalany on 10/11/2017. Wasder AB */
@Keep
public class FirestoreItemUtil {

    @SuppressWarnings("unused")
    public static final String TAG = "FirestoreItemUtil";

    @SuppressWarnings("unused")
    public static final ThreadPoolExecutor EXECUTOR =
            new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @SuppressWarnings("unused")
    public static final String RESTAURANT_URL_FMT =
            "https://storage.googleapis"
                    + ""
                    + ""
                    + ""
                    + ".com/firestorequickstarts.appspot.com/food_%d.png";

    public static final int MAX_IMAGE_NUM = 22;

    static final String[] NAME_FIRST_WORDS = {
        "John", "David", "Buz", "Laura", "Mike", "Sam's", "World Famous", "Google", "The Best",
    };

    static final String[] NAME_SECOND_WORDS = {
        "Norman", "Kelley", "Lauren", "Al' " + "Mark", "Mendley", "Gamer", "Alex",
    };

    /** Create a random FeedModel POJO. */
    @NonNull
    public static FeedModel getRandom(@NonNull final Context context) {
        final FeedModel feedModel = new FeedModel();
        final Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories_items);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        final int[] prices = new int[] {1, 2, 3};

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uId = user.getUid();
            final String name = user.getDisplayName();
            feedModel.setUid(uId);
            feedModel.setName(name);
            final Uri profilePhotoUri = user.getPhotoUrl();
            String profilePhotoUrl = null;
            if (profilePhotoUri != null) {
                profilePhotoUrl = profilePhotoUri.toString();
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    feedModel.setProfilePhoto(profilePhotoUrl);
                }
            }
        }

        feedModel.setPhoto(getRandomImageUrl(random));
        feedModel.setAvgRating(getRandomRating(random));
        feedModel.setNumRatings(random.nextInt(20));

        return feedModel;
    }

    /** Get a random image. */
    @SuppressWarnings("SameReturnValue")
    public static String getRandomImageUrl(@NonNull final Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        @SuppressWarnings("unused")
        final int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        // return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id);
        return "f07fafef-219a-4d10-90b5-bcabc1c82348";
    }

    @SuppressWarnings("WeakerAccess")
    public static double getRandomRating(@NonNull final Random random) {
        final double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getRandomName(@NonNull final Random random) {
        return getRandomString(NAME_FIRST_WORDS, random)
                + " "
                + getRandomString(NAME_SECOND_WORDS, random);
    }

    @SuppressWarnings("WeakerAccess")
    public static String getRandomString(
            @NonNull final String[] array, @NonNull final Random random) {
        final int ind = random.nextInt(array.length);
        return array[ind];
    }

    @SuppressWarnings("WeakerAccess")
    public static int getRandomInt(@NonNull final int[] array, @NonNull final Random random) {
        final int ind = random.nextInt(array.length);
        return array[ind];
    }

    public static void onAddItemsClicked(@NonNull final Context context) {
        // Get a reference to the events collection
        final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference events = mFirestore.collection("restaurants");

        for (int i = 0; i < 10; i++) {
            // Get a random events POJO
            final FeedModel event = getRandom(context);

            // Add a new document to the events collection
            events.add(event);
        }
    }
}
