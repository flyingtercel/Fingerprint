package us.mifeng.biometricslib;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

public class BiometricPromptManager {

    private static final String TAG = "Biomeric";
    private IBiometricPrompt mBiometricImpl;
    private Activity activity;

    public interface OnBiometricIdentifyCallback{

        void onUsePassword();

        void onSucceeded();

        void onFailed();

        void onError(int code, String reason);

        void onCancel();

    }

    public static BiometricPromptManager from(Activity activity){
        return new BiometricPromptManager(activity);
    }

    private BiometricPromptManager(Activity activity) {
        this.activity = activity;
        if (isAboveApi28()){
            mBiometricImpl = new BiometricPromptApi28(activity);
        }else if (isAboveApi23()){
            mBiometricImpl = new BiometricPromptApi23(activity);
        }
    }

    private boolean isAboveApi28(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }
    private boolean isAboveApi23(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void authenticate(@NonNull OnBiometricIdentifyCallback callback){
        mBiometricImpl.authenticate(null,callback);
    }
    public void authenticate(@NonNull CancellationSignal cancel,
                             @NonNull OnBiometricIdentifyCallback callback) {
        mBiometricImpl.authenticate(cancel, callback);
    }

    public boolean hasEnrolledFingerprints(){

        if (isAboveApi28()){
            //这种判断方式还是基于api23的判断方式
            return FingerprintManagerCompat.from(activity).hasEnrolledFingerprints();
        }else if (isAboveApi23()){
            return ((BiometricPromptApi23)mBiometricImpl).hasEnrolledFingerprints();
        }
        return false;
    }

    public boolean isHardwareDetected(){
        if (isAboveApi28()){
            return FingerprintManagerCompat.from(activity).isHardwareDetected();
        }else if (isAboveApi23()){
            return ((BiometricPromptApi23)mBiometricImpl).isHardwareDetected();
        }
        return false;
    }

    public boolean isKeyguardSecure(){
        KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
        if (null != keyguardManager){
           return keyguardManager.isKeyguardSecure();
        }
        return false;
    }

    public boolean isBiometricPromptEnable(){
        return isAboveApi23()
                && isHardwareDetected()
                && hasEnrolledFingerprints()
                && isKeyguardSecure();
    }

}
