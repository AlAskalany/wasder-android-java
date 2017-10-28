/*
  Copyright 2017 Google Inc. All Rights Reserved.
  <p>
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  <p>
  http://www.apache.org/licenses/LICENSE-2.0
  <p>
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package co.wasder.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.filter.Filters;
import co.wasder.filter.PostsFilters;
import co.wasder.model.Post;
import co.wasder.wasder.R;

/**
 * Dialog Fragment containing filter form.
 */
public class PostsFilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_city)
    Spinner mCitySpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_price)
    Spinner mPriceSpinner;
    private View mRootView;
    private FilterListener mFilterListener;

    public PostsFilterDialogFragment() {
    }

    @SuppressWarnings("unused")
    public static PostsFilterDialogFragment newInstance() {
        return new PostsFilterDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters_posts, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement " + "FilterListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
        }
    }

    @OnClick(R.id.button_search)
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category_posts).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedCity() {
        String selected = (String) mCitySpinner.getSelectedItem();
        if (getString(R.string.value_any_city).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private int getSelectedPrice() {
        String selected = (String) mPriceSpinner.getSelectedItem();
        if (selected.equals(getString(R.string.price_1))) {
            return 1;
        } else if (selected.equals(getString(R.string.price_2))) {
            return 2;
        } else if (selected.equals(getString(R.string.price_3))) {
            return 3;
        } else {
            return -1;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_posts_by_rating).equals(selected)) {
            return Post.FIELD_AVG_RATING;
        }
        if (getString(R.string.sort_posts_by_price).equals(selected)) {
            return Post.FIELD_PRICE;
        }
        if (getString(R.string.sort_posts_by_popularity).equals(selected)) {
            return Post.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_posts_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_posts_by_price).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_posts_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mCategorySpinner.setSelection(0);
            mCitySpinner.setSelection(0);
            mPriceSpinner.setSelection(0);
            mSortSpinner.setSelection(0);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public PostsFilters getFilters() {
        PostsFilters postsFilters = Filters.PostsFilters();

        if (mRootView != null) {
            postsFilters.setCategory(getSelectedCategory());
            postsFilters.setCity(getSelectedCity());
            postsFilters.setPrice(getSelectedPrice());
            postsFilters.setSortBy(getSelectedSortBy());
            postsFilters.setSortDirection(getSortDirection());
        }

        return postsFilters;
    }

    public interface FilterListener {

        void onFilter(PostsFilters postsFilters);

    }
}
