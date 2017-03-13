package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.HttpGetUtil;
import com.example.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * 修改昵称界面
 */
public class user extends Activity implements OnClickListener{
    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private String userid;
    private TextView mytextview;
    private Button mybutton;
    private Context mcontext;
    private EditText myEdittext;
    private String username;
    /**
     修改昵称的handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                try {
                    JSONObject jsonObject= new JSONObject(msg.obj.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                    if (jsonArray.getJSONObject(0).opt("message").equals("success")) {
                        Toast.makeText(mcontext, "更换昵称成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("nicheng", username);
                        finish();
                    }else {
                        Toast.makeText(mcontext,"更换昵称失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mcontext=user.this;
        init();
        findById();
        //获取到userid
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        Log.d("userid", userid);
        myEdittext.setFilters(new InputFilter[]{filter});
    }
    //不能输入空格和换行符
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
    private void findById() {
        mytextview.setOnClickListener(this); //获取用户昵称
        mybutton.setOnClickListener(this);  //保存按钮
    }

    private void init() {
        mytextview= (TextView) findViewById(R.id.text_back);
        mybutton= (Button) findViewById(R.id.save);
        myEdittext= (EditText) findViewById(R.id.user_edit);
    }
    //点击事件
    @Override
    public void onClick(View v) {
       final  String tv_username=myEdittext.getText().toString();
        //获取用户填写的昵称
        switch (v.getId()) {
            case R.id.text_back:
                finish();
                break;
            case R.id.save:
                if (tv_username.equals("")) {
                    Toast.makeText(mcontext, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    changenameThread thread=new changenameThread();
                    thread.start();
                }
        }
    }
    //修改昵称的线程类
    class changenameThread extends Thread {
        final String tv_username = myEdittext.getText().toString();
        @Override
        public void run() {
            Log.v("zms","线程changenameThread开始");
            Message msg=handler.obtainMessage();
            msg.what = 1;
            try {
                Map<String,String> params = new HashMap<String, String>();
                params.put("userid",userid);
                params.put("username",tv_username);
                msg.obj= HttpGetUtil.httpPost(HttpUtil.ChangeUrl ,params);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            super.run();
        }
    }





}
