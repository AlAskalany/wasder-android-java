package co.wasder.wasder.ui;

import android.support.annotation.Keep;
import android.support.v4.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.ui.messages.tabs.MentionsTabFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MentionsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@Keep
public interface TabFragment {

    public FirebaseFirestore getFirestore();

    public void setFirestore(FirebaseFirestore firestore);

    public Query getQuery();

    public void setQuery(Query query);

    public String getTitle();
}
