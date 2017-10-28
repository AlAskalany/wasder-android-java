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
package co.wasder.wasder.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.filter.EventsFilters;
import co.wasder.wasder.filter.Filters;
import co.wasder.wasder.model.Event;

/**
 * Dialog Fragment containing filter form.
 */
public class EventsFilterDialogFragment extends DialogFragment implements DatePickerDialog
        .OnDateSetListener {

    public static final String TAG = "EventsFilterDialog";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_category)
    public Spinner mCategorySpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_city)
    public Spinner mCitySpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_sort)
    public Spinner mSortSpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_price)
    public Spinner mPriceSpinner;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.spinner_date)
    public Spinner mDateSpinner;
    private View mRootView;
    private String mSelectedDate;
    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters_events, container, false);
        ButterKnife.bind(this, mRootView);

        mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    final Calendar c;
                    c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    new DatePickerDialog(getContext(), getTheme(), EventsFilterDialogFragment
                            .this, year, month, day).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        resetFilters();
        dismiss();
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        resetFilters();
        dismiss();
    }

    @Nullable
    private String getSelectedCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category_events).equals(selected)) {
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

    private String getSelectedDate() {
        String selected = (String) mDateSpinner.getSelectedItem();
        if (getString(R.string.value_any_date).equals(selected)) {
            return null;
        } else {
            return mSelectedDate;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_events_by_rating).equals(selected)) {
            return Event.FIELD_AVG_RATING;
        }
        if (getString(R.string.sort_events_by_price).equals(selected)) {
            return Event.FIELD_PRICE;
        }
        if (getString(R.string.sort_events_by_popularity).equals(selected)) {
            return Event.FIELD_POPULARITY;
        }
        if (getString(R.string.sort_events_by_date).equals(selected)) {
            return Event.FIELD_DATE;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_events_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_events_by_price).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_events_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_events_by_date).equals(selected)) {
            return Query.Direction.ASCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mCategorySpinner.setSelection(0);
            mCitySpinner.setSelection(0);
            mPriceSpinner.setSelection(0);
            mDateSpinner.setSelection(0);
            mSortSpinner.setSelection(0);
        }
    }

    private EventsFilters getFilters() {
        EventsFilters eventsFilters = Filters.EventsFilters();

        if (mRootView != null) {
            eventsFilters.setCategory(getSelectedCategory());
            eventsFilters.setCity(getSelectedCity());
            eventsFilters.setPrice(getSelectedPrice());
            eventsFilters.setDate(getSelectedDate());
            eventsFilters.setSortBy(getSelectedSortBy());
            eventsFilters.setSortDirection(getSortDirection());
        }

        return eventsFilters;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mSelectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
    }

    public interface FilterListener {

        void onFilter(EventsFilters eventsFilters);

    }
}
