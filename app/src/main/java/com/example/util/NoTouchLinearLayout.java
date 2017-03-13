package com.example.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by szjdj on 2016-10-18.
 */
public class NoTouchLinearLayout extends LinearLayout {


    public NoTouchLinearLayout(Context context) {

        super(context);
    }

    public NoTouchLinearLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public NoTouchLinearLayout(Context context, AttributeSet attrs, int defStyle) {


        super(context, attrs, defStyle);
    }


    private OnResizeListener mListener;

    public interface OnResizeListener {
        void OnResize();
    }

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            mListener.OnResize();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
