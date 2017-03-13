package com.example.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.example.smsdemo.R;
/**
 * Created by szjdj on 2016-11-25.
 * 这个是登录的dialog，自定义弹出框的样式
 */
public class MyDialog extends Dialog {
    public MyDialog(Context context) {
        super(context,  R.style.ImageloadingDialogStyle);
        setOwnerActivity((Activity) context);// 设置dialog全屏显示
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_imageloading);
    }
}
