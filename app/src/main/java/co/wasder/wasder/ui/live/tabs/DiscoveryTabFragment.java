package co.wasder.wasder.ui.live.tabs;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.adapter.Adapters;
import co.wasder.wasder.adapter.EventsAdapter;
import co.wasder.wasder.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.dialog.Dialogs;
import co.wasder.wasder.dialog.FIrestoreItemFilterDialogFragment;
import co.wasder.wasder.ui.OnFragmentInteractionListener;
import co.wasder.wasder.ui.TabFragment;
import co.wasder.wasder.viewmodel.TabFragmentViewModel;
import co.wasder.wasder.views.FirestoreCollections;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */

public class DiscoveryTabFragment extends Fragment implements TabFragment, LifecycleOwner {

    private static final long LIMIT = 50;
    private static final String TAG = "TabFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private FIrestoreItemFilterDialogFragment mFilterDialog;
    private AddFirestoreItemDialogFragment mAddPostDialog;
    private TabFragmentViewModel mViewModel;
    // TODO: Rename and change types of parameters
    private String mCollectionReferenceString;
    private OnFragmentInteractionListener mListener;
    private EventsAdapter.OnEventSelected mPostSelectedListener = new EventsAdapter
            .OnEventSelected() {

        @Override
        public void onEventSelected(DocumentSnapshot event, View itemView) {
            Log.d(TAG, "onFirestoreItemSelected: " + itemView);
        }
    };
    private String mTitle;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoveryTabFragment newInstance(int sectionNumber) {
        DiscoveryTabFragment fragment = new DiscoveryTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = "Discovery";
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUserVisibleHint(true);
        if (getArguments() != null) {
            int mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        mCollectionReferenceString = FirestoreCollections.EVENTS;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);

        mViewModel = ViewModelProviders.of(this).get(TabFragmentViewModel.class);

        initFirestore();
        initRecyclerView();
        // Filter Dialog
        mFilterDialog = Dialogs.PostsFilterDialogFragment();
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onResume() {
        super.onResume();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        // Get the 50 highest rated posts
        mQuery = mFirestore.collection(mCollectionReferenceString)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        EventsAdapter adapter = Adapters.eventAdapter(this, mQuery, mPostSelectedListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter((RecyclerView.Adapter) adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public FirebaseFirestore getFirestore() {
        return mFirestore;
    }

    @Override
    public void setFirestore(FirebaseFirestore firestore) {
        mFirestore = firestore;
    }

    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(Query query) {
        mQuery = query;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
