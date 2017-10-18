package com.wasder.wasder.jobservices;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wasder.wasder.R;

import java.util.Map;


/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */

public class FirestoreQueryJobService extends JobService {

    private Thread mThread;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = getApplicationContext();
                String appName = context.getString(R.string.app_name);
                try {
                    //This task takes 2 seconds to complete.
                    //Thread.sleep(2000);
                    Log.d("FirestoreJobService", appName);
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    Query query = firestore.collection("restaurants");
                    //query.whereEqualTo("category", "Mexican");
                    query.limit(50);
                    query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots,
                                            FirebaseFirestoreException e) {
                            for (DocumentSnapshot snapshot : documentSnapshots) {
                                Map<String, Object> data = snapshot.getData();
                                Log.d("OnStartJob", "onEvent: " + data);
                            }
                        }
                    });
                } finally {
                    //Tell the framework that the job has completed and doesnot needs to be
                    // reschedule
                    jobFinished(jobParameters, false);
                }
            }
        });
        mThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        return true;
    }
}
