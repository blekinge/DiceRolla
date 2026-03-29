package dk.blekinge.dicerolla;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class MainActivityTest {


    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void ensureFirstPageIsVisible() throws Exception {

        Matcher<View> viewMatcher = withText("D6");
        ViewInteraction viewInteraction = onView(viewMatcher);
        Matcher<View> displayed = isDisplayed();
        ViewAssertion matches = matches(displayed);
        viewInteraction.check(matches);
    }

    @Test
    public void clickTest() throws Exception {

        onView(withId(R.id.editText)).perform(typeText("33"));
        onView(withId(R.id.button)).perform(click());

        onView(withContentDescription("buckets")).check(matches(isDisplayed()));
        onView(withId(R.id.Buckets)).check(matches(isDisplayed()));
        onView(withId(R.id.Status)).check(matches(withText("Dice pool: 33, selected dice: 0")));
    }


    @Test
    public void rerollTest() throws Exception {

        onView(withId(R.id.editText))
                .perform(typeText("33"));
        onView(withId(R.id.button))
                .perform(click());

        onView(withContentDescription("buckets"))
                .check(matches(isDisplayed()));
        onView(withId(R.id.Buckets))
                .check(matches(isDisplayed()));
        onView(withId(R.id.Status))
                .check(matches(withText("Dice pool: 33, selected dice: 0")));

        onView(withId(D6.R1.imageButtonId))
                .perform(click());
        onView(withId(R.id.Reroll_button))
                .check(matches(isClickable()))
                .perform(click());

    }

}