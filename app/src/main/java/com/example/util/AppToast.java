package com.example.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by szjdj on 2016-10-13.
 */
public class AppToast {
    protected static final String TAG = "AppToast";
    public static Toast toast;
    /**
     * 信息提示
     *
     * @param context
     * @param content
     */
    public static void makeToast(Context context, String content) {
        if(context==null)return;
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShortText(Context context, int resId) {
        try {
            if(context==null)return;
            if(toast != null)
                toast.cancel();
            toast = Toast.makeText(context, context.getString(resId),Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            AppLog.e(TAG,e.getMessage());
        }
    }

}
