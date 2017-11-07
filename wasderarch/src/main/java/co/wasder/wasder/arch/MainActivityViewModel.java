package co.wasder.wasder.arch;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Ahmed AlAskalany on 11/7/2017.
 * Navigator
 */

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> user;

    public LiveData<FirebaseUser> getUser() {
        if (user == null) {
            user = new MutableLiveData<>();
            loadUser();
        }
        return user;
    }

    private void loadUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user.setValue(auth.getCurrentUser());
    }
}
