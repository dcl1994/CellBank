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
import com.example.util.MD5;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改密码页面
 * 新的修改密码的接口是根据旧密码和新密码来修改的
 * 需要传递的数据是newPassword,password,userid,userid是long类型
 */

public class ChangePassword extends Activity implements OnClickListener {
    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private String userid;
    private TextView mytextview;
    private Button mybutton;
    private Context mcontext;
    private EditText my_oldpwd, my_newpwd, my_newpwd2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                    Log.e("JsonArry", "" + jsonObject.getJSONArray("JsonArry"));
                    if (jsonArray.getJSONObject(0).opt("message").equals("success")) {
                        Toast.makeText(mcontext, "更换密码成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mcontext, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mcontext, "更换密码失败", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_change_password);
        init();
        mcontext = ChangePassword.this;
        //获取到userid
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        Log.d("userid", userid);
        my_oldpwd.setFilters(new InputFilter[]{filter});
        my_newpwd.setFilters(new InputFilter[]{filter});
        my_newpwd2.setFilters(new InputFilter[]{filter});
    }
    //不能输入空格和换行符
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
    private void init() {
        mytextview = (TextView) findViewById(R.id.text_back);
        mybutton = (Button) findViewById(R.id.change);
        my_oldpwd = (EditText) findViewById(R.id.oldpassword);
        my_newpwd = (EditText) findViewById(R.id.newpassword);
        my_newpwd2 = (EditText) findViewById(R.id.newpassword2);
        mytextview.setOnClickListener(this);
        mybutton.setOnClickListener(this);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        final String oldpwd = my_oldpwd.getText().toString();
        final String newpwd = my_newpwd.getText().toString();
        final String newpwd2 = my_newpwd2.getText().toString();
        switch (v.getId()) {
            case R.id.text_back:
                finish();
                break;
            case R.id.change:
                if (oldpwd.equals("")) {
                    Toast.makeText(mcontext, "旧密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (newpwd.equals("")) {
                    Toast.makeText(mcontext, "新密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (newpwd2.equals("")) {
                    Toast.makeText(mcontext, "确认新密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!newpwd.equals(newpwd2)) {
                    Toast.makeText(mcontext, "两次输入的密码不相等", Toast.LENGTH_SHORT).show();
                } else {
                    changethread thread = new changethread();
                    thread.start();
                }
                break;
        }
    }
    class changethread extends Thread {
        String password = my_oldpwd.getText().toString();
        String newpassword = my_newpwd2.getText().toString();
        @Override
        public void run() {
            Log.v("zms", "线程changenameThread开始");
            Message msg = handler.obtainMessage();
            msg.what = 1;
            try {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userid);
                password=new MD5().string2MD5(new MD5().string2MD5(password+"agp"));
                params.put("password", password);
                newpassword=new MD5().string2MD5(new MD5().string2MD5(newpassword+"agp"));
                params.put("newPassword", newpassword);
                msg.obj = HttpGetUtil.httpPost(HttpUtil.changepassword, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            super.run();
        }
    }
}
