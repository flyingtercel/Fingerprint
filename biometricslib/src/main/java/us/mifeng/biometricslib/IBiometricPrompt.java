package us.mifeng.biometricslib;

import android.os.CancellationSignal;
import android.support.annotation.NonNull;

public interface IBiometricPrompt {

    void authenticate(@NonNull CancellationSignal cancel,
                      @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

}
