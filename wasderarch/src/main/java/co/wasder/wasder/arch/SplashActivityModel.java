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
 * Created by Ahmed AlAskalany on 11/7/2017.
 * Navigator
 */
class SplashActivityModel extends ViewModel implements FirebaseAuth.AuthStateListener,
        FirebaseAuth.IdTokenListener {

    private static final String TAG = "SplashActivityModel";
    private FirebaseAuth auth;
    private MutableLiveData<Boolean> signedIn = new MutableLiveData<>();
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private DatabaseReference databaseReference;

    public SplashActivityModel() {

        databaseReference = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);
        auth.addIdTokenListener(this);
        user.setValue(auth.getCurrentUser());
        if (user.getValue() != null) {
            signedIn.setValue(Boolean.TRUE);
        } else {
            signedIn.setValue(Boolean.FALSE);
        }
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
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
    }

    @Override
    public void onIdTokenChanged(@NonNull final FirebaseAuth firebaseAuth) {
        auth = firebaseAuth;
    }

    void startSignInAnonymous() {
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.postValue(task.getResult().getUser());
            }
        });
    }
}
