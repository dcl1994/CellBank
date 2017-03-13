package com.example.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.getTipUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 忘记密码
 * 通过短信验证的方式，验证码成功通过就跳转到找回密码界面
 */
public class ForgetPassword extends Activity implements OnClickListener {
    private Context mContext;
    private Button sensmsButton,retrieve;   //获取验证码，找回密码
    private EditText fg_phone, fg_get;
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "1746f4006554c";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "cd241e28102cdb19dc56c7a4e0ee0d68";
    public String phString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        sensmsButton = (Button) findViewById(R.id.fg_gettexting); //获取验证码
        retrieve= (Button) findViewById(R.id.retrieve_password); //找回密码

        fg_phone= (EditText) findViewById(R.id.fg_phone);   //获取手机号输入框
        fg_get= (EditText) findViewById(R.id.fg_get);       //获取验证码输入框
        sensmsButton.setOnClickListener(this);
        retrieve.setOnClickListener(this);
        mContext=ForgetPassword.this;
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fg_gettexting:// 获取验证码
                if (!TextUtils.isEmpty(fg_phone.getText().toString())) {
                    SMSSDK.getVerificationCode("86", fg_phone.getText().toString());
                    phString = fg_phone.getText().toString();
                } else {
                    Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.retrieve_password:// 找回密码
                if (!TextUtils.isEmpty(fg_get.getText().toString())) {
                    System.out.println(fg_get.getText().toString().trim());
                    System.out.println(phString.trim());
                    SMSSDK.submitVerificationCode("86", phString, fg_get
                            .getText().toString());
                } else {
                    Toast.makeText(this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                }
                break;

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
                        Intent intent=new Intent(mContext,RetrievePassword.class);  //跳转到找回密码界面
                        intent.putExtra("phone",phString);//将电话号码的值带到RetrievePassword中去
                        startActivity(intent);
                Toast.makeText(getApplicationContext(), "短信验证成功",Toast.LENGTH_SHORT).show();}
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送",
                            Toast.LENGTH_SHORT).show();
                }else if (event == SMSSDK.RESULT_ERROR) {
                    Toast.makeText(getApplicationContext(), "------",Toast.LENGTH_SHORT).show();
                }
            } else {
                ((Throwable) data).printStackTrace();
                Toast.makeText(getApplicationContext(),"错误"+data,Toast.LENGTH_SHORT).show();
                String tip = getTipUtil.getTip(data.toString());
                Toast.makeText(getApplicationContext(),tip,Toast.LENGTH_SHORT).show();

            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
    //返回上一页
    public void return1(View view){
        finish();
    }
}
