package com.wasder.wasder.Util;

import android.content.Context;

import com.wasder.wasder.R;
import com.wasder.wasder.model.Event;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class EventUtil {

    private static final String TAG = "EventUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60, TimeUnit
            .SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String EVENT_URL_FMT = "https://storage.googleapis" + "" +
            ".com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {"Foo", "Bar", "Baz", "Qux", "Fire",
            "Sam's", "World Famous", "Google", "The Best",};

    private static final String[] NAME_SECOND_WORDS = {"Event", "Cafe", "Spot", "Eatin' " +
            "Place", "Eatery", "Drive Thru", "Diner",};


    /**
     * Create a random Event POJO.
     */
    public static Event getRandom(Context context) {
        Event event = new Event();
        Random random = new Random();

        // Cities (first elemnt is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories_events);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        event.setName(getRandomName(random));
        event.setCity(getRandomString(cities, random));
        event.setCategory(getRandomString(categories, random));
        event.setPhoto(getRandomImageUrl(random));
        event.setPrice(getRandomInt(prices, random));
        event.setAvgRating(getRandomRating(random));
        event.setNumRatings(random.nextInt(20));
        event.setDate("2017/10/28");

        return event;
    }


    /**
     * Get a random image.
     */
    public static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), EVENT_URL_FMT, id);
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(Event event) {
        return getPriceString(event.getPrice());
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }

    public static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    public static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " " + getRandomString
                (NAME_SECOND_WORDS, random);
    }

    public static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    public static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }
}
