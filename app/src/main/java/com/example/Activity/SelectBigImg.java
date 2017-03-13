package com.example.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.smsdemo.R;
import com.example.util.MyDialog;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 点击查看大图
 */
public class SelectBigImg extends Activity {
    private ImageView myImgview;
    private String url;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_big_img);
        myImgview = (ImageView) findViewById(R.id.select_img); //获取img

        final MyDialog dialog = new MyDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //两秒后关闭doalog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                //设置图片为bitmap
                Intent intent = getIntent();
                if (intent != null) {
                    url = intent.getStringExtra("url");
                    myThread thread = new myThread();
                    thread.start();
                    while (bitmap == null) {
                    }
                    Log.e("test", "bitmap !=null");
                    myImgview.setImageBitmap(bitmap);
                }
            }
        }, 1000 * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;
            int length = http.getContentLength();
            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    class myThread extends Thread {
        @Override
        public void run() {
            bitmap = getBitmap(url);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // 设置想要的大小
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
            int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
            int newWidth = screenWidth;
            int newHeight = screenHeight;
            // 计算缩放比例
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
            super.run();
        }
    }
}
