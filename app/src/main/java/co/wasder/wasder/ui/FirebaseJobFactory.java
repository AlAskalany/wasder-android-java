package co.wasder.wasder.ui;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import co.wasder.wasder.data.FirestoreQueryJobService;

/** Created by Ahmed AlAskalany on 10/21/2017. Navigator */
@Keep
public class FirebaseJobFactory {

    public static Job createJob(@NonNull final FirebaseJobDispatcher dispatcher) {
        return dispatcher
                .newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // Call this service when the criteria are met.
                .setService(FirestoreQueryJobService.class)
                // unique id of the task
                .setTag("OneTimeJob")
                // We are mentioning that the job is not periodic.
                .setRecurring(false)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(0, 60))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
    }

    public static void scheduleJob(final Context context) {
        final FirebaseJobDispatcher dispatcher =
                new FirebaseJobDispatcher(new GooglePlayDriver(context));
        final Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }
}
