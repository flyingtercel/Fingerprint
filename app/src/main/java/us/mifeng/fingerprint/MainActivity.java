package us.mifeng.fingerprint;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import us.mifeng.biometricslib.BiometricPromptManager;

public class MainActivity extends AppCompatActivity {

    private TextView mText;
    private Button mButton;
    private BiometricPromptManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = findViewById(R.id.mText);
        mButton = findViewById(R.id.button);

        mManager = BiometricPromptManager.from(this);
        StringBuilder sbt = new StringBuilder();
        sbt.append("SDK VERSION IS" + Build.VERSION.SDK_INT);
        sbt.append("\n");
        sbt.append("isHardwareDetected" + mManager.isHardwareDetected());
        sbt.append("\n");
        sbt.append("hasEnrolledFingerprints" + mManager.hasEnrolledFingerprints());
        sbt.append("\n");
        sbt.append("isKeyguardSecure" + mManager.isKeyguardSecure());

        mText.setText(sbt.toString());

        mButton.setOnClickListener(v -> {
            if (mManager.isBiometricPromptEnable()) {
                mManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                    @Override
                    public void onUsePassword() {
                        ToastU.show("onUsePassword");
                    }

                    @Override
                    public void onSucceeded() {
                        ToastU.show("onSucceeded");
                    }

                    @Override
                    public void onFailed() {
                        ToastU.show("onFailed");
                    }

                    @Override
                    public void onError(int code, String reason) {
                        ToastU.show("onError");
                    }

                    @Override
                    public void onCancel() {
                        ToastU.show("onCancel");
                    }
                });
            }
        });
    }

}
