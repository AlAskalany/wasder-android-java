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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.filter.RestaurantsFilters;
import co.wasder.wasder.model.Model;
import co.wasder.wasder.model.Restaurant;

import static android.app.Activity.RESULT_OK;

/**
 * Dialog Fragment containing filter form.
 */
public class AddRestaurantDialogFragment extends DialogFragment {

    public static final String TAG = "AddRestaurantDialog";
    private static final int INITIAL_AVG_RATING = 0;
    private static final int INITIAL_NUM_RATINGS = 0;
    private static final int RC_CHOOSE_PHOTO = 101;
    private static final int RC_IMAGE_PERMS = 102;
    private final int INITIAL_CATEGORY_SELECTION = 0;
    private final int INITIAL_CITY_SELECTION = 0;
    private final int INITIAL_PRICE_SELECTION = 0;
    private final String INITIAL_NAME = "";
    @BindView(R.id.restaurantNameEditText)
    EditText mRestaurantNameEditText;
    @BindView(R.id.spinner_category)
    Spinner mCategorySpinner;
    @BindView(R.id.spinner_city)
    Spinner mCitySpinner;
    @BindView(R.id.spinner_price)
    Spinner mPriceSpinner;
    private String uuid;
    private View mRootView;
    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_add_restaurant, container, false);
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
        mRestaurantNameEditText.setText(INITIAL_NAME);
        mCategorySpinner.setSelection(INITIAL_CATEGORY_SELECTION);
        mCitySpinner.setSelection(INITIAL_CITY_SELECTION);
        mPriceSpinner.setSelection(INITIAL_PRICE_SELECTION);
    }

    @OnClick(R.id.button_add_restaurant)
    public void onAddRestaurantClicked() {
        addRestaurantToDatabase(createRestaurantFromFields());
        dismiss();
    }

    @NonNull
    private Restaurant createRestaurantFromFields() {
        Random random = new Random();

        return Model.Restaurant(getRestaurantName(), getRestaurantCity(), getRestaurantCategory()
                , uuid, getRestaurantPrice(), INITIAL_AVG_RATING, INITIAL_NUM_RATINGS);
    }

    private void addRestaurantToDatabase(@NonNull Restaurant restaurant) {
        CollectionReference restaurants = FirebaseFirestore.getInstance().collection("restaurants");
        restaurants.add(restaurant);
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getRestaurantCategory() {
        String selected = (String) mCategorySpinner.getSelectedItem();
        if (getString(R.string.value_any_category_restaurants).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private String getRestaurantName() {
        String name = mRestaurantNameEditText.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            return name;
        } else {
            return null;
        }
    }

    @Nullable
    private String getRestaurantCity() {
        String selected = (String) mCitySpinner.getSelectedItem();
        if (getString(R.string.value_any_city).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private int getRestaurantPrice() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                uploadPhoto(selectedImage);
            } else {
                Toast.makeText(getContext(), "No image chosen", Toast.LENGTH_SHORT).show();
            }
        } /*else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE
                && EasyPermissions.hasPermissions(this, PERMS)) {
            choosePhoto();
        }*/
    }

    @OnClick(R.id.selected_restaurant_image)
    protected void choosePhoto() {
        /*if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rational_image_perm),
                    RC_IMAGE_PERMS, PERMS);
            return;
        }*/

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void uploadPhoto(Uri uri) {
        // Reset UI
        //hideDownloadUI();
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        // Upload to Cloud Storage
        uuid = UUID.randomUUID().toString();
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(uri).addOnSuccessListener(getActivity(), new
                OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //noinspection LogConditional
                Log.d(TAG, "uploadPhoto:onSuccess:" + taskSnapshot.getMetadata().getReference()
                        .getPath());
                Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

                //showDownloadUI();
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "uploadPhoto:onError", e);
                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface FilterListener {

        void onFilter(RestaurantsFilters restaurantsFilters);

    }
}
