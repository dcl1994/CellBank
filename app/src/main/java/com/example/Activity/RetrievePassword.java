package com.example.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.example.smsdemo.R;
import com.example.util.MD5;
import com.example.util.RequestUtil;


import java.util.Map;

/**
 * 修改密码
 *当用户填写了新密码与确认密码后，进行插入数据库操作，成功后返回主页面
 */
public class RetrievePassword extends Activity implements OnClickListener {
    private Context mContext;

    private Button change;

    private Map<String,String> result;

    private EditText enter_password,confirm_password;   //输入新密码，确认密码

    private Map<String,String> resultmap=null;
    String ep;
    String cp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        mContext=RetrievePassword.this;
        change= (Button) findViewById(R.id.change_btn);
        enter_password= (EditText) findViewById(R.id.enter_password);   //输入新密码
        confirm_password= (EditText) findViewById(R.id.confirm_password);//确认密码
        init();
    }
    //初始化事件
    private void init() {
        change.setOnClickListener(this);
    }
    //返回上一页
    public void return1(View view){
        finish();
    }

    //点击事件//点击修改密码按钮将值发送给后台服务器
    @Override
    public void onClick(View v) {
        Intent intent=getIntent();  //获取到phone的值
        Bundle bundle = intent.getExtras();
        final String phone = bundle.getString("phone");
        switch (v.getId()) {
            case R.id.change_btn:
                /**
                 * 对密码进行加密
                 */
                String newpassWord=enter_password.getText().toString();
                ep=new MD5().string2MD5(new MD5().string2MD5(newpassWord + "agp"));

                String newpassWord2=confirm_password.getText().toString();
                cp=new MD5().string2MD5(new MD5().string2MD5(newpassWord2+ "agp"));
                if(ep.equals("")){
                    Toast.makeText(mContext,"输入密码不能为空!", Toast.LENGTH_SHORT).show();
                }if(ep.length()<6){
                Toast.makeText(mContext,"密码最小长度为6,请重新输入!", Toast.LENGTH_SHORT).show();
            }
                if(cp.equals("")){
                Toast.makeText(mContext,"确认密码不能为空!", Toast.LENGTH_SHORT).show();
                }else if(!(ep.equals(cp))){
                    Toast.makeText(mContext,"两次输入的密码不相等!",Toast.LENGTH_SHORT).show();
                    enter_password.setText("");
                    confirm_password.setText("");
                     return;
            }else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            resultmap = RequestUtil.getPDAServerData("changePw.action?phone=" +
                                    phone + "&password=" + cp);
                            Looper.prepare();
                            Toast.makeText(mContext, resultmap.get("description"), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });
                    thread.start();
                    while (resultmap==null){}
                    if (resultmap.get("message").equals("success")){
                        Toast.makeText(mContext, resultmap.get("description"), Toast.LENGTH_SHORT).show();
                        Intent intent1=new Intent(mContext,LoginActivity.class);
                        startActivity(intent1);
                        finish();
                    }else{
                        Toast.makeText(mContext, resultmap.get("description"), Toast.LENGTH_SHORT).show();
                    }
                    resultmap=null;
                }
                break;
        }
    }
}
