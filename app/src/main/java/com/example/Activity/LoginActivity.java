package com.example.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.ChangeTime;
import com.example.util.HttpUtil;
import com.example.util.MD5;
import com.example.util.RequestUtil;
import com.example.util.getcellutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class LoginActivity extends Activity implements OnClickListener {
    private Context mContext;
    private final String PREFERENCES_NAME = "userinfo";
    private Boolean isRemember = false;
    private Boolean isDoctor = false;
    private CheckBox doctor_login, cb_remeber_pwd; //两个单选按钮
    private TextView register, forgetpassword, contact_us;    //注册，忘记密码，联系我们
    private Button mlogin;
    private EditText my_phone, my_password; //电话号码和密码
    private Map<String, String> resultmap = null;
    private String phone, passWord;
    Bundle bundle = new Bundle();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        progressDialog= new ProgressDialog(mContext);
        findById();
        init();
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        cb_remeber_pwd.setChecked(preferences.getBoolean("Remember", false));   //记住账号
        doctor_login.setChecked(preferences.getBoolean("Doctor", false));
        //医生登录
        if (cb_remeber_pwd.isChecked()) {
            // my_password.setText(preferences.getString("PassWord", null));
            my_phone.setText(preferences.getString("Phone", null));
        }
        if (doctor_login.isChecked()) {
            bundle.putBoolean("isChecked", true);
        } else {
            my_password.setText(null);
            bundle.putBoolean("isChecked", false);
        }
        //如果没有联网的话设置按钮为不能点击
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //网络未连接
                if (!RequestUtil.isNetworkAvailable(LoginActivity.this)) {
                    mlogin.setEnabled(false);   //设置按钮不能被点击
                } else {
                    mlogin.setEnabled(true);    //设置按钮是可点击状态
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    //初始化事件
    private void init() {
        mlogin.setOnClickListener(this);    //登录
        register.setOnClickListener(this);  //注册
        forgetpassword.setOnClickListener(this); //忘记密码
        contact_us.setOnClickListener(this);//联系我们
        cb_remeber_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });
        //医生登录可以看到产品介绍
        doctor_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //如果选中的话就将isChecked的值传递给主界面，主界面对isChecked的值进行判断
                isDoctor = isChecked;
            }
        });
    }

    private void findById() {
        mlogin = (Button) findViewById(R.id.login);//登录
        my_phone = (EditText) findViewById(R.id.phone);//手机号
        my_password = (EditText) findViewById(R.id.password); //密码
        register = (TextView) findViewById(R.id.register_link);//注册
        forgetpassword = (TextView) findViewById(R.id.forgetpassword);//忘记密码
        contact_us = (TextView) findViewById(R.id.contact_us);   //联系我们
        cb_remeber_pwd = (CheckBox) findViewById(R.id.checkbox2);//记住账号
        doctor_login = (CheckBox) findViewById(R.id.checkbox1);//医生登录
    }

    //点击事件
    @Override
    public void onClick(View v) {
        phone = my_phone.getText().toString();
        /**
         * 对密码进行加密
         */
        String newpassWord = my_password.getText().toString();
        passWord = new MD5().string2MD5(new MD5().string2MD5(newpassWord + "agp"));
        switch (v.getId()) {
            case R.id.login://登录
                /**
                 * 获取当前时间戳存缓存
                 */
                SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String time = ChangeTime.timeStamp();
                editor.putString("time", time);
                editor.commit();

                if (phone.equals("")) {
                    Toast.makeText(mContext, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                }
                if (newpassWord.equals("")) {
                    Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * 启动登录的异步线程
                     */
                    LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                    loginAsyncTask.execute();

                }
                break;
            case R.id.register_link://注册
                Intent intent1 = new Intent(mContext, RegisterActivity.class);
                startActivity(intent1);
                break;
            case R.id.forgetpassword://忘记密码
                Intent intent2 = new Intent(mContext, ForgetPassword.class);
                startActivity(intent2);
                break;
            case R.id.contact_us://联系我们
                Intent intent3 = new Intent(mContext, ContactUs.class);
                startActivity(intent3);
                break;
        }
    }

    //重写返回键时要记得过滤点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 登录的异步线程处理
     */
    class LoginAsyncTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading....");
            progressDialog.setCancelable(false);
            progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loading));
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject = null;
            try {
                String result = getcellutil.get(HttpUtil.login + "?phone=" +
                        phone + "&password=" + passWord);
                jsonObject = new JSONObject(result);
                Log.e("result", result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressDialog.dismiss();
            Log.e("progressdialog","关闭");
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                if (jsonArray.getJSONObject(0).opt("message").equals("success")) {
                    SharedPreferences agPreferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = agPreferences.edit();
                    editor.putString("userid", jsonArray.getJSONObject(0).opt("userid").toString());
                    editor.putString("username", jsonArray.getJSONObject(0).opt("username").toString());
                    editor.putString("headUrl", jsonArray.getJSONObject(0).opt("headUrl").toString());
                    editor.putString("Phone", phone);
                    editor.putBoolean("Remember", isRemember);
                    editor.putBoolean("isChecked", isDoctor);
                    editor.commit();    //提交数据
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                } else if (jsonArray.getJSONObject(0).opt("description").equals("")) {
                    Toast.makeText(mContext, "连接服务器失败，请稍后重试", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    my_password.setText("");
                    my_phone.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
}