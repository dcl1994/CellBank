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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddress extends Activity implements OnClickListener {
    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private TextView mytextview;
    private Button mybutton;
    private static  EditText myedittext;
    private Context mcontext;
    private String userid,email;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                try {
                    JSONObject jsonObject=new JSONObject(msg.obj.toString());
                    JSONArray jsonArray=jsonObject.getJSONArray("JsonArry");
                    if (jsonArray.getJSONObject(0).opt("message").equals("success")){
                        Toast.makeText(mcontext,"保存邮箱成功",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("youxiang",email);
                        finish();
                    }else {
                        Toast.makeText(mcontext,"保存邮箱失败",Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_email_address);
        mcontext=EmailAddress.this;
        init();

        //获取到userid
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        Log.d("userid", userid);
        myedittext.setFilters(new InputFilter[]{filter});
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
        mytextview= (TextView) findViewById(R.id.text_back);
        //保存按钮的点击事件
        mybutton= (Button) findViewById(R.id.save_emailaddress);
        //输入邮箱地址
        myedittext= (EditText) findViewById(R.id.user_emailedit);

        mytextview.setOnClickListener(this);
        mybutton.setOnClickListener(this);
        myedittext.setOnClickListener(this);
    }

    //判断是否是有效的邮箱地址的方法
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    @Override
    public void onClick(View v) {
        final String email=myedittext.getText().toString();
        Log.e("onclick", isEmail(email)+"");
        switch (v.getId()){
            case R.id.text_back:
                finish();
                break;
            case R.id.save_emailaddress:
               if (!isEmail(email)){
                    Toast.makeText(mcontext,"请填写正确的邮箱地址",Toast.LENGTH_SHORT).show();
                  }
               else if (email.equals("")){
                    Toast.makeText(mcontext,"邮箱不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    addressThread thread=new addressThread();
                    thread.start();
                }
                break;
        }
    }
    class  addressThread extends  Thread{
    final String email=myedittext.getText().toString();

        @Override
        public void run() {
            Log.v("zms", "线程changenameThread开始");
            Message msg=handler.obtainMessage();
            msg.what=1;
            try {
                Map<String ,String>  params=new HashMap<String, String>();
                params.put("userid",userid);
                params.put("email",email);
                msg.obj= HttpGetUtil.httpPost(HttpUtil.ChangeUrl, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            super.run();
        }
    }
}
