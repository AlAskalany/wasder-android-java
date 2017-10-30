package co.wasder.wasder.ui;

import android.support.v4.app.Fragment;

import co.wasder.wasder.ui.messages.tabs.MentionsTabFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MentionsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public interface TabFragment {

    String getTitle();
}
