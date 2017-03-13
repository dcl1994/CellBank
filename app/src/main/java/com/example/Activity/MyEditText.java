package com.example.Activity;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.smsdemo.R;

/**新建一个Edittext样式
 *
 * Created by szjdj on 2016-09-23.
 */
public class MyEditText extends EditText {
    private Context context;    //定义一个上下文对象
    private Drawable delete,phone;    //删除图标

    public MyEditText(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context=context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.context=context;
        init();
    }
    //初始化事件
    private void init() {
        //获取user图标
        phone=context.getResources().getDrawable(R.drawable.phone);
        setCompoundDrawablesWithIntrinsicBounds(phone,null,null,null);
        //通过上下文获取删除图标
        delete=context.getResources().getDrawable(R.drawable.delete);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            //在文本更改完成后
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0){
                    //如果有内容的话就将删除图标设置在右边        左上右下
                    setCompoundDrawablesWithIntrinsicBounds(phone,null,delete,null);
                }else{
                    //没有就将图标隐藏
                    setCompoundDrawablesWithIntrinsicBounds(phone,null,null,null);
                }
            }
        });
    }
    //实现删除功能
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP){
            //获得手指触碰的坐标
            int eventx= (int) event.getRawX();
            int eventy= (int) event.getRawY();
            //新建一个矩形选区，表示删除图标的范围
            Rect rect = new Rect();
            //获得EditText在屏幕中所处的矩形区域，赋给rect
            getGlobalVisibleRect(rect);
            //将该rect的右边缘减去60，赋给左边缘，此时rect的区域缩小成了删除图标所在的区域
            rect.left=rect.right-80;
            //如果触碰的点属于rect(即删除图标所在的范围)就清空EditText内容
            if (rect.contains(eventx,eventy)){
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}
