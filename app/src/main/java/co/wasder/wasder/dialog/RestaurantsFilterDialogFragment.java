/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.wasder.wasder.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.filter.Filters;
import co.wasder.wasder.filter.RestaurantsFilters;
import co.wasder.wasder.model.Restaurant;

/**
 * Dialog Fragment containing filter form.
 */
public class RestaurantsFilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";
    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;
    @BindView(R.id.spinner_city)
    Spinner mCitySpinner;
    @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;
    @BindView(R.id.spinner_price)
    Spinner mPriceSpinner;
    private View mRootView;
    private FilterListener mFilterListener;

    public RestaurantsFilterDialogFragment() {
    }

    public static RestaurantsFilterDialogFragment newInstance() {
        return new RestaurantsFilterDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters_restaurants, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
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
        if (getString(R.string.value_any_category_restaurants).equals(selected)) {
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
        if (getString(R.string.sort_restaurants_by_rating).equals(selected)) {
            return Restaurant.FIELD_AVG_RATING;
        }
        if (getString(R.string.sort_restaurants_by_price).equals(selected)) {
            return Restaurant.FIELD_PRICE;
        }
        if (getString(R.string.sort_restaurants_by_popularity).equals(selected)) {
            return Restaurant.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_restaurants_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_restaurants_by_price).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_restaurants_by_popularity).equals(selected)) {
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

    public RestaurantsFilters getFilters() {
        RestaurantsFilters restaurantsFilters = Filters.RestaurantsFilters();

        if (mRootView != null) {
            restaurantsFilters.setCategory(getSelectedCategory());
            restaurantsFilters.setCity(getSelectedCity());
            restaurantsFilters.setPrice(getSelectedPrice());
            restaurantsFilters.setSortBy(getSelectedSortBy());
            restaurantsFilters.setSortDirection(getSortDirection());
        }

        return restaurantsFilters;
    }

    public interface FilterListener {

        void onFilter(RestaurantsFilters restaurantsFilters);

    }
}
