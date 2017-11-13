package co.wasder.wasder.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Ahmed AlAskalany on 10/15/2017.
 * Navigator
 */
@Keep
public class NonSwipeableViewPager extends ViewPager {

    public NonSwipeableViewPager(@NonNull final Context context) {
        super(context);
        setMyScroller();
    }

    public NonSwipeableViewPager(@NonNull final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    //down one is added for smooth scrolling

    public void setMyScroller() {
        try {
            final Class<?> viewpager = ViewPager.class;
            final Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (@NonNull final Exception e) {
            e.printStackTrace();
        }
    }

    public static class MyScroller extends Scroller {

        public MyScroller(final Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(final int startX, final int startY, final int dx, final int dy, final int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}
