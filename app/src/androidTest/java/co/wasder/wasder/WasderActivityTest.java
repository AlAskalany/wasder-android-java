package co.wasder.wasder;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WasderActivityTest {

    @Rule
    public ActivityTestRule<WasderActivity> mActivityTestRule = new ActivityTestRule<>
            (WasderActivity.class);

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int
            position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view
                        .equals(((ViewGroup) parent)
                        .getChildAt(position));
            }
        };
    }

    @Test
    public void wasderActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText = onView(allOf(withId(R.id.email), childAtPosition
                (childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(allOf(withId(R.id.email), childAtPosition
                (childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
        textInputEditText2.perform(replaceText("ahmed"), closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText3 = onView(allOf(withId(R.id.email), withText("ahmed"),
                childAtPosition(childAtPosition(withId(R.id.email_layout), 0), 0), isDisplayed()));
        textInputEditText3.perform(replaceText("ahmed.alaskalany@gmail.com"));

        ViewInteraction textInputEditText4 = onView(allOf(withId(R.id.email), withText("ahmed" +
                ".alaskalany@gmail.com"), childAtPosition(childAtPosition(withId(R.id
                .email_layout), 0), 0), isDisplayed()));
        textInputEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(allOf(withId(R.id.button_next), withText("Next")
                , childAtPosition(childAtPosition(withId(R.id.fragment_register_email), 0), 1),
                isDisplayed()));
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(3422155);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textInputEditText5 = onView(allOf(withId(R.id.password), childAtPosition
                (childAtPosition(withId(R.id.password_layout), 0), 0), isDisplayed()));
        textInputEditText5.perform(replaceText("Nader-2004"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.button_done), withText("Sign " +
                "" + "in"), childAtPosition(childAtPosition(withClassName(is("android.widget" + "" +
                ".LinearLayout")), 3), 1)));
        appCompatButton2.perform(scrollTo(), click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(3551585);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recyclerView), childAtPosition
                (withId(R.id.fragment_tab_home_feed_frameLayout), 0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(3577298);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageView = onView(allOf(withId(R.id.post_button_back), childAtPosition(allOf(withId(R.id.item_top_card), childAtPosition(withClassName
                        (is("android.widget.RelativeLayout")), 0)), 2), isDisplayed()));
        appCompatImageView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource
        // /index.html
        try {
            Thread.sleep(3591362);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*ViewInteraction cardView = onView(allOf(withId(R.id.filter_bar), childAtPosition(allOf
                (withId(R.id.filter_bar_container), childAtPosition(withId(R.id.toolbar), 0)), 0)
                , isDisplayed()));
        cardView.perform(click());*/

        ViewInteraction appCompatButton3 = onView(allOf(withId(R.id.button_cancel), withText
                ("Cancel"), childAtPosition(childAtPosition(withId(R.id.filters_form), 5), 1),
                isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction bottomNavigationItemView = onView(allOf(withId(R.id.navigation_live),
                childAtPosition(childAtPosition(withId(R.id.navigation2), 0), 1), isDisplayed()));
        bottomNavigationItemView.perform(click());

    }
}
