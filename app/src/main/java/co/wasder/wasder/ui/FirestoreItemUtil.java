package co.wasder.wasder.ui;

import android.net.Uri;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import co.wasder.wasder.data.FeedModel;

/** Created by Ahmed AlAskalany on 10/11/2017. Wasder AB */
@Keep
public class FirestoreItemUtil {

    public static final String TAG = "FirestoreItemUtil";

    private static final int MAX_IMAGE_NUM = 22;

    /** Create a random FeedModel POJO. */
    @NonNull
    private static FeedModel getRandom() {
        final FeedModel feedModel = new FeedModel();
        final Random random = new Random();

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

    private static String getRandomImageUrl(@NonNull final Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        final int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        // return String.format(Locale.getDefault(), RESTAURANT_URL_FMT, id);
        return "f07fafef-219a-4d10-90b5-bcabc1c82348";
    }

    private static double getRandomRating(@NonNull final Random random) {
        final double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    static void onAddItemsClicked() {
        // Get a reference to the events collection
        final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference events = mFirestore.collection("restaurants");

        for (int i = 0; i < 10; i++) {
            // Get a random events POJO
            final FeedModel event = getRandom();

            // Add a new document to the events collection
            events.add(event);
        }
    }
}
