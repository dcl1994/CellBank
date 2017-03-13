package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.util.getcellutil;
import com.example.view.ArcMenu;
import com.sharesdk.common.mobscreenshotcommon.ScreenShotListenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ui.ThumbnailLayout;

public class StemCellsActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private ThumbnailLayout thumbLayout;
    private ImageView iconImg;
    private ContentLoadingProgressBar progress;
    private TextView shareImage;
    String imageUri = "";
    ScreenShotListenManager manager;
    private Bitmap bitmap;
    private BitmapFactory.Options options;

    private StemCellsAsynctask stemCellsAsynctask;  //我的细胞的异步处理

    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private String Phone;
    private LinearLayout myllayout01, myllayout02, myllayout03, myllayout04;
    private TextView mTextview;
    private ArcMenu mArcMenu;
    private TextView my_yedan_text1, my_yedan_text2, my_qidan_text3, my_qidan_text4, my_yp_temperature, my_gttop_temperature, mycellname, mynumber, mysavewhere, myjl_time, mysave_time, myliquid_level_text, myliquid_level_low, myliquid_level_high, mytemperature_low, mytemperature_high;
    accessThread mythread1;
    //底部液氮1,2温度,底部气氮3,4温度,样品温度，顶部罐体温度
    private String 正常, 不正常;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stem_cells);
        mContext = StemCellsActivity.this;
        //返回按钮的点击事件
        mTextview = (TextView) findViewById(R.id.text_back);
        mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mythread1.setA(false);
                finish();
            }
        });
        /**
         * 初始化ShareSDK
         */
        ShareSDK.initSDK(this);
        init();
        initEvent();
        initScreenShotListener();

    }

    /**
     * 对截取的图片进行处理
     */
    private void initScreenShotListener() {
        manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(
                new ScreenShotListenManager.OnScreenShotListener() {
                    public void onShot(String imagePath) {
                        progress.setVisibility(View.VISIBLE);
                        setImageView(imagePath);
                    }
                }
        );
    }

    private void setImageView(final String file) {
        imageUri = file;
        if (thumbLayout != null && (thumbLayout.getVisibility() == View.GONE)) {
            thumbLayout.setVisibility(View.VISIBLE);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(file, options);
                if (bitmap != null) {
                    iconImg.setImageBitmap(bitmap);
                    progress.setVisibility(View.GONE);
                }
            }
        });

    }


    private void initEvent() {
        mArcMenu.setOnMenuItemClickListener(new ArcMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                switch (pos) {
                    case 1://状态
                        myllayout01.setVisibility(View.VISIBLE);
                        myllayout04.setVisibility(View.GONE);
                        myllayout03.setVisibility(View.GONE);
                        myllayout02.setVisibility(View.GONE);
                        Toast.makeText(StemCellsActivity.this, "" + view.getTag(), Toast.LENGTH_SHORT).show();
                        break;
                    case 2://基本资料
                        myllayout02.setVisibility(View.VISIBLE);
                        myllayout04.setVisibility(View.GONE);
                        myllayout03.setVisibility(View.GONE);
                        myllayout01.setVisibility(View.GONE);
                        Toast.makeText(StemCellsActivity.this, "" + view.getTag(), Toast.LENGTH_SHORT).show();
                        break;
                    case 3://温度
                        myllayout03.setVisibility(View.VISIBLE);
                        myllayout02.setVisibility(View.GONE);
                        myllayout01.setVisibility(View.GONE);
                        myllayout04.setVisibility(View.GONE);
                        Toast.makeText(StemCellsActivity.this, "" + view.getTag(), Toast.LENGTH_SHORT).show();
                        break;
                    case 4://液位
                        myllayout04.setVisibility(View.VISIBLE);
                        myllayout03.setVisibility(View.GONE);
                        myllayout02.setVisibility(View.GONE);
                        myllayout01.setVisibility(View.GONE);
                        Toast.makeText(StemCellsActivity.this, "" + view.getTag(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    //初始化view
    private void init() {
        thumbLayout = (ThumbnailLayout) findViewById(R.id.thumbLayout);
        thumbLayout.setAutoHide(true);//设置自动消失。
        iconImg = (ImageView) findViewById(R.id.thumb);
        progress = (ContentLoadingProgressBar) findViewById(R.id.startShow);
        progress.setVisibility(View.GONE);
        shareImage = (TextView) findViewById(R.id.shareFriends);
        shareImage.setOnClickListener(this);


        my_yedan_text1 = (TextView) findViewById(R.id.yedan_text1);
        my_yedan_text2 = (TextView) findViewById(R.id.yedan_text2);
        my_qidan_text3 = (TextView) findViewById(R.id.qidan_text3);
        my_qidan_text4 = (TextView) findViewById(R.id.qidan_text4);

        my_yp_temperature = (TextView) findViewById(R.id.yp_temperature);
        my_gttop_temperature = (TextView) findViewById(R.id.gttop_temperature);

        mycellname = (TextView) findViewById(R.id.cellname);
        mynumber = (TextView) findViewById(R.id.number);
        mysavewhere = (TextView) findViewById(R.id.savewhere);
        myjl_time = (TextView) findViewById(R.id.jl_time);
        mysave_time = (TextView) findViewById(R.id.save_time);

        myliquid_level_text = (TextView) findViewById(R.id.liquid_level_text);

        myliquid_level_low = (TextView) findViewById(R.id.liquid_level_low);
        myliquid_level_high = (TextView) findViewById(R.id.liquid_level_high);
        mytemperature_low = (TextView) findViewById(R.id.temperature_low);
        mytemperature_high = (TextView) findViewById(R.id.temperature_high);

        //找到自定义view
        mArcMenu = (ArcMenu) findViewById(R.id.id_menu);
        myllayout01 = (LinearLayout) findViewById(R.id.layout_01);
        myllayout02 = (LinearLayout) findViewById(R.id.layout_02);
        myllayout03 = (LinearLayout) findViewById(R.id.layout_03);
        myllayout04 = (LinearLayout) findViewById(R.id.layout_04);
    }

    //可见的时候调用这个方法
    @Override
    protected void onStart() {
        super.onStart();
        //从缓存中读取电话号码
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Phone = preferences.getString("Phone", "");
        Log.d("Phone", Phone);
        mythread1 = new accessThread();
        mythread1.start();

        manager.startListen();
    }

    /**
     * 点击事件的处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareFriends:
                OneKeyShare(imageUri);
                break;
        }

    }
    /**
     * 截屏分享
     *
     * @param imageUrl
     */
    private void OneKeyShare(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            OnekeyShare oks = new OnekeyShare();
            oks.setTitle("万海生命银行");
            oks.setText("截屏分享");
            oks.setImagePath(imageUrl);
            oks.show(this);
        }
    }

    //使用httpclint获取网络返回json
    class accessThread extends Thread {
        private boolean a = true;

        @Override
        public void run() {
            while (a) {
                Log.v("zms", "线程accessThread开始");
                try {
                    stemCellsAsynctask = new StemCellsAsynctask();
                    stemCellsAsynctask.execute();
                    Thread.sleep(1000 * 30);
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public void setA(boolean a) {
            this.a = a;
        }
    }
    /**
     * 将异步与activity进行绑定
     */
    @Override
    protected void onPause() {
        if (stemCellsAsynctask != null && stemCellsAsynctask.getStatus() == AsyncTask.Status.RUNNING) {
            stemCellsAsynctask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 我的干细胞的异步处理
     */
    class StemCellsAsynctask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject = null;
            try {
                String result = getcellutil.getHttpJsonByhttpclient(HttpUtil.StemCellUrl + "?phone=" + Phone);
                jsonObject = new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        /**
         * 解析json
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            try {
                JSONArray jsonArray1 = jsonObject.getJSONArray("JsonArry");
                if (jsonArray1.getJSONObject(0).opt("底部液氮T1").toString().equals("")) {
                    Toast.makeText(StemCellsActivity.this, "您还没有存储任何干细胞哦", Toast.LENGTH_SHORT).show();
                }
                String str1 = jsonArray1.getJSONObject(0).opt("底部液氮T1").toString();
                String str2 = jsonArray1.getJSONObject(0).opt("底部液氮T2").toString();
                String str3 = jsonArray1.getJSONObject(0).opt("底部气氮T3").toString();
                String str4 = jsonArray1.getJSONObject(0).opt("底部气氮T4").toString();
                String str5 = jsonArray1.getJSONObject(0).opt("样品温度").toString();
                String str6 = jsonArray1.getJSONObject(0).opt("罐体顶部温度").toString();
                String str7 = jsonArray1.getJSONObject(0).opt("细胞名称").toString();
                String str8 = jsonArray1.getJSONObject(0).opt("存储数量").toString();
                String str9 = jsonArray1.getJSONObject(0).opt("存放地点").toString();
                String str10 = jsonArray1.getJSONObject(0).opt("记录时间").toString();
                String str11 = jsonArray1.getJSONObject(0).opt("存储时间").toString();
                String str12 = jsonArray1.getJSONObject(0).opt("液氮罐液位").toString();
                String str13 = jsonArray1.getJSONObject(0).opt("液位低报警").toString();  //0表示正常，1表示不正常
                String str14 = jsonArray1.getJSONObject(0).opt("液位高报警").toString();
                String str15 = jsonArray1.getJSONObject(0).opt("罐内温度高报警").toString();
                String str16 = jsonArray1.getJSONObject(0).opt("罐内温度超高报警").toString();
                my_yedan_text1.setText("底部液氮T1:" + str1 + "℃");
                my_yedan_text2.setText("底部液氮T2:" + str2 + "℃");
                my_qidan_text3.setText("底部气氮T3:" + str3 + "℃");
                my_qidan_text4.setText("底部气氮T4:" + str4 + "℃");
                my_yp_temperature.setText("样品温度:" + str5 + "℃");
                my_gttop_temperature.setText("罐体顶部温度:" + str6 + "℃");


                mycellname.setText("细胞名称:" + str7);
                mynumber.setText("存储数量:" + str8);
                mysavewhere.setText("存放地点:" + str9);
                myjl_time.setText("记录时间:" + str10);
                mysave_time.setText("存储时间:" + str11);

                myliquid_level_text.setText("液氮罐液位:" + str12 + "㎝");
                Log.e("str13", str13);
                if (str13.equals("0") || str13.equals("")) {
                    str13 = "正常";
                    myliquid_level_low.setTextColor(getResources().getColor(R.color.green));
                } else {
                    str13 = "报警";
                    myliquid_level_low.setTextColor(getResources().getColor(R.color.red));
                }
                if (str14.equals("0") || str14.equals("")) {
                    str14 = "正常";
                    myliquid_level_high.setTextColor(getResources().getColor(R.color.green));
                } else {
                    str14 = "报警";
                    myliquid_level_high.setTextColor(getResources().getColor(R.color.red));
                }
                if (str15.equals("0") || str15.equals("")) {
                    str15 = "正常";
                    mytemperature_low.setTextColor(getResources().getColor(R.color.green));
                } else {
                    str15 = "报警";
                    mytemperature_low.setTextColor(getResources().getColor(R.color.red));
                }
                if (str16.equals("0") || str16.equals("")) {
                    str16 = "正常";
                    mytemperature_high.setTextColor(getResources().getColor(R.color.green));

                } else {
                    str16 = "报警";
                    mytemperature_high.setTextColor(getResources().getColor(R.color.red));
                }
                myliquid_level_low.setText("液位低报警:" + str13);
                myliquid_level_high.setText("液位高报警:" + str14);
                mytemperature_low.setText("罐内温度高报警:" + str15);
                mytemperature_high.setText("罐内温度超高报警:" + str16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

}
