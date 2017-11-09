package co.wasder.wasder.network.service;

import android.support.annotation.Keep;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "MyFirebaseIIDService";
    public static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        final String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
        Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }

    private void sendRegistrationToServer(final String token) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        String uId = null;
        if (user != null) {
            uId = user.getUid();
            final DatabaseReference database = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uId)
                    .child("asd")
                    .push()
                    .child(token);
        }
    }
}
