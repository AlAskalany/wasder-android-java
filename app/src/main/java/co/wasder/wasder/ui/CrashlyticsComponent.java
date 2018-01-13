package co.wasder.wasder.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import co.wasder.wasder.R;

class CrashlyticsComponent {

    CrashlyticsComponent() {
    }

    void setUpCrashButton(AppCompatActivity activity, boolean enableCrashButton) {
        if (enableCrashButton) {
            final Button crashButton = new Button(null);
            crashButton.setText(R.string.crash);
            crashButton.setOnClickListener(
                    view -> {
                        Crashlytics.getInstance().crash(); // Force a crash
                    });
            activity.addContentView(
                    crashButton,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}
