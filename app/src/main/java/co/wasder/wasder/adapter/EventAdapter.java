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
package co.wasder.wasder.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.EventUtil;
import co.wasder.wasder.model.Event;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Events.
 */
public class EventAdapter extends FirestoreAdapter<EventAdapter.ViewHolder> {

    private final OnEventSelectedListener mListener;

    protected EventAdapter(Query query, OnEventSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_event, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    public interface OnEventSelectedListener {

        void onEventSelected(DocumentSnapshot event);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.event_user_name)
        TextView userNameView;

        @BindView(R.id.event_item_image)
        ImageView imageView;

        @BindView(R.id.event_item_name)
        TextView eventNameView;

        @BindView(R.id.event_item_rating)
        MaterialRatingBar ratingBar;

        @BindView(R.id.event_item_num_ratings)
        TextView numRatingsView;

        @BindView(R.id.event_item_price)
        TextView priceView;

        @BindView(R.id.event_item_category)
        TextView categoryView;

        @BindView(R.id.event_item_city)
        TextView cityView;

        @BindView(R.id.event_item_date)
        TextView dateView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnEventSelectedListener listener) {

            Event event = snapshot.toObject(Event.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext()).load(event.getPhoto()).into(imageView);

            userNameView.setText(event.getUserName());
            eventNameView.setText(event.getEventName());
            ratingBar.setRating((float) event.getAvgRating());
            cityView.setText(event.getCity());
            categoryView.setText(event.getCategory());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings, event
                    .getNumRatings()));
            priceView.setText(EventUtil.getPriceString(event));
            dateView.setText(event.getDate());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onEventSelected(snapshot);
                    }
                }
            });
        }

    }
}
