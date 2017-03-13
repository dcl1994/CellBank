package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.TeamAdapter;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.util.getcellutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * 专家团队界面
 */

public class TeamExperts extends Activity {
    private TextView mytextview;
    private Context mcontext;
    private TeamAdapter teamAdapter;
    private JSONArray jsonArray;
    private ListView mlistview;
    private TeameAsyncTask teameAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_experts);
        mcontext=TeamExperts.this;
         init();
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        //开启异步线程
        teameAsyncTask=new TeameAsyncTask();
        teameAsyncTask.execute();
        super.onStart();
    }
    private void init() {
        mytextview= (TextView) findViewById(R.id.text_back);
        mlistview= (ListView) findViewById(R.id.teamlist);//找到listview
    }
    @Override
    protected void onPause() {
        if (teameAsyncTask!=null&&teameAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            teameAsyncTask.cancel(true);
        }
        super.onPause();
    }
    /**
     * 专家团队的异步线程
     */
    class TeameAsyncTask extends AsyncTask<Map,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            if (isCancelled()){
                return null;
            }
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.teamexperts);
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
                    JSONArray jsonArray=jsonObject1.getJSONArray("ExpertTeams");
                    for (int i=0;i<jsonArray.length();i++){
                        teamAdapter=new TeamAdapter(mcontext,jsonArray);
                        mlistview.setAdapter(teamAdapter);
                    }
                }
                if (!message.equals("")){
                    Toast.makeText(mcontext,message,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

}
