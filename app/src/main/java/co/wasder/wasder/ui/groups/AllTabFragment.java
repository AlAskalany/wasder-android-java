package co.wasder.wasder.ui.groups;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import co.wasder.wasder.R;
import co.wasder.wasder.data.BaseModel;
import co.wasder.wasder.data.FeedModel;
import co.wasder.wasder.databinding.FragmentTabBinding;
import co.wasder.wasder.ui.OnFirestoreItemSelectedListener;
import co.wasder.wasder.ui.feed.AddPostDialogFragment;
import co.wasder.wasder.ui.feed.FeedRecyclerAdapter;
import co.wasder.wasder.ui.feed.FeedViewHolder;
import co.wasder.wasder.ui.navigation.BaseTabFragment;

/** Created by Ahmed AlAskalany on 10/30/2017. Navigator */
@Keep
public class AllTabFragment extends BaseTabFragment {

    public static String ARG_SECTION_NUMBER = "section-number";

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public String TAG;
    public AddPostDialogFragment mAddPostDialog;

    @NonNull
    protected OnFirestoreItemSelectedListener onFirestoreItemSelectedListener =
            new OnFirestoreItemSelectedListener() {
                @Override
                public void onFirestoreItemSelected(BaseModel event, View itemView) {}

                @Override
                public void onChildChanged(
                        ChangeEventType type,
                        DocumentSnapshot snapshot,
                        int newIndex,
                        int oldIndex) {}

                @Override
                public void onDataChanged() {}

                @Override
                public void onError(FirebaseFirestoreException e) {}
            };

    private FragmentTabBinding binding;

    public static AllTabFragment newInstance(int sectionNumber, String title) {
        AllTabFragment fragment = new AllTabFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.mTitle = title;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab, container, false);
        // TODO create AllTabFragmentViewModel
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = new AddPostDialogFragment();
        setupSearchAndFilters();
        return binding.getRoot();
    }

    protected void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter<FeedViewHolder> adapter =
                FeedRecyclerAdapter.getAdapter(
                        this, onFirestoreItemSelectedListener, mQuery, FeedModel.class);

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(final int positionStart, final int itemCount) {
                        binding.recyclerView.smoothScrollToPosition(0);
                    }
                });
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), R.string.signing_in, Toast.LENGTH_SHORT).show();
            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {});
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }
}
