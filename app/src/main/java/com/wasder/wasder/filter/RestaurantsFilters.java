package com.wasder.wasder.filter;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.firestore.Query;
import com.wasder.wasder.R;
import com.wasder.wasder.Util.RestaurantUtil;
import com.wasder.wasder.model.Restaurant;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class RestaurantsFilters {

    private String category = null;
    private String city = null;
    private int price = -1;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public RestaurantsFilters() {
    }

    public static RestaurantsFilters getDefault() {
        RestaurantsFilters restaurantsFilters = new RestaurantsFilters();
        restaurantsFilters.setSortBy(Restaurant.FIELD_AVG_RATING);
        restaurantsFilters.setSortDirection(Query.Direction.DESCENDING);

        return restaurantsFilters;
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
            desc.append(context.getString(R.string.all_restaurants));
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
            desc.append(RestaurantUtil.getPriceString(price));
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Restaurant.FIELD_PRICE.equals(sortBy)) {
            return context.getString(R.string.restaurants_sorted_by_price);
        } else if (Restaurant.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.restaurants_sorted_by_popularity);
        } else {
            return context.getString(R.string.restaurants_sorted_by_rating);
        }
    }
}
