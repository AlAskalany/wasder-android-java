package co.wasder.wasder.ui;

import android.support.v4.app.Fragment;

import co.wasder.wasder.ui.tabs.messages.MentionsTabFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MentionsTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MentionsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {

    protected String mTitle;

    public TabFragment() {
        // Required empty public constructor
    }


    public String getTitle() {
        return mTitle;
    }
}
