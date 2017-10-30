package co.wasder.wasder.ui.tabs.feed;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.adapter.Adapters;
import co.wasder.wasder.adapter.PostAdapter;
import co.wasder.wasder.dialog.AddPostDialogFragment;
import co.wasder.wasder.dialog.Dialogs;
import co.wasder.wasder.dialog.PostsFilterDialogFragment;
import co.wasder.wasder.jobservices.FirestoreQueryJobService;
import co.wasder.wasder.ui.TabFragment;
import co.wasder.wasder.viewmodel.TabFragmentViewModel;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */

public class FeedTabFragment extends TabFragment implements LifecycleOwner {

    @SuppressWarnings("unused")
    private static final int FEED = 0;
    @SuppressWarnings("unused")
    private static final int Following = 1;
    @SuppressWarnings("unused")
    private static final int Discovery = 2;
    @SuppressWarnings("unused")
    private static final int Favorites = 3;
    @SuppressWarnings("unused")
    private static final int ALL = 4;
    @SuppressWarnings("unused")
    private static final int OWNED = 5;
    @SuppressWarnings("unused")
    private static final int MENTIONS = 6;
    @SuppressWarnings("unused")
    private static final int PM = 7;
    private static final long LIMIT = 50;
    private static final String TAG = "TabFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COLLECTION_REFERENCE_STRING = "collection_reference_string";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_TAB_TYPE = "tab_type";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private PostsFilterDialogFragment mFilterDialog;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private AddPostDialogFragment mAddPostDialog;
    private TabFragmentViewModel mViewModel;
    // TODO: Rename and change types of parameters
    private String mCollectionReferenceString;
    private OnFragmentInteractionListener mListener;
    private PostAdapter.OnPostSelectedListener mPostSelectedListener = new PostAdapter
            .OnPostSelectedListener() {

        @Override
        public void onPostSelectedListener(DocumentSnapshot event, View itemView) {
            Log.d(TAG, "onPostSelectedListener: " + itemView);
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedTabFragment newInstance(int sectionNumber) {
        FeedTabFragment fragment = new FeedTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private static Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // Call this service when the criteria are met.
                .setService(FirestoreQueryJobService.class)
                // unique id of the task
                .setTag("OneTimeJob")
                // We are mentioning that the job is not periodic.
                .setRecurring(false)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 60))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK).build();
    }

    private static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUserVisibleHint(true);
        if (getArguments() != null) {
            int mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        mTitle = "Feed";
        mCollectionReferenceString = "restaurants";
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
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        PostAdapter adapter = Adapters.PostAdapter(this, mQuery, mPostSelectedListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        @SuppressWarnings({"EmptyMethod", "unused"})
        void onFragmentInteraction(@SuppressWarnings("unused") Uri uri);
    }
}
