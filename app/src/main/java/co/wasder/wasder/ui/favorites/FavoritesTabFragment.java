package co.wasder.wasder.ui.favorites;

import android.support.annotation.Keep;

import com.google.firebase.firestore.FirebaseFirestore;

import co.wasder.wasder.ui.navigation.BaseTabFragment;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public class FavoritesTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
}
