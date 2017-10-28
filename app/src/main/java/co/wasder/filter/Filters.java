package co.wasder.filter;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class Filters {

    public static EventsFilters EventsFilters() {
        return new EventsFilters();
    }

    public static PostsFilters PostsFilters() {
        return new PostsFilters();
    }
}
