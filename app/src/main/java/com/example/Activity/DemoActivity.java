package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.Adapter.CommentAdapter;
import com.example.Adapter.MyAdapter;
import com.example.Adapter.ReplyAdapter;
import com.example.model.IComments;
import com.example.model.ITopics;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;
import com.example.widget.NoTouchLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by szjdj on 2016-11-22.
 */
public class DemoActivity extends Activity implements View.OnClickListener{
    private final String PREFERENCES_NAME = "userinfo";
    private final String PREFERENCES_COMMENT = "commentInfo";
    private SharedPreferences spf;//comment缓存
    private SharedPreferences preferences;//用户
    private String userid, username, commentId;
    private long exitTime = 0;
    private ListView mListData;     //评论区的listview
    private EditText mCommentEdittext;  //输入框
    private Button mSendBut;    //发表
    CommentAdapter commentadapter;  //评论的adapter
    MyAdapter adapter;
    ReplyAdapter replayadapter;     //回复的adapter
    private NoTouchLinearLayout commetn_lyt;   //评论框
    private LinearLayout replay_lyt; //
    private Context mContext;

    private TextView mTextview, mrepaly, mytextnumber, mytopictext;

    private ListView mlistview;

    private ListView mreplay_listview;
    private Integer comPosition; //记录位置
    private LinearLayout lyt_comment;

    List<IComments> commentsData = new ArrayList<IComments>();   //评论的数据
    private PullToRefreshLayout ptrltopic;  //刷新组件
    //  private String baseUrl = "http://192.168.1.112:8080/CellBank/getComRep.action";    //添加评论的URL地址
    long topicid;
    private boolean isReply = false;    //发表为false
    private Map<String, String> resultmap = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_topic);
        spf = getSharedPreferences(PREFERENCES_COMMENT, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        ptrltopic = ((PullToRefreshLayout) findViewById(R.id.refresh_topicview));

        mContext = DemoActivity.this;
        initview();
        initListView();

        //获取到userid和username
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        username = preferences.getString("UserName", "");
        Log.d("userid", userid);

    }
    //初始化listview
    private void initListView() {
        Intent intent = getIntent();
        final String Tusername = intent.getStringExtra("username");
        final Integer time = intent.getIntExtra("time", 0);
        final String content = intent.getStringExtra("content");
        final String title = intent.getStringExtra("title");
        final Long id = intent.getLongExtra("id", 0);
        final String TheadUrl = intent.getStringExtra("headUrl");
        final String jsonArray = intent.getStringExtra("urlList");
        Log.e("接收到的urlList", jsonArray);
        topicid = id;
//        Log.d("initListView",  "topicId:"+topicid);
//        Log.d("initListView", String.valueOf(topicid));
        final Long userid = intent.getLongExtra("userid", 0);
        final String Type = intent.getStringExtra("type");

//        final List<ITopics> topics1 = new ArrayList<ITopics>();
        ITopics topic = new ITopics();
        topic.setClock(time);
        topic.setContent(content);
        topic.setId(id);
        topic.setTitle(title);
        topic.setType(Type);
        topic.setUserid(userid);
        topic.setUsername(Tusername);
        topic.setHeadUrl(TheadUrl);
        List<String> urlList = new ArrayList<String>();
        try {
            JSONArray jsonArray1 = new JSONArray(jsonArray);
            for (int i = 0; i < jsonArray1.length(); i++) {
                urlList.add(jsonArray1.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        topic.setUrlList(urlList);
        HttpUtil.Top2ComList.clear();
        HttpUtil.Top2ComList.add(topic);
        Log.e("HttpUtil", HttpUtil.Top2ComList.get(0).getTitle());
        commentadapter = new CommentAdapter(mContext,new ArrayList<IComments>(),ComHandler);
        mListData.setAdapter(commentadapter);
    }
    private Handler ComHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    //初始化view
    private void initview() {
       // mlistview = (ListView) findViewById(R.id.topic_listview);   //顶部listview
        mListData = (ListView) findViewById(R.id.list_data);    //评论的listview
        mreplay_listview = (ListView) findViewById(R.id.no_scroll_list_reply);    //不可滚动的listview回复
        mTextview = (TextView) findViewById(R.id.top_back_tv);   //返回
        mrepaly = (TextView) findViewById(R.id.coment_text);     //回复评论窗口
        mCommentEdittext = (EditText) findViewById(R.id.edit_comment);  //输入框

        mSendBut = (Button) findViewById(R.id.but_comment_send);    //发表按钮
        replay_lyt = (LinearLayout) findViewById(R.id.edit_vg_lyt);  //评论一下
        commetn_lyt = (NoTouchLinearLayout) findViewById(R.id.edit_vg_lyt); //回复框
        mytextnumber = (TextView) findViewById(R.id.textnumber);     //可输入字数
        mSendBut.setOnClickListener(this);
        mytopictext = (TextView) findViewById(R.id.Topic_text);  //点击话题或评论人就可以直接回复
        mytopictext.setOnClickListener(this);

    }
    public void onClick(View v) {

    }
}
