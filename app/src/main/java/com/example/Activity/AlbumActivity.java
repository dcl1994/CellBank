package com.example.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.Adapter.AlbumGridViewAdapter;
import com.example.smsdemo.R;
import com.example.util.AlbumHelper;
import com.example.util.Bimp;
import com.example.util.BitmapUtil;
import com.example.util.HttpUtil;
import com.example.util.ImageBucket;
import com.example.util.ImageItem;
import com.example.util.PublicWay;
import com.example.util.Res;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个是显示手机里所有图片的界面
 */
public class AlbumActivity extends Activity {
    //显示手机里的所有图片的列表控件
    private GridView gridView;
    //当手机里没有图片时，提示用户没有图片的控件
    private TextView tv;
    //gridView的adapter
    private AlbumGridViewAdapter gridImageAdapter;
    //完成按钮
    private Button okButton;
    // 打开相册按钮按钮
    private Button back;
    // 取消按钮
    private Button cancel;
    private Intent intent;
    // 预览按钮
    private Button preview;
    private Context mContext;
    private ArrayList<ImageItem> dataList;
    private AlbumHelper helper;
    public static List<ImageBucket> contentList;
    public static Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        PublicWay.activityList.add(this);
        mContext = this;
        //注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        bitmap = BitmapFactory.decodeResource(getResources(), Res.getDrawableID("plugin_camera_no_pictures"));
        HttpUtil.isOpenPhoto = true;
        init();
        initListener();
        //这个函数主要用来控制预览和完成按钮的状态
        isShowOkBt();
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
        }
    };
    // 预览按钮的监听
    private class PreviewListener implements View.OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size() > 0) {
                intent.putExtra("position", "1");
                intent.setClass(AlbumActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        }
    }
    //            if (HttpUtil.isTopic==true) {
//                intent.setClass(mContext, PublishActivity.class);
//                startActivity(intent);
//                finish();
//            }else {
//                intent.setClass(mContext, TopicActivity.class);
//                startActivity(intent);
//                finish();
//            }
    /**
     * 完成按钮的点击事件,压缩选中的图片
     */
    private class AlbumSendListener implements View.OnClickListener {
        public void onClick(View v) {
            Bimp.tempFile.clear();
            //获取选择的图片
           // overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            for (int i=0;i<Bimp.tempSelectBitmap.size();i++){   //遍历list
                String path=  Bimp.tempSelectBitmap.get(i).getImagePath();  //拿到图片地址
                Log.e("imagePath",path);
                File file=new File(path);   //将图片地址放到file中
                BitmapFactory bitmapFactory=new BitmapFactory();
                Bitmap bitmap = bitmapFactory.decodeFile(file.toString());
                Bitmap bitmap1 = BitmapUtil.com(bitmap);    //对bitmap进行压缩
                String filename = "/storage/emulated/0/"+i+"_"+"tempFile.JPEG"; //文件保存的地址
                BitmapUtil.saveBitmap2file(bitmap1,filename);
                Bimp.tempFile.add(new File(filename));  //保存到临时文件中
            }
            for(File file :Bimp.tempFile){
                Log.e("tempFile",file.getName());
            }
            finish();
        }
    }
    // 打开相册按钮监听
    private class BackListener implements View.OnClickListener {
        public void onClick(View v) {
            intent.setClass(AlbumActivity.this, ImageFile.class);
            startActivity(intent);
            finish();
        }
    }
    // 取消按钮的监听
    private class CancelListener implements View.OnClickListener {
        public void onClick(View v) {
            Bimp.tempSelectBitmap.clear();
            finish();
        }
    }
    //初始化给一些对象赋值
    private void init() {
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        contentList = helper.getImagesBucketList(false);
        dataList = new ArrayList<ImageItem>();
        for(int i = 0; i<contentList.size(); i++){
            dataList.addAll( contentList.get(i).imageList );
        }
        back = (Button) findViewById(Res.getWidgetID("back"));
        cancel = (Button) findViewById(Res.getWidgetID("cancel"));
        cancel.setOnClickListener(new CancelListener());
        back.setOnClickListener(new BackListener());
        preview = (Button) findViewById(Res.getWidgetID("preview"));
        preview.setOnClickListener(new PreviewListener());
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        gridView = (GridView) findViewById(Res.getWidgetID("myGrid"));
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
                Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        tv = (TextView) findViewById(Res.getWidgetID("myText"));
        gridView.setEmptyView(tv);
        okButton = (Button) findViewById(Res.getWidgetID("ok_button")); //确定按钮
        okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size()
                + "/"+PublicWay.num+")");
    }
    private void initListener() {
        gridImageAdapter
                .setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,Button chooseBt) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
                            toggleButton.setChecked(false);
                            chooseBt.setVisibility(View.GONE);
                            if (!removeOneData(dataList.get(position))) {
                                Toast.makeText(AlbumActivity.this, Res.getString("only_choose_num"),
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        if (isChecked) {
                            chooseBt.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size()
                                    + "/"+PublicWay.num+")");
                        } else {
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            chooseBt.setVisibility(View.GONE);
                            okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
                        }
                        isShowOkBt();
                    }
                });
        okButton.setOnClickListener(new AlbumSendListener());
    }

    private boolean removeOneData(ImageItem imageItem) {
        if (Bimp.tempSelectBitmap.contains(imageItem)) {
            Bimp.tempSelectBitmap.remove(imageItem);
            okButton.setText(Res.getString("finish")+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
            return true;
        }
        return false;
    }

    private void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
            preview.setPressed(true);
            okButton.setPressed(true);
            preview.setClickable(true);
            okButton.setClickable(true);
            okButton.setTextColor(Color.WHITE);
            preview.setTextColor(Color.WHITE);
        } else {
            okButton.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
            preview.setPressed(false);
            preview.setClickable(false);
            okButton.setPressed(false);
            okButton.setClickable(false);
            okButton.setTextColor(Color.parseColor("#E1E0DE"));
            preview.setTextColor(Color.parseColor("#E1E0DE"));
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent.setClass(AlbumActivity.this, ImageFile.class);
            startActivity(intent);
        }
        return false;
    }
    @Override
    protected void onRestart() {
        isShowOkBt();
        super.onRestart();
    }
}
