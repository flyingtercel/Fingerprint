package us.mifeng.fingerprint;

import android.content.Context;
import android.widget.Toast;

public class ToastU {
    private static Toast mToast;
    public static void init(Context context){
        mToast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
    }
    public static void show(String text){

        mToast.setText(text);
        mToast.show();

    }
}
