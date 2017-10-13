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
package com.wasder.wasder.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wasder.wasder.EventsActivity;
import com.wasder.wasder.MainActivity;
import com.wasder.wasder.R;
import com.wasder.wasder.filter.RestaurantsFilters;
import com.wasder.wasder.model.Event;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wasder.wasder.Util.RestaurantUtil.getRandomImageUrl;

/**
 * Dialog Fragment containing filter form.
 */
public class AddEventDialogFragment extends DialogFragment implements
        OnCompleteListener<DocumentReference> {

    public static final String TAG = "AddEventDialog";
    private static final int INITIAL_AVG_RATING = 0;
    private static final int INITIAL_NUM_RATINGS = 0;
    private final int INITIAL_CATEGORY_SELECTION = 0;
    private final int INITIAL_CITY_SELECTION = 0;
    private final int INITIAL_PRICE_SELECTION = 0;
    private final String INITIAL_NAME = "";

    @Override
    public void onComplete(@NonNull Task<DocumentReference> task) {
        if (task.isSuccessful()) {
            Snackbar.make(getActivity().findViewById(R.id.activity_events_coordinator_layout), R
                    .string.snackbar_event_added, Snackbar.LENGTH_SHORT).show();
            dismiss();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.activity_events_coordinator_layout), R
                    .string.snackbar_event_add_failed, Snackbar.LENGTH_SHORT).show();
            dismiss();
        }
    }

    interface FilterListener {

        void onFilter(RestaurantsFilters restaurantsFilters);

    }

    private View mRootView;

    @BindView(R.id.eventNameEditText)
    EditText mEventNameEditText;

    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;

    @BindView(R.id.spinner_city)
    Spinner mCitySpinner;

    @BindView(R.id.spinner_price)
    Spinner mPriceSpinner;

    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_add_event, container, false);
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
        resetFields();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
    }

    private void resetFields() {
        mEventNameEditText.setText(INITIAL_NAME);
        mCategorySpinner.setSelection(INITIAL_CATEGORY_SELECTION);
        mCitySpinner.setSelection(INITIAL_CITY_SELECTION);
        mPriceSpinner.setSelection(INITIAL_PRICE_SELECTION);
    }

    @OnClick(R.id.button_add_event)
    public void onAddEventClicked() {
        addEventToDatabase(createEventFromFields());
    }

    @NonNull
    private Event createEventFromFields() {
        Random random = new Random();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String uId = user.getUid();
        String name = user.getDisplayName();
        return new Event(uId, name, getEventName(), getEventCity(), getEventCategory(),
                getRandomImageUrl(random), getEventPrice(), INITIAL_AVG_RATING,
                INITIAL_NUM_RATINGS);
    }

    private void addEventToDatabase(@NonNull Event event) {
        CollectionReference events = FirebaseFirestore.getInstance().collection("events");
        events.add(event).addOnCompleteListener(this);
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getEventCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category_restaurants).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private String getEventName() {
        String name = (String) mEventNameEditText.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            return name;
        } else {
            return null;
        }
    }

    @Nullable
    private String getEventCity() {
        String selected = (String) mCitySpinner.getSelectedItem();
        if (getString(R.string.value_any_city).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private int getEventPrice() {
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
}
