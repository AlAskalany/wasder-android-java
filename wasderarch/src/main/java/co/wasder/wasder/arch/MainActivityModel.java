package co.wasder.wasder.arch;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ahmed AlAskalany on 11/8/2017.
 * Navigator
 */

class MainActivityModel extends ViewModel implements FirebaseAuth.AuthStateListener, FirebaseAuth
        .IdTokenListener {

    private static final String TAG = "SplashActivityModel";
    private FirebaseAuth auth;
    private MutableLiveData<Boolean> signedIn = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private DatabaseReference databaseReference;

    public MainActivityModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);
        auth.addIdTokenListener(this);
        user.setValue(auth.getCurrentUser());
    }

    LiveData<FirebaseUser> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
            user.setValue(auth.getCurrentUser());
        }
        return user;
    }

    LiveData<Boolean> isSignedIn() {
        if (signedIn == null) {
            signedIn = new MutableLiveData<>();
            signedIn.setValue(user.getValue() != null ? Boolean.TRUE : Boolean.FALSE);
        }
        return signedIn;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
    }

    @Override
    public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
    }
}
