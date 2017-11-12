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
package co.wasder.wasder.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.MessageFormat;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.data.filter.FirestoreItemFilters;
import co.wasder.data.model.FeedModel;
import co.wasder.data.model.Model;
import co.wasder.wasder.R;

import static android.app.Activity.RESULT_OK;

/**
 * Dialog Fragment containing filter form.
 */
@Keep
public class AddFirestoreItemDialogFragment extends DialogFragment {

    public static final String TAG = "AddPostDialog";
    public static final int INITIAL_AVG_RATING = 0;
    public static final int INITIAL_NUM_RATINGS = 0;
    public static final int RC_CHOOSE_PHOTO = 101;
    @SuppressWarnings("unused")
    public static final int RC_IMAGE_PERMS = 102;
    @Nullable
    @BindView(R.id.itemEditText)
    public EditText mFeedEditText;

    public String uuid;
    public String feedText;
    public String postProfilePhotoUrl;

    public static void addPostToDatabase(@NonNull final FeedModel feedModel) {
        final CollectionReference posts = FirebaseFirestore.getInstance().collection("posts");
        posts.add(feedModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentReference> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View mRootView = inflater.inflate(R.layout.dialog_add_item, container, false);
        ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof FilterListener) {
            @SuppressWarnings("unused") final FilterListener mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
        }
    }

    @OnClick(R.id.button_add_item)
    public void onAddPostClicked() {
        addPostToDatabase(createPostFromFields());
        dismiss();
    }

    @NonNull
    public FeedModel createPostFromFields() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        String uId = null;
        if (user != null) {
            uId = user.getUid();
        }
        return Model.FirestoreItem(uId, getPostProfilePhotoUrl(), getUuid(), INITIAL_AVG_RATING,
                INITIAL_NUM_RATINGS, getFeedText());
    }

    @Nullable
    public String getUuid() {
        if (uuid != null) {
            return uuid;
        } else {
            return null;
        }
    }

    @Nullable
    public String getFeedText() {
        final String feedText = mFeedEditText.getText().toString();
        if (!TextUtils.isEmpty(feedText)) {
            return feedText;
        } else {
            return null;
        }
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @NonNull final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                final Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    uploadPhoto(selectedImage);
                }
            } else {
                Toast.makeText(getContext(), "No image chosen", Toast.LENGTH_SHORT).show();
            }
        } /*else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE
                && EasyPermissions.hasPermissions(this, PERMS)) {
            choosePhoto();
        }*/
    }

    @OnClick(R.id.selected_item_image)
    protected void choosePhoto() {
        /*if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rational_image_perm),
                    RC_IMAGE_PERMS, PERMS);
            return;
        }*/

        final Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    public void uploadPhoto(@NonNull final Uri uri) {
        // Reset UI
        //hideDownloadUI();
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        // Upload to Cloud Storage
        uuid = UUID.randomUUID().toString();
        final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(uri)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask
                        .TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull final UploadTask.TaskSnapshot taskSnapshot) {
                        //noinspection LogConditional
                        final StorageMetadata storageMetadata = taskSnapshot.getMetadata();
                        final StorageReference storageReference;
                        if (storageMetadata != null) {
                            storageReference = storageMetadata.getReference();
                            if (storageReference != null) {
                                Log.d(TAG, MessageFormat.format("uploadPhoto:onSuccess:{0}",
                                        storageReference
                                        .getPath()));
                            }
                            Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        //showDownloadUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull final Exception e) {
                        Log.w(TAG, "uploadPhoto:onError", e);
                        Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    public String getPostProfilePhotoUrl() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        Uri profilePhotoUri = null;
        if (user != null) {
            profilePhotoUri = user.getPhotoUrl();
            if (profilePhotoUri != null) {
                final String profilePhotoUrl = profilePhotoUri.toString();
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    return profilePhotoUrl;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    interface FilterListener {

        @SuppressWarnings("unused")
        void onFilter(FirestoreItemFilters firestoreItemFilters);

    }
}
