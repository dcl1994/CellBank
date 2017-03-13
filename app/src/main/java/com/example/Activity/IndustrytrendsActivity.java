package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.IndestryAdapter;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.util.getcellutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 行业动态
 */
public class IndustrytrendsActivity extends Activity {
    private TextView mytextView;
    private Context mContext;
    private ListView mlistview;
    private IndestryAdapter adapter;

    private JSONArray nativeJson;
    private IndestryAsyncTask indestryAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industrytrends);
        init();
        mContext=IndustrytrendsActivity.this;
    }
    //初始化
    private void init() {
        mlistview= (ListView) findViewById(R.id.mylistview);
        mytextView= (TextView) findViewById(R.id.text_back);
        mytextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        indestryAsyncTask =new IndestryAsyncTask();
        indestryAsyncTask.execute();    //开启线程

           // 设置listview的点击事件
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取URL
                try {
                    Intent intent = new Intent(mContext, Rel_industry.class);
                    String url = nativeJson.getJSONObject(position).opt("url").toString();
                    Log.e("url", url);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (indestryAsyncTask!=null&&indestryAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            indestryAsyncTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 行业动态的异步线程
     */
    class IndestryAsyncTask extends AsyncTask<Map,Void,JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.Industrynews);
                jsonObject=new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        /**
         * 更新UI
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            try {
                int code  =jsonObject.optInt("code");
                String message=jsonObject.optString("msg");
                if (code==100){
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    nativeJson=jsonObject1.getJSONArray("News");
                    adapter=new IndestryAdapter(mContext,nativeJson);
                    mlistview.setAdapter(adapter);
                }
                if (!message.equals("")){
                    Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
}
