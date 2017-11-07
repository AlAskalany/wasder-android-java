package co.wasder.wasder.ui.feed;

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
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.ProfileActivity;
import co.wasder.wasder.R;
import co.wasder.wasder.SignInResultNotifier;
import co.wasder.wasder.adapter.FirestoreItemsAdapter;
import co.wasder.wasder.dialog.AddFirestoreItemDialogFragment;
import co.wasder.wasder.dialog.Dialogs;
import co.wasder.wasder.filter.FirestoreItemFilters;
import co.wasder.wasder.model.AbstractFirestoreItem;
import co.wasder.wasder.model.FirestoreItem;
import co.wasder.wasder.model.User;
import co.wasder.wasder.ui.OnFragmentInteractionListener;
import co.wasder.wasder.ui.TabFragment;
import co.wasder.wasder.viewmodel.TabFragmentViewModel;
import co.wasder.wasder.views.FeedView;
import co.wasder.wasder.views.FirestoreCollections;

/**
 * Created by Ahmed AlAskalany on 10/30/2017.
 * Navigator
 */
@Keep
public class FeedTabFragment extends Fragment implements TabFragment, LifecycleOwner,
        FirebaseAuth.AuthStateListener {

    public static final long LIMIT = 50;
    public static final String TAG = "TabFragment";
    public static final String ARG_SECTION_NUMBER = "section_number";
    private static final CollectionReference postsCollection = FirebaseFirestore.getInstance()
            .collection("posts");
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
        public void onFirestoreItemSelected(AbstractFirestoreItem event, View itemView) {

        }
    };

    public static FeedTabFragment newInstance() {
        FeedTabFragment fragment = new FeedTabFragment();
        fragment.mTitle = "Feed";
        return fragment;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        mViewModel = ViewModelProviders.of(this).get(TabFragmentViewModel.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAddPostDialog = Dialogs.AddPostDialogFragment();
        SearchView mSearchView = getActivity().findViewById(R.id.searchView);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FirestoreItemFilters firestoreItemFilters = FirestoreItemFilters.getDefault();
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                CollectionReference usersReference = FirebaseFirestore.getInstance()
                        .collection("users");
                Query query = usersReference.whereEqualTo("displayName", search);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryResult = task.getResult();
                            List<DocumentSnapshot> usersDocuments = queryResult.getDocuments();
                            if (queryResult.size() > 0) {
                                User firstUser = usersDocuments.get(0).toObject(User.class);
                                String userId = firstUser.getUid();
                                FirestoreItemFilters firestoreItemFilters = new
                                        FirestoreItemFilters();
                                firestoreItemFilters.setUid(userId);
                            }
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                return false;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        FirestoreRecyclerOptions<FirestoreItem> options = new FirestoreRecyclerOptions
                .Builder<FirestoreItem>()
                .setQuery(mQuery, FirestoreItem.class)
                .build();
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
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

    protected RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<FirestoreItem> options = new FirestoreRecyclerOptions
                .Builder<FirestoreItem>()
                .setQuery(mQuery, FirestoreItem.class)
                .setLifecycleOwner(this)
                .build();

        return new FirestoreRecyclerAdapter<FirestoreItem, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChatHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_firestore_item, parent, false));
            }

            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, FirestoreItem model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
    public void setFirestore(FirebaseFirestore instance) {

    }

    @Override
    public Query getQuery() {
        return null;
    }

    @Override
    public void setQuery(Query timestamp) {

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

        public ChatHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final AbstractFirestoreItem chat) {
            String userId = chat.getUid();
            CollectionReference users = FirebaseFirestore.getInstance()
                    .collection(FirestoreCollections.USERS);
            final DocumentReference userReference = users.document(userId);
            Task<DocumentSnapshot> getUserTask = userReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user = task.getResult().toObject(User.class);
                            String userName = user.getDisplayName();
                            feedView.getHeader().getUserName().setText(userName);
                        }
                    });

            // Load image
            String uuid = null;
            uuid = chat.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(feedView.getItemImage().getItemImageView());
                feedView.getItemImage().makeVisible();
            }
            String profilePhotoUrl = chat.getProfilePhoto();
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
                        public void onClick(View v) {
                            if (chat.getUid() != null) {
                                Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                        .class);
                                intent.putExtra("user-reference", chat.getUid());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });
            Date date = chat.getTimestamp();
            if (date != null) {
                String dateString = new SimpleDateFormat().format(date);
                feedView.getHeader().getTimeStamp().setText(dateString);
            }
            feedView.getItemText().getItemTextView().setText(chat.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(v.getContext(), feedView.getHeader()
                            .getExpandButton());
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    final String currentUserId = user.getUid();
                    if (!TextUtils.equals(chat.getUid(), currentUserId)) {
                        popup.getMenu().getItem(1).setVisible(false);
                        popup.getMenu().getItem(2).setVisible(true);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    Task<Void> reference = firestore.collection
                                            (FirestoreCollections.POSTS)
                                            .document(chat.getUid())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: ");
                                                    }
                                                }
                                            });
                                    break;
                                case R.id.follow:
                                    DatabaseReference database = FirebaseDatabase.getInstance()
                                            .getReference("users");
                                    DatabaseReference followedFollowersRef = database.child(chat
                                            .getUid())
                                            .child("followers");

                                    Map<String, Object> data = new HashMap<>();
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
