package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smsdemo.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

public class QrCode extends Activity {
    /**
     * 我的二维码界面，生成二维码
     * @param savedInstanceState
     */
    private Context mContext;
    private TextView mytextback;
    private final String PREFERENCES_NAME = "userinfo";
    private String userid;
    private ImageView myqrcodeimg,myuserimg;
    private TextView myqrcode_username,myqrcode_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        mContext=QrCode.this;
        //拿到用户的userid
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", userid);
        //返回按钮
        mytextback= (TextView) findViewById(R.id.text_back);
        mytextback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myqrcodeimg= (ImageView) findViewById(R.id.qrcodeimg);
        myuserimg= (ImageView) findViewById(R.id.userimg);
        myqrcode_username= (TextView) findViewById(R.id.qrcode_username);
        myqrcode_address= (TextView) findViewById(R.id.qrcode_address);

        Intent intent=getIntent();
        String username= intent.getStringExtra("username");
        String address=intent.getStringExtra("address");
        String url=intent.getStringExtra("url");
        if (intent!=null) {
           // ImageLoad.loadImg(url,myuserimg);

            Glide.with(mContext).load(url).into(myuserimg);

            myqrcode_username.setText(username);
            myqrcode_address.setText(address);
        }else {
           myuserimg.setImageResource(R.drawable.user_top);
        }
    }

    /**
     * 生成二维码
     */
    @Override
    protected void onStart() {
        String input="http://www.pcbiot.com?userid="+userid;
        Bitmap bitmap = EncodingUtils.createQRCode(input, 500, 500, BitmapFactory.decodeResource(getResources(), R.drawable.lifebank));
        myqrcodeimg.setImageBitmap(bitmap);
        super.onStart();
    }
}
