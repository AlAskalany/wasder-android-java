package com.wasder.wasder.filter;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.firestore.Query;
import com.wasder.wasder.R;
import com.wasder.wasder.Util.EventUtil;
import com.wasder.wasder.model.Event;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class EventsFilters {

    private String category = null;
    private String city = null;
    private int price = -1;
    private String date = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public EventsFilters() {
    }

    public static EventsFilters getDefault() {
        EventsFilters eventsFilters = new EventsFilters();
        eventsFilters.setSortBy(Event.FIELD_AVG_RATING);
        eventsFilters.setSortDirection(Query.Direction.DESCENDING);

        return eventsFilters;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }

    public boolean hasCity() {
        return !(TextUtils.isEmpty(city));
    }

    public boolean hasPrice() {
        return (price > 0);
    }

    public boolean hasDate() {
        return !(TextUtils.isEmpty(date));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (category == null && city == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_events));
            desc.append("</b>");
        }

        if (category != null) {
            desc.append("<b>");
            desc.append(category);
            desc.append("</b>");
        }

        if (category != null && city != null) {
            desc.append(" in ");
        }

        if (city != null) {
            desc.append("<b>");
            desc.append(city);
            desc.append("</b>");
        }

        if (price > 0) {
            desc.append(" for ");
            desc.append("<b>");
            desc.append(EventUtil.getPriceString(price));
            desc.append("</b>");
        }

        if (date != null) {
            desc.append(" on ");
            desc.append("<b>");
            desc.append(date);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Event.FIELD_PRICE.equals(sortBy)) {
            return context.getString(R.string.events_sorted_by_price);
        } else if (Event.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.events_sorted_by_popularity);
        } else if (Event.FIELD_AVG_RATING.equals(sortBy)) {
            return context.getString(R.string.events_sorted_by_rating);
        } else {
            return context.getString(R.string.events_sorted_by_date);
        }
    }
}
