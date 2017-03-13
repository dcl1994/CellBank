package com.example.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.MD5;
import com.example.util.RequestUtil;
import com.example.util.getTipUtil;
import java.util.Map;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends Activity implements OnClickListener {
    private Context mContext;

    private EditText user,my_password,phone,get; //用户名，密码，电话号码,验证码

    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "171d0f580d45a";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET ="b765d576f56cbe56b0a1d2d41fc406f6";
    public String phString;
    private String password;
    private Button register_gettexting,reg_Btn; //获取验证码，注册按钮

    private Map<String,String> resultmap = null;

    private boolean isoncl=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext= RegisterActivity.this;
        findById();
        init();
    }
    //初始化view
    private void init() {
        phone.setOnClickListener(this);

        reg_Btn.setOnClickListener(this);

        register_gettexting.setOnClickListener(this);

        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                handler.sendMessage(msg);
                msg.obj = data;
            }

        };
        SMSSDK.registerEventHandler(eh);
    }
    //初始化点击事件
    private void findById() {
        user= (EditText) findViewById(R.id.register_user);  //用户
        my_password= (EditText) findViewById(R.id.register_password);//密码
        phone= (EditText) findViewById(R.id.register_phone);//电话号码
        get= (EditText) findViewById(R.id.get); //验证码
        reg_Btn= (Button) findViewById(R.id.register_Button);//注册
        register_gettexting= (Button) findViewById(R.id.register_gettexting); //获取验证码
    }
    @Override
    public void onClick(View v) {
        /**
         * 对密码进行加密
         */
        String newpassWord=my_password.getText().toString();
        password=new MD5().string2MD5(new MD5().string2MD5(newpassWord + "agp"));
        switch (v.getId()) {
            case R.id.register_gettexting:// 获取验证码
                if(user.getText().toString().equals("")){
                    Toast.makeText(mContext,"用户名不能为空!",Toast.LENGTH_SHORT).show();
                }
                if(my_password.getText().toString().equals("")) {
                Toast.makeText(mContext, "密码不能为空!", Toast.LENGTH_SHORT).show();
                }
                if(phone.getText().toString().equals("")){
                    Toast.makeText(this, "电话不能为空!", Toast.LENGTH_SHORT).show();
                }else {
                    SMSSDK.getVerificationCode("86", phone.getText().toString());
                    phString = phone.getText().toString();
                }if(my_password.getText().toString().length()<6){
                    Toast.makeText(mContext,"密码最小长度为6,请重新输入!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register_Button:// 点击注册按钮校验验证码
                if(isoncl) {
                    if (!TextUtils.isEmpty(get.getText().toString())) {
                        SMSSDK.submitVerificationCode("86", phString, get.getText().toString());
                        finish();
                    } else {
                        Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_SHORT).show();
                    }
                    isoncl=false; //点击一次后就改成false，这样就实现只点击一次了
                    break;
                }
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("----" + event);
                // 短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Thread thread=new Thread(new Runnable() {
                            @Override
                            public void run() {
                                resultmap = RequestUtil.getPDAServerData("register.action?phone=" +
                                        phone.getText().toString()+"&username="+
                                        user.getText().toString()+"&password="+password);
                                Looper.prepare();
                                Toast.makeText(mContext, resultmap.get("description"), Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        });
                        thread.start();
                        while (resultmap==null){
                        }
                        if (resultmap.get("message").equals("success")){
                            Intent intent=new Intent(mContext,LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext, resultmap.get("description"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送!",
                            Toast.LENGTH_SHORT).show();
                } else if (event == SMSSDK.RESULT_ERROR) {
                    Toast.makeText(getApplicationContext(), "------",Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                String tip = getTipUtil.getTip(data.toString());
                Toast.makeText(getApplicationContext(),tip,Toast.LENGTH_SHORT).show();
            }
            resultmap=null;
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
    //返回按钮
    public void return1(View v){
        finish();
    }
}
