package com.example.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.Adapter.AlbumGridViewAdapter;
import com.example.smsdemo.R;
import com.example.util.Bimp;
import com.example.util.ImageItem;
import com.example.util.PublicWay;
import com.example.util.Res;

import java.util.ArrayList;

/**
 * 这个是显示一个文件夹里面的所有图片时的界面
 *
 */
public class ShowAllPhoto extends Activity {
    private GridView gridView;
    private ProgressBar progressBar;
    private AlbumGridViewAdapter gridImageAdapter;
    // 完成按钮
    private Button okButton;
    // 预览按钮
    private Button preview;
    // 返回按钮
    private Button back;
    // 取消按钮
    private Button cancel;
    // 标题
    private TextView headTitle;
    private Intent intent;
    private Context mContext;
    public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_photo);
        PublicWay.activityList.add(this);
        mContext = this;
        back = (Button) findViewById(Res.getWidgetID("showallphoto_back"));
        cancel = (Button) findViewById(Res.getWidgetID("showallphoto_cancel"));
        preview = (Button) findViewById(Res.getWidgetID("showallphoto_preview"));
        okButton = (Button) findViewById(Res.getWidgetID("showallphoto_ok_button"));
        headTitle = (TextView) findViewById(Res.getWidgetID("showallphoto_headtitle"));
        this.intent = getIntent();
        String folderName = intent.getStringExtra("folderName");
        if (folderName.length() > 6) {
            folderName = folderName.substring(0, 6) + "...";
        }
        headTitle.setText(folderName);
        cancel.setOnClickListener(new CancelListener());
        back.setOnClickListener(new BackListener(intent));
        preview.setOnClickListener(new PreviewListener());
        okButton.setOnClickListener(new FinishListener());
        init();
        initListener();
        isShowOkBt();

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            gridImageAdapter.notifyDataSetChanged();
        }
    };
    private class PreviewListener implements View.OnClickListener {
        public void onClick(View v) {
            if (Bimp.tempSelectBitmap.size() > 0) {
                intent.putExtra("position", "2");
                intent.setClass(ShowAllPhoto.this, GalleryActivity.class);  //图片预览界面
                startActivity(intent);
            }
        }

    }
    private class BackListener implements View.OnClickListener {    // 返回按钮监听
        Intent intent;

        public BackListener(Intent intent) {
            this.intent = intent;
        }

        public void onClick(View v) {
            intent.setClass(ShowAllPhoto.this, ImageFile.class);
            startActivity(intent);
        }
    }

    private class CancelListener implements View.OnClickListener {  // 取消按钮的监听
        public void onClick(View v) {
            //清空选择的图片
            Bimp.tempSelectBitmap.clear();
//            intent.setClass(mContext, MainActivity.class);
//            startActivity(intent);
            finish();
        }
    }

    /**
     * 预览相册的页面
     * 完成按钮的点击事件
     */
    private class FinishListener implements View.OnClickListener{   //完成按钮的监听
        @Override
        public void onClick(View v) {
            okButton.setClickable(false);
            overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
            //如果是发表就跳转到publish，否则就跳转到topic
//            intent.setClass(mContext, AlbumActivity.class);
//            startActivity(intent);

//            if (HttpUtil.isTopic==true) {
//                intent.setClass(mContext, PublishActivity.class);
//                startActivity(intent);
//                finish();
//            }else {
//                intent.setClass(mContext, TopicActivity.class);
//                startActivity(intent);
//                finish();
//            }
            finish();
        }
    }


    private void init() {
        IntentFilter filter = new IntentFilter("data.broadcast.action");
        registerReceiver(broadcastReceiver, filter);
        progressBar = (ProgressBar) findViewById(Res.getWidgetID("showallphoto_progressbar"));
        progressBar.setVisibility(View.GONE);
        gridView = (GridView) findViewById(Res.getWidgetID("showallphoto_myGrid"));
        gridImageAdapter = new AlbumGridViewAdapter(this,dataList,Bimp.tempSelectBitmap);
        gridView.setAdapter(gridImageAdapter);
        okButton = (Button) findViewById(Res.getWidgetID("showallphoto_ok_button"));
    }

    private void initListener() {
        gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
                    public void onItemClick(final ToggleButton toggleButton,
                                            int position, boolean isChecked,
                                            Button button) {
                        if (Bimp.tempSelectBitmap.size() >= PublicWay.num && isChecked) {
                            button.setVisibility(View.GONE);
                            toggleButton.setChecked(false);
                            Toast.makeText(ShowAllPhoto.this, Res.getString("only_choose_num"), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        if (isChecked) {
                            button.setVisibility(View.VISIBLE);
                            Bimp.tempSelectBitmap.add(dataList.get(position));
                            okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size()
                                    + "/" + PublicWay.num + ")");
                        } else {
                            button.setVisibility(View.GONE);
                            Bimp.tempSelectBitmap.remove(dataList.get(position));
                            okButton.setText(Res.getString("finish") + "(" + Bimp.tempSelectBitmap.size() + "/" + PublicWay.num + ")");
                        }
                        isShowOkBt();
                    }
                });



//        //完成按钮的点击事件
//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                okButton.setClickable(false);
//                Toast.makeText(mContext,"点击了完成按钮",Toast.LENGTH_SHORT).show();
//
//
////				if (PublicWay.photoService != null) {
////					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
////					Bimp.tempSelectBitmap.clear();
////					PublicWay.photoService.onActivityResult(0, -2, intent);
////				}
////                intent.setClass(mContext, MainActivity.class);
////                startActivity(intent);
////                 Intent intent = new Intent();
////                 Bundle bundle = new Bundle();
////                 bundle.putStringArrayList("selectedDataList",
////                 selectedDataList);
////                 intent.putExtras(bundle);
////                 intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
////                 startActivity(intent);
////
////                BitmapUtil bitmapUtil=new BitmapUtil();
////                bitmapUtil.com(v);
//
////                if (HttpUtil.isPublish==true){
////                    finish();
////                }else {
////                    Intent intent = new Intent(mContext,TopicActivity.class);
////                    startActivity(intent);
////                }
//            }
//        });

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
            this.finish();
            intent.setClass(ShowAllPhoto.this, ImageFile.class);
            startActivity(intent);
        }
        return false;
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        isShowOkBt();
        super.onRestart();
    }

}
