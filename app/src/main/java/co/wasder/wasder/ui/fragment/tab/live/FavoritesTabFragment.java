package co.wasder.wasder.ui.fragment.tab.live;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
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
import com.wasder.wasder.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;
import co.wasder.wasder.ui.dialog.FirestoreItemFilterDialogFragment;
import co.wasder.wasder.ui.fragment.OnFragmentInteractionListener;
import co.wasder.wasder.ui.fragment.tab.TabFragment;
import co.wasder.wasder.ui.fragment.tab.TabFragmentViewModel;
import co.wasder.wasder.ui.fragment.tab.adapter.Adapters;
import co.wasder.wasder.ui.fragment.tab.adapter.EventsAdapter;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FavoritesTabFragment extends Fragment implements TabFragment, LifecycleOwner {

    public static final long LIMIT = FirebaseUtil.LIMIT;
    public static final String TAG = "TabFragment";
    public static final String ARG_SECTION_NUMBER = Utils.ARG_SECTION_NUMBER;
    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    public FirebaseFirestore mFirestore;
    public Query mQuery;
    public FirestoreItemFilterDialogFragment mFilterDialog;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public TabFragmentViewModel mViewModel;
    // TODO: Rename and change types of parameters
    public String mCollectionReferenceString;
    public OnFragmentInteractionListener mListener;
    public EventsAdapter.OnEventSelected mPostSelectedListener = new EventsAdapter
            .OnEventSelected() {

        @Override
        public void onEventSelected(final DocumentSnapshot event, final View itemView) {
            Log.d(TAG, "onFirestoreItemSelected: " + itemView);
        }
    };
    public String mTitle;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesTabFragment newInstance(final int sectionNumber) {
        final FavoritesTabFragment fragment = new FavoritesTabFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = "Favorites";
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setUserVisibleHint(true);
        if (getArguments() != null) {
            final int mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        mCollectionReferenceString = Utils.EVENTS;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);
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

    public void initFirestore() {
        mFirestore = FirebaseUtil.getFirestore();
        final FirebaseFirestoreSettings settings = FirebaseUtil.getFirebaseFirestoreSettings();
        mFirestore.setFirestoreSettings(settings);

        // Get the 50 highest rated posts
        mQuery = mFirestore.collection(mCollectionReferenceString)
                .orderBy(Utils.TIMESTAMP, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    public void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        final EventsAdapter adapter = Adapters.eventAdapter(this, mQuery, mPostSelectedListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter((RecyclerView.Adapter) adapter);
    }

    @Override
    public void onAttach(final Context context) {
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
    public void setFirestore(final FirebaseFirestore firestore) {
        mFirestore = firestore;
    }

    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(final Query query) {
        mQuery = query;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }
}
