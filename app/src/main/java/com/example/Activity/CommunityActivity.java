package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.MyAdapter;
import com.example.model.ITopics;
import com.example.smsdemo.R;
import com.example.util.CustomDialog;
import com.example.util.HttpUtil;
import com.example.util.PublicWay;
import com.example.util.RequestUtil;
import com.example.util.getcellutil;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunityActivity extends Activity implements PullToRefreshLayout.OnRefreshListener {
    /**
     * 社群界面
     */
    private TextView mtextview;
    private long exitTime = 0;
    private Context mContext;
    private String userid;
    private final String PREFERENCES_NAME = "topicInfo";
    private ListView listView;
    private PullToRefreshLayout ptrl;
    List<ITopics> topics = new ArrayList<ITopics>();
    private Map<String, String> resultmap = null;
    private static String delPosition = "";
    EditText edit_search;    //搜索框
    private SharedPreferences spf;
    private SharedPreferences userspf;
    MyAdapter adapter;
    //onRefreshThread onfreshthread;  //下拉
    //onLoadMoreThread onloadthread; //上拉
    private static int count = 0;

    private MyOnfreshTask myOnfreshTask; //下拉
    private OnloadTask onloadTask;  //上拉

    private String edittext = "";
    private SearchTask searchTask; //搜索的异步线程

    //发布新话题界面
    public void onPublishClicked(View v) {
        HttpUtil.isPublish = true;
        startActivity(new Intent(mContext, PublishActivity.class));
    }

    //json转实体类
    public static <T> Object JSONToObj(String jsonStr, Class<T> obj) {
        T t = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            t = objectMapper.readValue(jsonStr, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

//    //下拉刷新的Handler
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            try {
//                //上拉加载
//                if (msg.what == 1) {
//                    if (System.currentTimeMillis() - exitTime < 1000) {
//                        Toast.makeText(getApplicationContext(), "上拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.d("pullHandler", "up");
//                        JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj));
//                        JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
//
//                        if (jsonArray.getJSONObject(0).opt("message") != null) {
//                            Log.d("pullHandler", "error");
//                            Toast.makeText(CommunityActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
//                        } else {
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
//                                if (i == 0) {
//                                    Log.d("pullHandler", jsonObject.toString());
//                                }
//                                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
//
//                                ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
//                                topics.add(topic);
//                            }
//                            Log.d("pullHandler", "refreshing");
//                            adapter = new MyAdapter(CommunityActivity.this, topics, "");
//                            listView.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//                            listView.setSelection(listView.getCount() - 16);
//                            Log.d("pullHandler", "refreshing");
//                        }
//                    }
//                }
//                //下拉刷新
//                if (msg.what == 0) {
//                    if (System.currentTimeMillis() - exitTime < 1000) {
//                        Toast.makeText(getApplicationContext(), "上拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
//                    } else {
//                        topics.clear();
//                        JSONObject jsonObject1 = new JSONObject(String.valueOf(msg.obj));
//                        Log.d("pullHandler", "" + msg.obj);
//                        JSONArray jsonArray1 = jsonObject1.getJSONArray("JsonArry");
//                        Log.d("pullHandler", "" + jsonArray1.length());
//                        for (int i = 0; i < jsonArray1.length(); i++) {
//                            JSONObject jsonObjectSon = (JSONObject) jsonArray1.opt(i);
//                            System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
//                            ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
//                            topics.add(topic);
//                        }
//                        SharedPreferences.Editor editor = spf.edit();
//                        editor.putString("topics", jsonArray1.toString());
//                        editor.commit();
//                        adapter = new MyAdapter(CommunityActivity.this, topics, "");
//                        listView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                        Log.d("pullHandler", "refreshing");
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            super.handleMessage(msg);
//        }
//    };

//    //搜索的Handler，同时获取用户头像和评论数
//    private Handler handlersearch = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Log.d("handlerSearch", "search");
//            try {
//                topics.clear();
//                JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj));
//                JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
//                if (jsonArray.getJSONObject(0).opt("message") != null) {
//                    Log.d("handlerSearch", "error");
//                    Toast.makeText(CommunityActivity.this, "未找到相应话题", Toast.LENGTH_SHORT).show();
//                    topics.clear();
//                    adapter = new MyAdapter(CommunityActivity.this, topics, "");
//                    listView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged(); //动态更新UI
//                } else {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
//                        ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
//                        topics.add(topic);
//                    }
//
//                    adapter = new MyAdapter(CommunityActivity.this, topics, "");
//                    listView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged(); //动态更新UI
//                    Log.d("handlerSearch", "searching");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            super.handleMessage(msg);
//        }
//    };

//    //删除的handler
//    private Handler handlerdele = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    resultmap = RequestUtil.getPDAServerData("delTopic.action?topicId=" + topics.get(Integer.parseInt(delPosition)).getId());
//                    super.run();
//                }
//            };
//            thread.start();
//            while (resultmap == null) {
//            }
//            if (resultmap.get("message").equals("success")) {
//                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
//                int p = Integer.parseInt(delPosition);
//                if (topics.remove(p) != null) {
//                    System.out.println("success");
//                } else {
//                    System.out.println("failed");
//                }
//                adapter = new MyAdapter(CommunityActivity.this, topics, "");
//                listView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//                super.handleMessage(msg);
//            }
//            resultmap = null;
//        }
//    };

    /**
     * 初始化
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        mContext = CommunityActivity.this;
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
        ptrl.setOnRefreshListener(this);
        edit_search = (EditText) findViewById(R.id.et_search_topic);
        listView = (ListView) findViewById(R.id.content_view);
        initListView();
        mtextview = (TextView) findViewById(R.id.top_back_tv);   //返回
        //返回上一步
        mtextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                onDestroy();
            }
        });
        //监听EditText内容变化
        edit_search.addTextChangedListener(watcher);

        spf = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        userspf = getSharedPreferences("userinfo", MODE_PRIVATE);
        userid = userspf.getString("userid", "");

        count = listView.getCount();  //获取item的个数
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override//文本改变前
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override//文本改变时
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override//文本改变后
        public void afterTextChanged(Editable editable) {
            edittext = edit_search.getText().toString();
            searchTask = new SearchTask();     //开启search的异步线程处理
            searchTask.execute();
        }
    };

    //重写返回按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            finish();
        }
        return true;
    }

    //初始化listview
    private void initListView() {
        adapter = new MyAdapter(this, topics, "");
        listView.setAdapter(adapter);

        //长按删除事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //长按删除
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                if (Long.parseLong(userid) == topics.get(position).getUserid()) {
                    delPosition = "" + position;
                    Log.d("onItemLongClick", "" + topics.get(Integer.parseInt(delPosition)).getTitle());
                    CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                    builder.setTitle("提示");
                    builder.setMessage("确定删除此话题?");
                    //添加AlertDialog.Builder对象的setPositiveButton()方法
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            /**
                             * 删除的异步处理
                             */
                            DeletAsynctask deletAsynctask = new DeletAsynctask();
                            deletAsynctask.execute();
                        }
                    });
                    //添加AlertDialog.Builder对象的setNegativeButton()方法
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                    return true;
                } else {
                    Toast.makeText(mContext, "只能删除您本人发布的话题", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        });
        //items的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long itemid) {
                //跳转到话题讨论区
                Intent intent = new Intent();
                String username = topics.get(position).getUsername();
                Integer time = topics.get(position).getClock();
                String content = topics.get(position).getContent();
                String title = topics.get(position).getTitle();
                Long id = topics.get(position).getId();
//                Toast.makeText(CommunityActivity.this,""+id,Toast.LENGTH_SHORT).show();
                intent.putExtra("username", username);
                intent.putExtra("content", content);
                intent.putExtra("title", title);
                intent.putExtra("time", time);
                intent.putExtra("id", id);
                intent.putExtra("userid", topics.get(position).getUserid()); //userid
                intent.putExtra("type", topics.get(position).getType());
                intent.putExtra("headUrl", topics.get(position).getHeadUrl());
                JSONArray jsonArray = new JSONArray(topics.get(position).getUrlList());
                Log.e("传值", jsonArray.toString());
                intent.putExtra("urlList", jsonArray.toString());
                intent.setClass(mContext, TopicActivity.class);
                startActivity(intent);
            }
        });
    }

    //这个需要写到缓存中
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 第一次进入
        Intent intent = getIntent();
        boolean isFirstIn = spf.getBoolean("isFirstIn", true);
        String topicString = spf.getString("topics", "");
        Log.d("Method-OnStart", topicString);
        if (isFirstIn) {
            ptrl.autoRefresh();
            isFirstIn = false;
            SharedPreferences.Editor editor = spf.edit();
            editor.putBoolean("isFirstIn", isFirstIn);
            editor.commit();
            Log.d("Method-OnStart", "isFisrtIn");
        } else {
            if (HttpUtil.isPublish == true) {
                ptrl.autoRefresh();
                HttpUtil.isPublish = false;
                Log.d("Method-OnStart", "发表之后进行刷新");
            } else if (topicString.equals("")) {
                ptrl.autoRefresh();
                Log.d("Method-OnStart", "非发表但是没有缓存则刷新");
            } else {
                Log.d("Method-OnStart", "缓存不为空");
                try {
                    topics.clear();
                    JSONArray jsonArray = new JSONArray(topicString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ITopics iTopic = new ITopics();
                        iTopic.setContent(jsonArray.optJSONObject(i).get("content").toString());
                        iTopic.setId(jsonArray.optJSONObject(i).getLong("id"));
                        iTopic.setTitle(jsonArray.optJSONObject(i).get("title").toString());
                        iTopic.setType(jsonArray.optJSONObject(i).get("type").toString());
                        iTopic.setClock((Integer) jsonArray.optJSONObject(i).get("clock"));
                        iTopic.setUserid(jsonArray.optJSONObject(i).getLong("userid"));
                        iTopic.setUsername(jsonArray.optJSONObject(i).getString("username"));
                        iTopic.setCommentNum(jsonArray.optJSONObject(i).getInt("commentNum"));
                        iTopic.setHeadUrl(jsonArray.optJSONObject(i).getString("headUrl"));
//                        iTopic.setUrlList(jsonArray.optJSONObject(i).getString("headUrl"));
                        JSONArray urlString = jsonArray.optJSONObject(i).getJSONArray("urlList");
                        List<String> urlList = new ArrayList<String>();
                        if (urlString.toString().equals("") || urlString.length() == 0) {
                            iTopic.setUrlList(urlList);
                        } else {
                            for (int k = 0; k < urlString.length(); k++) {
                                urlList.add((String) urlString.get(k));
                            }
                            iTopic.setUrlList(urlList);
                        }
                        topics.add(iTopic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new MyAdapter(this, topics, "");
                listView.setAdapter(adapter);
            }
        }
    }


    /**
     * 在onPause中使异步线程状态和Activity进行绑定
     */
    @Override
    protected void onPause() {
        if (myOnfreshTask != null && myOnfreshTask.getStatus() == AsyncTask.Status.RUNNING) {
            //cancel方法只是将对应的AsyncTask标记为cancel状态，并不是真正的取消线程的执行
            myOnfreshTask.cancel(true);
        }
        if (onloadTask != null && onloadTask.getStatus() == AsyncTask.Status.RUNNING) {
            onloadTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 下拉刷新
     * @param pullToRefreshLayout
     */
    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /**
                 * 启动下拉刷新的异步线程
                 */
                myOnfreshTask = new MyOnfreshTask();
                myOnfreshTask.execute();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 加载操作上拉
     * @param pullToRefreshLayout
     */
    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /**
                 * 传递给后台两个参数
                 * 启动上拉加载的异步线程
                 * 千万别忘了告诉控件加载完毕了哦！
                 */
                onloadTask = new OnloadTask();
                onloadTask.execute();
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                count = listView.getCount();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }
    /**
     * 搜索的异步任务
     */
    class SearchTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject = null;
            try {
                String result = getcellutil.getHttpJsonByhttpclient(HttpUtil.searchUrl + "?keyword=" + edittext);
                jsonObject = new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        /**
         * 更新UI操作
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()) {
                return;
            }
            Log.d("handlerSearch", "search");
            try {
                topics.clear();
                JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                if (jsonArray.getJSONObject(0).opt("message") != null) {
                    Log.d("handlerSearch", "error");
                    Toast.makeText(CommunityActivity.this, "未找到相应话题", Toast.LENGTH_SHORT).show();
                    topics.clear();
                    adapter = new MyAdapter(CommunityActivity.this, topics, "");
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged(); //动态更新UI
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
                        ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
                        topics.add(topic);
                    }
                    adapter = new MyAdapter(CommunityActivity.this, topics, "");
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged(); //动态更新UI
                    Log.d("handlerSearch", "searching");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 下拉刷新的异步任务
     */
    class MyOnfreshTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {

            JSONObject jsonObject = null;
            try {
                //下拉刷新
                String result = getcellutil.getHttpJsonByhttpclient(HttpUtil.onRefreshurl);
                jsonObject = new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        /**
         * 进行更新UI操作
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()) {
                return;
            }
            try {
                if (System.currentTimeMillis() - exitTime < 1000) {
                    Toast.makeText(getApplicationContext(), "下拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                } else {
                    topics.clear();
                    JSONArray jsonArray1 = jsonObject.getJSONArray("JsonArry");
                    Log.d("pullHandler", "" + jsonArray1.length());
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsonObjectSon = (JSONObject) jsonArray1.opt(i);
                        System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
                        ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
                        topics.add(topic);
                    }
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putString("topics", jsonArray1.toString());
                    editor.commit();
                    adapter = new MyAdapter(CommunityActivity.this, topics, "");
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d("pullHandler", "refreshing");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 上拉加载的异步任务
     */
    class OnloadTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject2 = null;
            try {
                //上拉加载
                String result2 = getcellutil.getHttpJsonByhttpclient(HttpUtil.onRefreshurl + "?operation=up" + "&position=" + count);
                jsonObject2 = new JSONObject(result2);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject2;
        }

        /**
         * 更新操作
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()) {
                return;
            }
            try {
                if (System.currentTimeMillis() - exitTime < 1000) {
                    Toast.makeText(getApplicationContext(), "上拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("pullHandler", "up");
                    JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                    if (jsonArray.getJSONObject(0).opt("message") != null) {
                        Log.d("pullHandler", "error");
                        Toast.makeText(CommunityActivity.this, "暂无更多数据", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
                            if (i == 0) {
                                Log.d("pullHandler", jsonObject.toString());
                            }
                            System.out.print(">>>>>>>" + jsonObjectSon.toString());
                            ITopics topic = (ITopics) JSONToObj(jsonObjectSon.toString(), ITopics.class);
                            topics.add(topic);
                        }
                        Log.d("pullHandler", "refreshing");
                        adapter = new MyAdapter(CommunityActivity.this, topics, "");
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(listView.getCount() - 16);
                        Log.d("pullHandler", "refreshing");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }


    /**
     * 删除的异步任务
     */
    class DeletAsynctask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject1;
            resultmap = RequestUtil.getPDAServerData("delTopic.action?topicId=" + topics.get(Integer.parseInt(delPosition)).getId());
            jsonObject1 = new JSONObject(resultmap);
            return jsonObject1;
        }

        /**
         * 更新UI
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (resultmap.get("message").equals("success")) {
                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                int p = Integer.parseInt(delPosition);
                if (topics.remove(p) != null) {
                    System.out.println("success");
                } else {
                    System.out.println("failed");
                }
                adapter = new MyAdapter(CommunityActivity.this, topics, "");
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
