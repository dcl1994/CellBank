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

import com.example.Adapter.ClinOpenAdapter;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.util.getcellutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 临床案例
 */
public class CliniCalcase extends Activity {
    private TextView mytext;
    private Context mContext;
    private ListView mlistview;
    private ClinOpenAdapter coadapter;
    private JSONArray nativeJson;
    private CliniCalcaseAsyncTask cliniCalcaseAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clini_calcase);
        init();
        mContext=CliniCalcase.this;
    }
    private void init() {
        mytext= (TextView) findViewById(R.id.text_back);
        mytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mlistview= (ListView) findViewById(R.id.CCalcaselist);

        //设置listview的点击事件
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
        /**
         * 开启线程CliniCalcaseThread
         */
        cliniCalcaseAsyncTask=new CliniCalcaseAsyncTask();
        cliniCalcaseAsyncTask.execute();
    }
    @Override
    protected void onPause() {
        if (cliniCalcaseAsyncTask!=null&&cliniCalcaseAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            cliniCalcaseAsyncTask.cancel(true);
        }
        super.onPause();
    }
    /**
     * 临床案例的异步线程
     */
    class CliniCalcaseAsyncTask extends AsyncTask<Map,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.clinicalcase);
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
                int code=jsonObject.optInt("code");
                String message=jsonObject.optString("message");
                if (code==100){
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    nativeJson=jsonObject1.getJSONArray("Cases");
                    coadapter=new ClinOpenAdapter(mContext,nativeJson);
                    mlistview.setAdapter(coadapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

}
