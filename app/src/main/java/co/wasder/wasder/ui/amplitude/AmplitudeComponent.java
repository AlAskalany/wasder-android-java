package co.wasder.wasder.ui.amplitude;

import android.support.v7.app.AppCompatActivity;

import com.amplitude.api.Amplitude;

public class AmplitudeComponent {
    private static final java.lang.String AMPLITUDE_API_KEY = "937ae55b73eb164890021fe9b2d4fa63";

    public AmplitudeComponent() {}

    public void setupAmplitude(String userId, AppCompatActivity activity) {
        Amplitude.getInstance()
                .initialize(activity, AMPLITUDE_API_KEY)
                .enableForegroundTracking(activity.getApplication())
                .setUserId(userId);
        Amplitude.getInstance().trackSessionEvents(true);
    }
}
