package com.example.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.ListViewAdapter;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.util.getcellutil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 专家咨询界面
 */
public class FeedBack extends Activity {

    private Context mContext;
    private LinearLayout chatlinear;    //增加群组界面
    private ListView mylistview;
    private ListViewAdapter adapter;
    private String groupId; //群组ID
    private String owner;   //群主
    EMCursorResult<EMGroupInfo> result = null;
    private String username = null;
    private final String PREFERENCES_NAME = "userinfo";
    private TextView mytextview;    //返回上一个页面

    String expert;  //从缓存中获取的专家列表项
    private GetGroupAsyncTask getGroupAsyncTask;    //获取群组列表
    private JoinAsynctask joinAsynctask;            //加入群组列表
    private JSONArray jsonArray;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        mContext = FeedBack.this;
        super.onStart();        //登录获取groupID
        init();
        mylistview = (ListView) findViewById(R.id.listview);
        //item的点击事件
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 启动加入群聊
                 */
                joinAsynctask = new JoinAsynctask();
                joinAsynctask.execute();
                Intent intent = new Intent(mContext, GroupDetailsActivity.class);
                try {
                    groupId = jsonArray.getJSONObject(position).get("groupid").toString();
                    String groupowner = jsonArray.getJSONObject(position).get("owner").toString();
                    owner=groupowner.replace("1182170210178310#lifebank_", "");

                    intent.putExtra("owner",owner);
                    intent.putExtra("groupId",groupId);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                /**
//                 * 将groupid存缓存
//                 */
//                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
//                editor.putString("groupId", groupId);
//                editor.apply();
            }
        });

        SharedPreferences pref = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        username = pref.getString("Phone", "");
        Log.e("feed_username", username);

        //创建群聊
        chatlinear = (LinearLayout) findViewById(R.id.addgroup);
        chatlinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddGroupActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 退出登录的点击事件
     */
    private void init() {
        mytextview = (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EMClient.getInstance().logout(true);
                /**
                 * 退出登录
                 */
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    /**
     * 加入某个群组的异步处理
     */
    class JoinAsynctask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                EMClient.getInstance().groupManager().joinGroup(groupId);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 获取群组列表的异步处理
     */
    class GetGroupAsyncTask extends AsyncTask<Map, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject = null;
            try {
                String result = getcellutil.getHttpJsonByhttpclient(HttpUtil.getGroupList);
                publishProgress();
                jsonObject = new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        /**
         * 解析json数据
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressDialog.dismiss();
            Log.e("progressDialog","已关闭");
            if (isCancelled()) {
                return;
            }
            int code = jsonObject.optInt("code");
            String message = jsonObject.optString("msg");
            if (code == 100) {
                try {
                    jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length()==0){
                        Toast.makeText(mContext,"没有群组",Toast.LENGTH_SHORT).show();
                    }
                    adapter = new ListViewAdapter(mContext, jsonArray, username);
                    mylistview.setAdapter(adapter);
                    if (!message.equals("")) {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    protected void onStart() {

        progressDialog= new ProgressDialog(mContext);   //new一个progressdialog实例
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loading));
        progressDialog.show();

//        Log.e("onProgressUpdate", "asdasdasd");
//        EMClient.getInstance().login(username, "123456", new EMCallBack() {//回调
//            @Override
//            public void onSuccess() {
//                Log.d("main", "登录聊天服务器成功！");
//                /**
//                 * 获取群组列表拿到groupid
//                 */
                getGroupAsyncTask = new GetGroupAsyncTask();
                getGroupAsyncTask.execute();
//            }
//
//            @Override
//            public void onProgress(int progress, String status) {
//            }
//
//            @Override
//            public void onError(int code, String message) {
//                Log.d("main", "登录聊天服务器失败！");
//                Log.e("long", "登录失败" + code + "," + message);
//            }
//        });
        /**
         * 从缓存中拿到phone判断是否是专家，是则显示添加群组的功能
         */
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        Set<String> stringSet = pref.getStringSet("experts_list", null);
        for (String s : stringSet) {
            expert = s;
            Log.e("feed_expert", expert);
            if (expert.equals(username)) {
                chatlinear.setVisibility(View.VISIBLE);
            }
        }
        super.onStart();
    }


    /**
     * 将Asynctask与activity进行绑定
     */
    @Override
    protected void onPause() {
        if (getGroupAsyncTask != null && getGroupAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            getGroupAsyncTask.cancel(true);
        }
        if (joinAsynctask != null && joinAsynctask.getStatus() == AsyncTask.Status.RUNNING) {
            joinAsynctask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 重写返回键跳转到首页
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
