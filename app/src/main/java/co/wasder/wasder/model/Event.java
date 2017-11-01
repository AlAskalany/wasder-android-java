package co.wasder.wasder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 11/1/2017.
 * Navigator
 */
@IgnoreExtraProperties
public class Event {

    private String uId;
    private String title;
    private String description;
    private String eventImageUrl;
    private Date eventDate;
    private @ServerTimestamp
    Date creationDate;

    public Event() {
    }

    public Event(String uId, String title, String description, String eventImageUrl, Date
            eventDate) {
        this.uId = uId;
        this.title = title;
        this.description = description;
        this.eventImageUrl = eventImageUrl;
        this.eventDate = eventDate;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
