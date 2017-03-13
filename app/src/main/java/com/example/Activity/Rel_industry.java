package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.PublicWay;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 详细内容页面
 */

public class Rel_industry extends Activity implements PlatformActionListener, View.OnClickListener {
    private WebView mywebview;
    private TextView mytextView;
    private ImageView myshareview;
    private Context mContext;
    private String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rel_industry);
        mContext=Rel_industry.this;
        ShareSDK.initSDK(this);
        init();
    }

    private void init() {
        findViewById(R.id.share).setOnClickListener(this);

        mytextView= (TextView) findViewById(R.id.text_back);
        mywebview= (WebView) findViewById(R.id.webview_idsy);
        Intent intent=getIntent();
        url= intent.getStringExtra("url");
        mywebview.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mywebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        mywebview.setInitialScale(50);
        //支持javascript
        mywebview.getSettings().setJavaScriptEnabled(true);
        //设置可以支持缩放
        mywebview.getSettings().setSupportZoom(true);
        //设置出现缩放工具
        mywebview.getSettings().setBuiltInZoomControls(true);
        //设置扩大比例的缩放
        mywebview.getSettings().setUseWideViewPort(true);
//        //自适应屏幕
//        mywebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        mywebview.getSettings().setLoadWithOverviewMode(true);

        //优先使用缓存
        mywebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        mytextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //重写返回按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i< PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            finish();
        }
        return true;
    }
    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share:
                showShare();
                Toast.makeText(mContext,"正在分享到朋友圈",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    /**
     * 分享
     */
    private void showShare() {
        Platform.ShareParams wechat = new Platform.ShareParams();
        wechat.setTitle("万海生命银行");
        wechat.setText("");
        wechat.setImageUrl("http://120.76.22.150:8080/CellBank/upload/logo.png"); //这里放logo的图标
        wechat.setUrl(url);
        Log.e("url",url);
        wechat.setShareType(Platform.SHARE_WEBPAGE);
        Platform weixin = ShareSDK.getPlatform(mContext, WechatMoments.NAME);
        weixin.setPlatformActionListener(Rel_industry.this);
        weixin.share(wechat);
    }
    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
