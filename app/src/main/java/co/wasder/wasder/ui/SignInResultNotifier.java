package co.wasder.wasder.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 11/3/2017.
 * Navigator
 */

public class SignInResultNotifier implements OnCompleteListener<AuthResult> {

    private Context mContext;

    public SignInResultNotifier(final Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onComplete(@NonNull final Task<AuthResult> task) {
        if (task.isSuccessful()) {
            Toast.makeText(mContext, R.string.signed_in, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.anonymous_auth_failed_msg, Toast.LENGTH_LONG).show();
        }
    }
}
