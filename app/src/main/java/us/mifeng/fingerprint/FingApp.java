package us.mifeng.fingerprint;

import android.app.Application;
import android.widget.Toast;

public class FingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ToastU.init(this);
    }
}
