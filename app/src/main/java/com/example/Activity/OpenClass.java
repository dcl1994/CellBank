package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * 公开课
 */
public class OpenClass extends Activity {
    private Context mContext;
    private TextView mytextview;
    private ListView mlistview;
    private JSONArray jsonArray;
    private IndestryAdapter adapter;
    private OnpenClassAsyncTask onpenClassAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_class);
        mContext=OpenClass.this;
        init();
    }

    private void init() {
        mytextview= (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mlistview= (ListView) findViewById(R.id.doublelist);

        /**
         * listview的点击事件
         */
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent=new Intent(mContext,Rel_industry.class);
                    String url=jsonArray.getJSONObject(position).opt("url").toString();
                    intent.putExtra("url",url);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        onpenClassAsyncTask =new OnpenClassAsyncTask();
        onpenClassAsyncTask.execute(); //开启线程
    }



    @Override
    protected void onPause() {
        if (onpenClassAsyncTask!=null&&onpenClassAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            onpenClassAsyncTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 临床案例的异步线程
     */
    class OnpenClassAsyncTask extends AsyncTask<Map,Void,JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.openclass);
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
                String message=jsonObject.optString("msg");
                if (code==100){
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    jsonArray=jsonObject1.getJSONArray("Lessions");
                    adapter=new IndestryAdapter(mContext,jsonArray);
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
