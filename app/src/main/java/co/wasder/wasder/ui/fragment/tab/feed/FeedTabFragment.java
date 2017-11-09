package co.wasder.wasder.ui.fragment.tab.feed;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Util.FirestoreItemUtil;
import co.wasder.wasder.data.filter.FirestoreItemFilters;
import co.wasder.wasder.data.model.AbstractFirestoreItem;
import co.wasder.wasder.data.model.FirestoreItem;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.SignInResultNotifier;
import co.wasder.wasder.ui.activity.detail.ProfileActivity;
import co.wasder.wasder.ui.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.ui.dialog.Dialogs;
import co.wasder.wasder.ui.fragment.OnFragmentInteractionListener;
import co.wasder.wasder.ui.fragment.tab.TabFragment;
import co.wasder.wasder.ui.fragment.tab.TabFragmentViewModel;
import co.wasder.wasder.ui.fragment.tab.adapter.FirestoreItemsAdapter;
import co.wasder.wasder.ui.views.FeedView;
import co.wasder.wasder.ui.views.FirestoreCollections;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FeedTabFragment extends Fragment implements TabFragment, LifecycleOwner,
        FirebaseAuth.AuthStateListener {

    public static final long LIMIT = FirebaseUtil.LIMIT;
    public static final String TAG = "TabFragment";
    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final CollectionReference postsCollection = FirebaseUtil.getUsersCollectionReference("posts");
    private static final Query mQuery = postsCollection.orderBy("timestamp", Query.Direction
            .DESCENDING)
            .limit(LIMIT);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    @BindView(R.id.recyclerView)
    public RecyclerView mRecyclerView;
    public AddFirestoreItemDialogFragment mAddPostDialog;
    public TabFragmentViewModel mViewModel;
    public OnFragmentInteractionListener mListener;

    public String mTitle;
    private FirestoreItemsAdapter.OnFirestoreItemSelected onFirestoreItemSelected = new
            FirestoreItemsAdapter.OnFirestoreItemSelected() {


        @Override
        public void onFirestoreItemSelected(final AbstractFirestoreItem event, final View itemView) {

        }
    };

    public static FeedTabFragment newInstance() {
        final FeedTabFragment fragment = new FeedTabFragment();
        fragment.mTitle = "Feed";
        return fragment;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        mViewModel = ViewModelProviders.of(this).get(TabFragmentViewModel.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        setupSearchAndFilters();
        return view;
    }

    private void setupSearchAndFilters() {
        final SearchView mSearchView = getActivity().findViewById(R.id.searchView);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                final FirestoreItemFilters firestoreItemFilters = FirestoreItemFilters.getDefault();
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String search) {
                final CollectionReference usersReference = FirebaseUtil.getUsersCollectionReference("users");
                final Query query = usersReference.whereEqualTo("displayName", search);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final QuerySnapshot queryResult = task.getResult();
                            final List<DocumentSnapshot> usersDocuments = queryResult.getDocuments();
                            if (queryResult.size() > 0) {
                                final User firstUser = usersDocuments.get(0).toObject(User.class);
                                final String userId = firstUser.getUid();
                                final FirestoreItemFilters firestoreItemFilters = new
                                        FirestoreItemFilters();
                                firestoreItemFilters.setUid(userId);
                            }
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String search) {
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
        FirebaseUtil.getAuth().addAuthStateListener(this);
    }

    private boolean isSignedIn() {
        return FirestoreItemUtil.getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        final FirestoreRecyclerOptions<FirestoreItem> options = new FirestoreRecyclerOptions
                .Builder<FirestoreItem>()
                .setQuery(mQuery, FirestoreItem.class)
                .build();
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(final int positionStart, final int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();
        FirebaseUtil.getAuth().removeAuthStateListener(this);
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

    protected RecyclerView.Adapter newAdapter() {
        final FirestoreRecyclerOptions<FirestoreItem> options =
                getFirestoreItemFirestoreRecyclerOptions();

        return new FirestoreRecyclerAdapter<FirestoreItem, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_firestore_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(final ChatHolder holder, final int position, final FirestoreItem model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    private FirestoreRecyclerOptions<FirestoreItem> getFirestoreItemFirestoreRecyclerOptions() {
        return new FirestoreRecyclerOptions
                    .Builder<FirestoreItem>()
                    .setQuery(mQuery, FirestoreItem.class)
                    .setLifecycleOwner(this)
                    .build();
    }

    @Override
    public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        } else {
            Toast.makeText(getContext(), R.string.signing_in, Toast.LENGTH_SHORT).show();
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(new SignInResultNotifier(getContext()));
        }
    }

    @Override
    public FirebaseFirestore getFirestore() {
        return null;
    }

    @Override
    public void setFirestore(final FirebaseFirestore instance) {

    }

    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(final Query timestamp) {

    }

    @Override
    public String getTitle() {
        return null;
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        public final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        @BindView(R.id.feedView)
        public FeedView feedView;
        public String tag;

        public ChatHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final AbstractFirestoreItem chat) {
            final String userId = chat.getUid();
            final CollectionReference users = FirebaseUtil.getUsersCollectionReference(FirestoreCollections.USERS);
            final DocumentReference userReference = users.document(userId);
            final Task<DocumentSnapshot> getUserTask = userReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            final User user = task.getResult().toObject(User.class);
                            final String userName = user.getDisplayName();
                            feedView.getHeader().getUserName().setText(userName);
                        }
                    });

            // Load image
            String uuid = null;
            uuid = chat.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(feedView.getItemImage().getItemImageView());
                feedView.getItemImage().makeVisible();
            }
            final String profilePhotoUrl = chat.getProfilePhoto();
            if (profilePhotoUrl != null) {
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    GlideApp.with(itemView.getContext())
                            .load(profilePhotoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(feedView.getProfilePhoto().getProfileImageView(userId));

                }
            }
            feedView.getProfilePhoto()
                    .getProfileImageView(userId)
                    .setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(final View v) {
                            if (chat.getUid() != null) {
                                final Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                        .class);
                                intent.putExtra("user-reference", chat.getUid());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });
            final Date date = chat.getTimestamp();
            if (date != null) {
                final String dateString = new SimpleDateFormat().format(date);
                feedView.getHeader().getTimeStamp().setText(dateString);
            }
            feedView.getItemText().getItemTextView().setText(chat.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //Intent intent = new Intent(view.getContext(), FirestoreItemDetailActivity
                    // .class);
                    //intent.putExtra("key_post_id", snapshot.getId());
                    //view.getContext().startActivity(intent);
                    //postItemCardView.setBackgroundColor(Color.GREEN);
                    onFirestoreItemSelected.onFirestoreItemSelected(chat, itemView);
                }
            });

            feedView.getHeader().getExpandButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //creating a popup menu
                    final PopupMenu popup = new PopupMenu(v.getContext(), feedView.getHeader()
                            .getExpandButton());
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    final FirebaseAuth auth = FirebaseUtil.getAuth();
                    final FirebaseUser user = auth.getCurrentUser();
                    final String currentUserId = user.getUid();
                    if (!TextUtils.equals(chat.getUid(), currentUserId)) {
                        popup.getMenu().getItem(1).setVisible(false);
                        popup.getMenu().getItem(2).setVisible(true);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    final FirebaseFirestore firestore = FirestoreItemUtil
                                            .getFirestore();
                                    final Task<Void> reference = firestore.collection
                                            (FirestoreCollections.POSTS)
                                            .document(chat.getUid())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                @Override
                                                public void onComplete(@NonNull final Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: ");
                                                    }
                                                }
                                            });
                                    break;
                                case R.id.follow:
                                    final DatabaseReference database = FirebaseDatabase.getInstance()
                                            .getReference("users");
                                    final DatabaseReference followedFollowersRef = database.child(chat
                                            .getUid())
                                            .child("followers");

                                    final Map<String, Object> data = new HashMap<>();
                                    data.put(currentUserId, true);
                                    followedFollowersRef.updateChildren(data);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }

}
