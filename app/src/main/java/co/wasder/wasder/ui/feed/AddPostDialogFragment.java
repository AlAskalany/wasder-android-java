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
package co.wasder.wasder.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.text.MessageFormat;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.data.FeedModel;
import co.wasder.wasder.data.FirestoreItemFilters;

import static android.app.Activity.RESULT_OK;

/** Dialog Fragment containing filter form. */
@Keep
public class AddPostDialogFragment extends DialogFragment {

    public static final String TAG = "AddPostDialog";
    public static final int RC_CHOOSE_PHOTO = 101;

    @SuppressWarnings("unused")
    public static final int RC_IMAGE_PERMS = 102;

    @Nullable
    @BindView(R.id.itemEditText)
    public EditText mFeedEditText;

    public String uuid;
    private Context context;

    public void addPostToDatabase(@NonNull final FeedModel feedModel) {
        final CollectionReference posts = FirebaseFirestore.getInstance().collection("posts");
        if (feedModel.getUid() != null && feedModel.getFeedText() != null) {
            posts.add(feedModel)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Post Added", Toast.LENGTH_LONG).show();
                                }
                            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        final View mRootView = inflater.inflate(R.layout.dialog_add_item, container, false);
        ButterKnife.bind(this, mRootView);
        context = getActivity();
        return mRootView;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof FilterListener) {
            @SuppressWarnings("unused")
            final FilterListener mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        // TODO remove name parameter
        return new FeedModel(uId, "NAME", getPostProfilePhotoUrl(), getUuid(), getFeedText());
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
        assert mFeedEditText != null;
        Editable text = mFeedEditText.getText();
        final String feedText = text.toString();
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
    public void onActivityResult(
            final int requestCode, final int resultCode, @NonNull final Intent data) {
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

        final Intent i =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    public void uploadPhoto(@NonNull final Uri uri) {
        // Reset UI
        // hideDownloadUI();
        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        // Upload to Cloud Storage
        uuid = UUID.randomUUID().toString();
        final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        FragmentActivity activity = getActivity();
        assert activity != null;
        mImageRef
                .putFile(uri)
                .addOnSuccessListener(
                        activity,
                        taskSnapshot -> {
                            //noinspection LogConditional
                            final StorageMetadata storageMetadata = taskSnapshot.getMetadata();
                            final StorageReference storageReference;
                            if (storageMetadata != null) {
                                storageReference = storageMetadata.getReference();
                                if (storageReference != null) {
                                    Log.d(
                                            TAG,
                                            MessageFormat.format(
                                                    "uploadPhoto:onSuccess:{0}",
                                                    storageReference.getPath()));
                                }
                                Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT)
                                        .show();
                            }

                            // showDownloadUI();
                        })
                .addOnFailureListener(
                        activity,
                        e -> {
                            Log.w(TAG, "uploadPhoto:onError", e);
                            Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT)
                                    .show();
                        });
    }

    @Nullable
    public String getPostProfilePhotoUrl() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        Uri profilePhotoUri;
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
