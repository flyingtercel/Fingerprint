package us.mifeng.biometricslib;

import android.app.Activity;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(Build.VERSION_CODES.M)
public class BiometricPromptApi23 implements IBiometricPrompt{

    private static final String TAG = "Biometric";
    private Activity activity;
    private BiometricPromptDialog mDialog;
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private BiometricPromptManager.OnBiometricIdentifyCallback mIdentifyCallback;
    private FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManageCallbackImpl();

    public BiometricPromptApi23(Activity activity) {
        this.activity = activity;
        mFingerprintManager = getFingerprintManager(activity);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void authenticate(@NonNull CancellationSignal cancel, @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback) {

        mIdentifyCallback = callback;
        mDialog = BiometricPromptDialog.newInstance();
        mDialog.setOnBiometricPromptDialogActionCallback(new BiometricPromptDialog.OnBiometricPromptDialogActionCallback() {
            @Override
            public void onDialogDismiss() {
                //当dialog消失的时候，包括点击userPassword、点击cancel、和识别成功之后
                if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
                    mCancellationSignal.cancel();
                }
            }

            @Override
            public void onUsePassword() {
                //一些情况下，用户还可以选择使用密码
                if (mIdentifyCallback != null) {
                    mIdentifyCallback.onUsePassword();
                }
            }

            @Override
            public void onCancel() {
                //点击cancel键
                if (mIdentifyCallback != null) {
                    mIdentifyCallback.onCancel();
                }
            }
        });
        mDialog.show(activity.getFragmentManager(),"BiometricPromptApi23");

        mCancellationSignal = cancel;
        if (mCancellationSignal == null) {
            mCancellationSignal = new CancellationSignal();
        }
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                mDialog.dismiss();
            }
        });

        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            getFingerprintManager(activity).authenticate(
                    cryptoObjectHelper.buildCryptoObject(), mCancellationSignal,
                    0, mAuthenticationCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class FingerprintManageCallbackImpl extends FingerprintManager.AuthenticationCallback {

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Log.d(TAG, "onAuthenticationError() called with: errorCode = [" + errorCode + "], errString = [" + errString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_ERROR);
            mIdentifyCallback.onError(errorCode, errString.toString());
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Log.d(TAG, "onAuthenticationFailed() called");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mIdentifyCallback.onFailed();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Log.d(TAG, "onAuthenticationHelp() called with: helpCode = [" + helpCode + "], helpString = [" + helpString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mIdentifyCallback.onFailed();

        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.i(TAG, "onAuthenticationSucceeded: ");
            mDialog.setState(BiometricPromptDialog.STATE_SUCCEED);
            mIdentifyCallback.onSucceeded();

        }
    }

    private FingerprintManager getFingerprintManager(Context context) {
        if (mFingerprintManager == null) {
            mFingerprintManager = context.getSystemService(FingerprintManager.class);
        }
        return mFingerprintManager;
    }

    public boolean isHardwareDetected() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.isHardwareDetected();
        }
        return false;
    }

    public boolean hasEnrolledFingerprints() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }
}
