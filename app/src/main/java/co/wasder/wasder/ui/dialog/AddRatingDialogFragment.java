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
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Util.FirestoreItemUtil;
import co.wasder.wasder.data.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Dialog Fragment containing rating form.
 */
@Keep
public class AddRatingDialogFragment extends DialogFragment {

    public static final String TAG = "RatingDialog";

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.post_form_rating)
    public MaterialRatingBar mRatingBar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.post_form_text)
    public EditText mRatingText;
    public RatingListener mRatingListener;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_rating, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        if (context instanceof RatingListener) {
            mRatingListener = (RatingListener) context;
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

    @OnClick(R.id.post_form_button)
    public void onSubmitClicked(@SuppressWarnings("unused") final View view) {
        final Rating rating = new Rating(FirestoreItemUtil.getCurrentUser(), mRatingBar
                .getRating(), mRatingText.getText().toString());

        if (mRatingListener != null) {
            mRatingListener.onRating(rating);
        }

        dismiss();
    }

    @OnClick(R.id.post_form_cancel)
    public void onCancelClicked(@SuppressWarnings("unused") final View view) {
        dismiss();
    }

    public interface RatingListener {

        void onRating(Rating rating);

    }
}
