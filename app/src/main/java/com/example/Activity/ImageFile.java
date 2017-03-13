package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.Adapter.FolderAdapter;
import com.example.smsdemo.R;
import com.example.util.Bimp;
import com.example.util.PublicWay;
import com.example.util.Res;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 *
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日  下午11:48:06
 */
public class ImageFile extends Activity {
    private FolderAdapter folderAdapter;
    private Button bt_cancel;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_file);
        PublicWay.activityList.add(this);
        mContext = this;
        bt_cancel = (Button) findViewById(Res.getWidgetID("cancel"));
        bt_cancel.setOnClickListener(new CancelListener());
        GridView gridView = (GridView) findViewById(Res.getWidgetID("fileGridView"));
        TextView textView = (TextView) findViewById(Res.getWidgetID("headerTitle"));
        textView.setText(Res.getString("photo"));
        folderAdapter = new FolderAdapter(this);
        gridView.setAdapter(folderAdapter);

    }
    private class CancelListener implements View.OnClickListener {// 取消按钮的监听
        public void onClick(View v) {
            //清空选择的图片
            Bimp.tempSelectBitmap.clear();
//            Intent intent = new Intent();
//            intent.setClass(mContext, PublishActivity.class);
//            startActivity(intent);
            finish();
        }
    }
    //重写返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(mContext, PublishActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

}
