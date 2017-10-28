package co.wasder.wasder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

@IgnoreExtraProperties
public class Event {

    @SuppressWarnings("unused")
    public static final String FIELD_CITY = "city";
    @SuppressWarnings("unused")
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";
    public static final String FIELD_DATE = "date";

    private String uId;
    private String userName;
    private String eventName;
    private String city;
    private String category;
    private String photo;
    private int price;
    private int numRatings;
    private double avgRating;
    private String date;

    public Event() {
    }

    public Event(String uId, String userName, String eventName, String city, String category,
                 String photo, int price, int numRatings, double avgRating) {
        this.uId = uId;
        this.userName = userName;
        this.eventName = eventName;
        this.city = city;
        this.category = category;
        this.photo = photo;
        this.price = price;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @SuppressWarnings("unused")
    public String getUid() {
        return uId;
    }

    public void setUid(String uId) {
        this.uId = uId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getDate() {
        return date;
    }

    @SuppressWarnings("SameParameterValue")
    public void setDate(String date) {
        this.date = date;
    }
}
