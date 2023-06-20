package com.example.suculentphoto.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    public static void showToastCorto(Context context, String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
    }
}
