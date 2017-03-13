package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.example.smsdemo.R;
import com.hyphenate.chat.EMClient;

public class AppStart extends Activity {
    private Context mContext;
    private final String PREFERENCES_NAME = "userinfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.activity_app_start, null);
        setContentView(view);
        mContext = AppStart.this;
        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            /**
             * 渐变动画结束时调用redirectTo方法，跳转到登录界面
             * @param arg0
             */
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
    }
    @Override
    protected void onStart() {
        //取得当前时间，存入缓存
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.getString("Phone", "").equals("")) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
        EMClient.getInstance().chatManager().loadAllConversations();
        EMClient.getInstance().groupManager().loadAllGroups();

        super.onStart();
    }


    private void redirectTo() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
