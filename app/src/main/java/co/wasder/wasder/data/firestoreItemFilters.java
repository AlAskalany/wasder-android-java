package co.wasder.wasder.data;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.firestore.Query;

/** Created by Ahmed AlAskalany on 10/11/2017. Wasder AB */
@Keep
public class firestoreItemFilters {

    @Nullable private String category = null;
    @Nullable private String city = null;
    private int price = -1;
    @Nullable private String sortBy = null;
    @Nullable private Query.Direction sortDirection = null;
    private String uid;

    public firestoreItemFilters() {}

    @NonNull
    public static firestoreItemFilters getDefault() {
        final firestoreItemFilters firestoreItemFilters = new firestoreItemFilters();
        firestoreItemFilters.setSortBy("timestamp");
        firestoreItemFilters.setSortDirection(Query.Direction.DESCENDING);

        return firestoreItemFilters;
    }

    public boolean hasUid() {
        return !(TextUtils.isEmpty(uid));
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

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    @Nullable
    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    @Nullable
    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    @Nullable
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }

    @Nullable
    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(final Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    @NonNull
    public String getSearchDescription(@NonNull final Context context, int resIdAllItem) {
        final StringBuilder desc = new StringBuilder();

        if (uid != null) {
            desc.append("<b>");
            desc.append(uid);
            desc.append("</b>");
        }

        if (category == null && city == null) {
            desc.append("<b>");
            desc.append(context.getString(resIdAllItem));
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

        return desc.toString();
    }

    @NonNull
    public String getOrderDescription(
            @NonNull final Context context, int resId, int resIdPopularity, int resIdRating) {
        if ("price".equals(sortBy)) {
            return context.getString(resId);
        } else if ("numRatings".equals(sortBy)) {
            return context.getString(resIdPopularity);
        } else {
            return context.getString(resIdRating);
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }
}
