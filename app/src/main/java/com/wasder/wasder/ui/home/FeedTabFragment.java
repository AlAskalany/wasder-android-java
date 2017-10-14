package com.wasder.wasder.ui.home;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.wasder.wasder.R;
import com.wasder.wasder.adapter.Adapters;
import com.wasder.wasder.adapter.RestaurantAdapter;
import com.wasder.wasder.dialog.AddRestaurantDialogFragment;
import com.wasder.wasder.dialog.Dialogs;
import com.wasder.wasder.dialog.RestaurantsFilterDialogFragment;
import com.wasder.wasder.filter.RestaurantsFilters;
import com.wasder.wasder.ui.TabFragment;
import com.wasder.wasder.viewmodel.FeedFragmentViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class FeedTabFragment extends TabFragment implements RestaurantsFilterDialogFragment
        .FilterListener {

    private static final long LIMIT = 50;
    private static final String TAG = "FeedTab";
    @BindView(R.id.homeFeedRecyclerView)
    RecyclerView mFeedRecyclerView;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    @SuppressWarnings("FieldCanBeLocal")
    private RestaurantsFilterDialogFragment mFilterDialog;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private AddRestaurantDialogFragment mAddRestaurantDialog;
    private FeedFragmentViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home_feed, container, false);
        ButterKnife.bind(this, view);

        mViewModel = ViewModelProviders.of(this).get(FeedFragmentViewModel.class);

        initFirestore();
        initRecyclerView();
        // Filter Dialog
        mFilterDialog = Dialogs.RestaurantsFilterDialogFragment();
        mAddRestaurantDialog = Dialogs.AddRestaurantDialogFragment();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        onFilter(mViewModel.getFilters());
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true).build();
        mFirestore.setFirestoreSettings(settings);

        // Get the 50 highest rated restaurants
        mQuery = mFirestore.collection("restaurants").orderBy("avgRating", Query.Direction
                .DESCENDING).limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        RestaurantAdapter adapter = Adapters.RestaurantAdapter(this, mQuery);
        mFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onFilter(RestaurantsFilters filters) {
        // Construct query basic query
        Query query = mFirestore.collection("restaurants");

        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo("category", filters.getCategory());
        }

        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo("city", filters.getCity());
        }

        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo("price", filters.getPrice());
        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mQuery = query;

        // Set header
        //mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(this)));
        //mCurrentSortByView.setText(filters.getOrderDescription(this));

        // Save filters
        mViewModel.setFilters(filters);
    }
}
