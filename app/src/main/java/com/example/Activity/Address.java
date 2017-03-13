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

/**
 * 保存用户地址
 */
public class Address extends Activity implements OnClickListener {
    private TextView mytextview;
    private Button mybutton;
    private Context mcontext;
    private EditText myuser_address;
    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private String userid;
    private String tv_address;
    /**
     * 修改地址的handle
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                try {
                    JSONObject jsonObject=new JSONObject(msg.obj.toString());
                    JSONArray jsonArray=jsonObject.getJSONArray("JsonArry");
                    if (jsonArray.getJSONObject(0).opt("message").equals("success")){
                        Toast.makeText(mcontext,"保存成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra("dizhi",tv_address);
                        finish();
                    }else {
                        Toast.makeText(mcontext,"保存失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        mcontext=Address.this;
        init();
        //获取到userid
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        Log.d("userid", userid);
        myuser_address.setFilters(new InputFilter[]{filter});
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
        mybutton= (Button) findViewById(R.id.save_address);
        mytextview= (TextView) findViewById(R.id.text_back);
        myuser_address= (EditText) findViewById(R.id.user_address);
        mybutton.setOnClickListener(this);  //保存
        mytextview.setOnClickListener(this); //返回
        myuser_address.setOnClickListener(this);//地址
    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //保存的点击事件
            case R.id.save_address:
                if (myuser_address.getText().equals("")){
                    Toast.makeText(mcontext,"地址不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    changeaddressThread thread=new changeaddressThread();
                    thread.start();
                }
                break;
            case R.id.text_back:
                finish();
            break;
        }
    }
    class changeaddressThread extends Thread{
        final String tv_address=myuser_address.getText().toString();
        @Override
        public void run() {
            Log.v("zms", "线程changenameThread开始");
            Message msg=handler.obtainMessage();
            msg.what=1;
            try {
                Map<String ,String> params=new HashMap<String, String>();
                params.put("userid",userid);
                params.put("address",tv_address);
                msg.obj= HttpGetUtil.httpPost(HttpUtil.ChangeUrl, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            super.run();
        }
    }


}
