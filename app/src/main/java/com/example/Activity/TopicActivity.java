package com.example.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.CommentAdapter;
import com.example.Adapter.MyAdapter;
import com.example.Adapter.ReplyAdapter;
import com.example.model.IComments;
import com.example.model.IReplys;
import com.example.model.ITopics;
import com.example.smsdemo.R;
import com.example.util.Bimp;
import com.example.util.ChangeTime;
import com.example.util.FileUtils;
import com.example.util.HttpGetUtil;
import com.example.util.HttpUtil;
import com.example.util.ImageItem;
import com.example.util.MyGridView;
import com.example.util.PublicWay;
import com.example.util.Res;
import com.example.util.UploadUtil;
import com.example.util.getcellutil;
import com.example.widget.NoTouchLinearLayout;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicActivity extends Activity implements OnClickListener, PullToRefreshLayout.OnRefreshListener {
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
    TopicAdapter myadapter;
    private NoTouchLinearLayout commetn_lyt;   //评论框
    private LinearLayout replay_lyt; //
    private Context mContext;
    private MyGridView mytopicgridview; //gridview网格视图
    private PopupWindow pop;
    private LinearLayout ll_popup;
    private View parentView;
    public static Bitmap bimap;
    private TextView mTextview, mrepaly, mytextnumber, mytopictext, mytext;
    private ListView mlistview;
    private ListView mreplay_listview;
    private Integer comPosition; //记录位置
    private LinearLayout lyt_comment;
    List<IComments> commentsData = new ArrayList<IComments>();   //评论的数据
    private PullToRefreshLayout ptrltopic;  //刷新组件
    long topicid;
    private boolean isReply = false;    //发表为false
    private Map<String, String> resultmap = null;
    public Map<String, File> files = new HashMap<String, File>();
    private Map<String, String> params = new HashMap<String, String>();
    private Context context;
    public static int count=0;
    //        onRefreshThread onrefreshthread;    //下拉
    //        onLoadMoreThread onloadthread;     //上拉

    private MyOnfreshTask myOnfreshTask; //下拉
    private OnloadTask onloadTask;  //上拉

    private String comment=""; //输入的评论内容


    private Handler ComHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 12:
                    //删除评论
                    commentsData.remove(Integer.parseInt(msg.obj + ""));
                    Log.d("ComHandler", "msg.obj" + msg.obj);
                    commentadapter = new CommentAdapter(mContext, commentsData, this);
                    mListData.setAdapter(commentadapter);
                    commentadapter.notifyDataSetChanged();
//                    ptrltopic.autoRefresh();
                    Log.d("ComHandler", "msg.obj" + msg.obj + "");
                    break;
                case 13: //点击回复
                    isReply = true;
                    commentId = String.valueOf(commentsData.get((Integer) msg.obj).getId());
                    comPosition = (Integer) msg.obj;
                    String username = commentsData.get((Integer) msg.obj).getUsername();
                    //  Toast.makeText(mContext, "被点击了" + msg.obj + "isreply" + isReply, Toast.LENGTH_SHORT).show();
                    mCommentEdittext.setHint("回复" + username + ":");
                    mCommentEdittext.setText("");
                    onFocusChange(true);
                    replay_lyt.setVisibility(View.GONE); //将评论一下隐藏
                    commetn_lyt.setVisibility(View.VISIBLE); //将回复评论的输入框显示出来
                    mytopicgridview.setVisibility(View.GONE);//将网格视图隐藏
                    mytext.setVisibility(View.GONE);        //将提示信息隐藏
                    break;
            }
        }
    };

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

    /**
     * 添加评论的handler
     */
    private Handler handlercomment = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            final String content = msg.obj.toString();
//            if (!"".equals(msg.obj.toString())) {
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        resultmap = RequestUtil.getPDAServerData("addComment.action?userid="
//                                + userid + "&content=" + content + "&topicId=" + topicid);
//                    }
//                });
//                thread.start();
//                while (resultmap == null) {
//                }
//                if (resultmap.get("message").equals("success")) {
//                    Toast.makeText(mContext, "发表成功", Toast.LENGTH_SHORT).show();
//                    mCommentEdittext.setText("");   //发表成功清除edittext
//                    Bimp.tempSelectBitmap.clear();  //发表成功清除图片
//                    ptrltopic.autoRefresh();
//                } else {
//                    Toast.makeText(mContext, "发表失败", Toast.LENGTH_SHORT).show();
//                }
//                resultmap = null;
//            }
//            super.handleMessage(msg);
//        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj));
                    JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                    Log.e("handler", jsonArray.getJSONObject(0) + "");
                    if (jsonArray.getJSONObject(0).opt("description").toString().indexOf("发布成功") != -1) {
                        Toast.makeText(mContext, "发表成功", Toast.LENGTH_SHORT).show();
                        mCommentEdittext.setText("");
                        Bimp.tempSelectBitmap.clear();  //发表成功清除图片
                        Bimp.tempFile.clear();      //发表成功清除临时文件
                        ptrltopic.autoRefresh();
                    } else {
                        Toast.makeText(TopicActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.handleMessage(msg);
            }
        }
    };
    //添加回复的handler replay
    private Handler handlerreplay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj));
                    JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");

                    if (jsonArray.getJSONObject(0).opt("message").equals("success")) {
                        Toast.makeText(mContext, "回复成功", Toast.LENGTH_SHORT).show();
                        Log.d("handlerreplay", username);
                        IReplys reply = new IReplys(); //回复的对象
                        reply.setClock(Integer.parseInt(ChangeTime.timeStamp()));
                        reply.setContent(mCommentEdittext.getText().toString());
                        reply.setUsername(username);
                        Log.d("handlerreplay", commentId);
                        reply.setCommentid(Long.parseLong(commentId));
                        reply.setUserid(Long.parseLong(userid));
                        List<IReplys> iReplys = commentsData.get(comPosition).getReplys();
                        iReplys.add(reply);
                        commentsData.get(comPosition).setReplys(iReplys);
                        commentadapter = new CommentAdapter(mContext, commentsData, ComHandler);
                        mListData.setAdapter(commentadapter);
                        commentadapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "发表失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "服务器繁忙或连接超时，请稍后重试", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    //下拉刷新的Handler
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            try {
//                //上拉加载
//                if (msg.what == 1) {
//                    if (System.currentTimeMillis() - exitTime > 1000) {
//                        exitTime = System.currentTimeMillis();
////                        if (!commentsData.isEmpty()) {
////                            commentsData.clear();
////                        }
//                        Log.d("pullHandler", "up");
//                        JSONObject jsonObject = new JSONObject(String.valueOf(msg.obj));
//                        JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
//                        if (jsonArray.getJSONObject(0).opt("message") != null) {
//                            Log.d("pullHandler", "error");
//                            Toast.makeText(mContext, "没有评论咯", Toast.LENGTH_SHORT).show();
//                        } else {
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
//                                if (i == 0) {
//                                    Log.d("pullHandler", jsonObject.toString());
//                                }
//                                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
//                                IComments coment = (IComments) JSONToObj(jsonObjectSon.toString(), IComments.class);
//                                commentsData.add(coment);
//                            }
//                            commentadapter = new CommentAdapter(mContext, commentsData, ComHandler);
//                            commentadapter.notifyDataSetChanged();
//                            Log.d("pullHandler", "refreshing");
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "上拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                //下拉刷新
//                if (msg.what == 0) {
//                    if (System.currentTimeMillis() - exitTime > 1000) {
//                        exitTime = System.currentTimeMillis();
//                        if (!commentsData.isEmpty()) {
//                            commentsData.clear();
//                        }
//                        Log.d("pullHandler", "" + msg.obj);
//                        JSONObject jsonObject1 = new JSONObject(String.valueOf(msg.obj));
//                        JSONArray jsonArray1 = jsonObject1.getJSONArray("JsonArry");
//                        if (jsonArray1.getJSONObject(0).opt("message") != null) {
//                            Log.d("pullHandler", "error");
//                            Toast.makeText(mContext, "没有评论，来做第一个评论的人吧", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("pullHandler", "refreshing");
//                            for (int i = 0; i < jsonArray1.length(); i++) {
//                                JSONObject jsonObjectSon = (JSONObject) jsonArray1.opt(i);
//                                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
//                                IComments coment = (IComments) JSONToObj(jsonObjectSon.toString(), IComments.class);
//                                commentsData.add(coment);
//                            }
//                            Log.d("pullHandler", jsonArray1.toString());
//                            SharedPreferences.Editor editor = spf.edit();
//                            editor.putString(topicid + "", jsonArray1.toString());
//                            editor.commit();
//                            commentadapter = new CommentAdapter(mContext, commentsData, ComHandler);
//                            mListData.setAdapter(commentadapter);
//                            commentadapter.notifyDataSetChanged();
//                            Log.d("pullHandler", "refreshing");
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "下拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            super.handleMessage(msg);
//        }
//    };

    /**
     * 话题讨论区
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        spf = getSharedPreferences(PREFERENCES_COMMENT, MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_topic, null);
        setContentView(parentView);
        ptrltopic = ((PullToRefreshLayout) findViewById(R.id.refresh_topicview));
        ptrltopic.setOnRefreshListener(this);
        mContext = TopicActivity.this;
        initview();
        initListView();
        count=mListData.getCount(); //获取listview里item的个数
        /**
         * 返回按钮的点击事件
         */
        mTextview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < PublicWay.activityList.size(); i++) {
                    if (null != PublicWay.activityList.get(i)) {
                        PublicWay.activityList.get(i).finish();
                    }
                }
                Bimp.tempSelectBitmap.clear();  //返回清除图片

                finish();
            }
        });
        //获取到userid和username
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        username = preferences.getString("UserName", "");
        Log.d("userid", userid);
        mCommentEdittext.addTextChangedListener(watcher);
        mCommentEdittext.setFilters(new InputFilter[]{filter});

    }

    //发表框不能输入空格和换行符
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
    //动态监听改变字体
    TextWatcher watcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            temp = s;
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mCommentEdittext.getSelectionStart();
            editEnd = mCommentEdittext.getSelectionEnd();

            comment=mCommentEdittext.getText().toString();  //获取文本框输入的内容

            mytextnumber.setText("您还能输" + (200 - temp.length()) + "个字符");
            if (temp.length() > 200) {
                Toast.makeText(mContext,
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mCommentEdittext.setText(s);
                mCommentEdittext.setSelection(tempSelection);
            }
        }
    };

    /**
     * 重写onKeyDown,进行判断
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();  //返回清除图片

            finish();
        }
        return true;
    }

    /**
     * 显示或隐藏输入法
     */
    private void onFocusChange(boolean hasFocus) {
        final boolean isFocus = hasFocus;
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        mCommentEdittext.getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (isFocus) {
                    //显示输入法
                    mCommentEdittext.requestFocus();//获取焦点
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    //隐藏输入法
                    imm.hideSoftInputFromWindow(mCommentEdittext.getWindowToken(), 0);
                    replay_lyt.setVisibility(View.VISIBLE);
                    commetn_lyt.setVisibility(View.GONE);
                }
            }
        }, 100);
    }

    /**
     * 点击屏幕其他地方收起输入法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                onFocusChange(false);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 隐藏或者显示输入框
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            /**
             *这堆数值是算我的下边输入区域的布局的，
             * 规避点击输入区域也会隐藏输入区域
             */
            v.getLocationInWindow(leftTop);
            int left = leftTop[0] - 50;
            int top = leftTop[1] - 50;
            int bottom = top + v.getHeight() + 300;
            int right = left + v.getWidth() + 120;
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
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
//        Log.e("接收到的urlList", jsonArray);
        topicid = id;
//        Log.d("initListView",  "topicId:"+topicid);
//        Log.d("initListView", String.valueOf(topicid));
        final Long userid = intent.getLongExtra("userid", 0);
        final String Type = intent.getStringExtra("type");

        spf = getSharedPreferences("to", MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString("Tusername", Tusername);
        editor.putString("content", content);
        editor.putString("title", title);
        editor.putString("TheadUrl", TheadUrl);
        editor.putString("jsonArray", jsonArray);
        editor.putLong("id", id);
        editor.putInt("time", time);
        editor.commit();

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
//        topics1.add(topic);
        HttpUtil.Top2ComList.clear();
        HttpUtil.Top2ComList.add(topic);
        Log.e("", "");
        commentadapter = new CommentAdapter(mContext, new ArrayList<IComments>(), ComHandler);
        mListData.setAdapter(commentadapter);
//        mListData.setAdapter(new CommentAdapter(TopicActivity.this, new ArrayList<IComments>(), ComHandler));
        mListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {//点击话题,无效，所以过滤掉
                } else {
                    Log.e("OnitemClick", ">0:" + position + "");
                    Message msg = ComHandler.obtainMessage();
                    msg.what = 13;
                    msg.obj = position - 1;
                    ComHandler.sendMessage(msg);
                }
            }
        });
    }

    //初始化view
    private void initview() {
        mytext = (TextView) findViewById(R.id.hint_textview);
//                mlistview = (ListView) findViewById(R.id.topic_listview);   //顶部listview
        mListData = (ListView) findViewById(R.id.list_data);    //评论的listview
        mreplay_listview = (ListView) findViewById(R.id.no_scroll_list_reply);    //不可滚动的listview回复
        mTextview = (TextView) findViewById(R.id.top_back_tv);   //返回
        mrepaly = (TextView) findViewById(R.id.coment_text);     //回复评论窗口
        mCommentEdittext = (EditText) findViewById(R.id.edit_comment);  //输入框

        lyt_comment = (LinearLayout) findViewById(R.id.lyt_comment);     //评论框
        lyt_comment.setOnClickListener(this);
        replay_lyt = (LinearLayout) findViewById(R.id.edit_vg_lyt);
        replay_lyt.setOnClickListener(this);
        mSendBut = (Button) findViewById(R.id.but_comment_send);    //发表按钮
        commetn_lyt = (NoTouchLinearLayout) findViewById(R.id.edit_vg_lyt); //回复框
        mytextnumber = (TextView) findViewById(R.id.textnumber);     //可输入字数
        mSendBut.setOnClickListener(this);
        mytopictext = (TextView) findViewById(R.id.Topic_text);  //评论一下
        mytopictext.setOnClickListener(this);
        pop = new PopupWindow(TopicActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view


                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);

        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 发表下面的网格视图
         */
        mytopicgridview = (MyGridView) findViewById(R.id.topicgridview);
        mytopicgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        myadapter = new TopicAdapter(this);
        myadapter.update();
        mytopicgridview.setAdapter(myadapter);
        mytopicgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    intent.putExtra("position", "2");
                    intent.putExtra("ID", position);
                    startActivity(intent);
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    public class TopicAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public TopicAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        @Override
        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_photo, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.img_photo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }
            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        myadapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    protected void onRestart() {
        myadapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    private void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_comment_send: //发表的点击事件
                String content = mCommentEdittext.getText().toString(); //输入框内的内容
                //点击事件太频繁
                if (System.currentTimeMillis() - exitTime > 3000) {
                    exitTime = System.currentTimeMillis();
                    if (isReply) { //是回复
                        if (!content.equals("")) {
                            AddRepThread addRepThread = new AddRepThread();
                            addRepThread.start();
                            mytopicgridview.setVisibility(View.GONE);//将网格视图隐藏
                            mytext.setVisibility(View.GONE);        //将提示信息隐藏
                            commetn_lyt.setVisibility(View.GONE); //将回复评论的输入框隐藏
                        } else {
                            Toast.makeText(mContext, "内容不能为空!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!content.equals("")) { //评论
                            Log.d("onClick", "发表评论。。。。。。。。。。。");

                            AddComThread addComThread = new AddComThread();
                            addComThread.start();

                            Message msg = new Message();
                            msg.obj = content;
                            handlercomment.sendMessage(msg);
                            commetn_lyt.setVisibility(View.GONE); //将回复的输入框隐藏
                        } else {
                            Toast.makeText(mContext, "内容不能为空!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "发表太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.lyt_comment:
                isReply = false;
                onFocusChange(true);
                commetn_lyt.setVisibility(View.VISIBLE);
                mytopicgridview.setVisibility(View.VISIBLE);//将网格视图显示
                mytext.setVisibility(View.VISIBLE);        //将提示信息显示
                break;
            case R.id.Topic_text:
                isReply = false;
                onFocusChange(true);
                commetn_lyt.setVisibility(View.VISIBLE);
                mytopicgridview.setVisibility(View.VISIBLE);//将网格视图显示
                mytext.setVisibility(View.VISIBLE);        //将提示信息显示
                break;
        }
    }


    @Override
    protected void onPause() {
        if (myOnfreshTask!=null&&myOnfreshTask.getStatus()==AsyncTask.Status.RUNNING){
            //cancel方法只是将对应的AsyncTask标记为cancel状态，并不是真正的取消线程的执行
            myOnfreshTask.cancel(true);
        }
        if (onloadTask!=null&&onloadTask.getStatus()==AsyncTask.Status.RUNNING){
            myOnfreshTask.cancel(true);

        }
        super.onPause();
    }

    //第一次进入自动刷新
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    //下拉刷新
    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                onRefreshThread onrefreshthread = new onRefreshThread();
//                onrefreshthread.start();
                /**
                 * 开启一个异步线程执行下拉刷新操作
                 */
                myOnfreshTask=new MyOnfreshTask();
                myOnfreshTask.execute();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    //上拉加载
    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                onLoadMoreThread onloadthread = new onLoadMoreThread();
//                onloadthread.start();

                /**
                 * 开启一个异步线程执行上拉加载操作
                 */
                onloadTask=new OnloadTask();
                onloadTask.execute();
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                count=mListData.getCount();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

//    //下拉刷新的thread
//    class onRefreshThread extends Thread {
//        @Override
//        public void run() {
//            Log.v("zms", "下拉刷新评论线程启动");
//            Message msg1 = handler.obtainMessage();
//            msg1.what = 0;
//            try {
//                Log.d("下拉刷新线程", "topicId=" + topicid);
//                msg1.obj = getcellutil.getHttpJsonByhttpclient(HttpUtil.TopicUrl + "?topicId=" + topicid);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            handler.sendMessage(msg1);
//            super.run();
//        }
//    }
//
//    //上拉加载的thread
//    class onLoadMoreThread extends Thread {
//        @Override
//        public void run() {
//            Log.v("zms", "上拉加载评论启动");
//            Message msg1 = handler.obtainMessage();
//            msg1.what = 1;
//            try {
//                msg1.obj = getcellutil.getHttpJsonByhttpclient(HttpUtil.TopicUrl + "?operation=up" + "&topicId=" + topicid + "&position=" + mListData.getCount());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            handler.sendMessage(msg1);
//            super.run();
//        }
//    }

    //回复的thread类
    class AddRepThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            Map<String, String> params = new HashMap<String, String>();
            params.put("content", mCommentEdittext.getText().toString());
            params.put("userid", userid);
            params.put("commentId", commentId);
            params.put("isIOS", "0");
            try {
                msg.obj = HttpGetUtil.httpPost(HttpUtil.addRepUrl, params);
                handlerreplay.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    /**
     * 评论的thread类
     */
    class AddComThread extends Thread {
        @Override
        public void run() {
            super.run();
            Message msg = new Message();
            try {
                msg.what = 2;
                Integer count = 0;
                for (File file : Bimp.tempFile) {
                    count++;
                    files.put("file" + count, file);
                    params.put("filename" + count, file.getName());
                }
                params.put("fileNUM", count + "");
                params.put("content", mCommentEdittext.getText().toString());
                params.put("userid", userid);
                params.put("topicId", topicid + "");
                msg.obj = UploadUtil.post(HttpUtil.addComUrl, params, files);
                handlercomment.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        boolean isFirstIn = spf.getBoolean("isFirstIn", true);
        String commentString = spf.getString("" + topicid, "");//获取到话题对应的评论及回复的缓存
//        Log.d("k1k1k1k1k1k1", "" + spf.getLong("topicId", (0)));
        Log.d("Method-OnStart", "传入的topicId=" + topicid);
        Log.d("Method-OnStart", commentString);
        if (isFirstIn) {
            ptrltopic.autoRefresh();
            isFirstIn = false;
            SharedPreferences.Editor editor = spf.edit();
            editor.putBoolean("isFirstIn", isFirstIn);
//            editor.putLong("topicId", topicid);
            editor.commit();
            Log.d("Method-OnStart", "第一次进行该界面");
        } else {
            if (HttpUtil.isOpenImg == true) {
                HttpUtil.isOpenImg = false;
                Log.d("Method-OnStart", "放大了图片，不进行刷新");
            } else if (HttpUtil.isopenCame || HttpUtil.isOpenPhoto) {
                HttpUtil.isopenCame = false;
                HttpUtil.isOpenPhoto = false;
            } else if (commentString.equals("")) {
                ptrltopic.autoRefresh();
                Log.d("Method-OnStart", "该话题的缓存为空");
            } else {
                Log.d("Method-OnStart", "该话题的缓存不为空");

                commentsData.clear();
                try {
                    JSONArray jsonArray = new JSONArray(commentString);
                    Log.d("Method-OnStart", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        IComments iComment = new IComments();
                        iComment.setContent(jsonArray.optJSONObject(i).get("content").toString());
                        iComment.setId(jsonArray.optJSONObject(i).getLong("id"));
                        iComment.setClock((Integer) jsonArray.optJSONObject(i).get("clock"));
                        iComment.setUserid(jsonArray.optJSONObject(i).getLong("userid"));
                        iComment.setUsername(jsonArray.optJSONObject(i).getString("username"));
                        iComment.setTopicid(jsonArray.optJSONObject(i).getLong("topicid"));
                        iComment.setHeadUrl(jsonArray.optJSONObject(i).getString("headUrl"));
                        JSONArray UrlsString = jsonArray.optJSONObject(i).getJSONArray("urlList");
                        List<String> urlList = new ArrayList<String>();
                        if (UrlsString.toString().equals("") || UrlsString.length() == 0) {
                            iComment.setUrlList(urlList);
                        } else {
                            for (int k = 0; k < UrlsString.length(); k++) {
                                urlList.add(UrlsString.get(k) + "");
                            }
                            iComment.setUrlList(urlList);
                        }
                        JSONArray replyString = jsonArray.optJSONObject(i).getJSONArray("replys");
                        Log.d("Method-OnStart", "replyString:" + replyString.toString());
                        List<IReplys> replys = new ArrayList<IReplys>();
                        if (replyString.toString().equals("") || replyString.length() == 0) {
                            iComment.setReplys(replys);
                        } else {
                            for (int k = 0; k < replyString.length(); k++) {
                                IReplys iReply = new IReplys();
                                iReply.setContent(replyString.optJSONObject(k).get("content").toString());
                                iReply.setId(replyString.optJSONObject(k).getLong("id"));
                                iReply.setClock((Integer) replyString.optJSONObject(k).get("clock"));
                                iReply.setUserid(replyString.optJSONObject(k).getLong("userid"));
                                iReply.setUsername(replyString.optJSONObject(k).getString("username"));
                                iReply.setCommentid(replyString.optJSONObject(k).getLong("commentid"));
                                replys.add(iReply);
                            }
                            iComment.setReplys(replys);
                        }
                        commentsData.add(iComment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                commentadapter = new CommentAdapter(this, commentsData, ComHandler);
                mListData.setAdapter(commentadapter);
                commentadapter.notifyDataSetChanged();
                if (Bimp.tempSelectBitmap != null && Bimp.tempSelectBitmap.size() > 0) {
                    for (ImageItem imageItem : Bimp.tempSelectBitmap) {
                        Log.d("Method-OnStart", imageItem.getImagePath());
                    }
                }
            }
        }
        super.onStart();
    }


    /**
     * 下拉刷新的异步处理
     */
    class MyOnfreshTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject = null;
            try {
                String result = getcellutil.getHttpJsonByhttpclient(HttpUtil.TopicUrl + "?topicId=" + topicid);
                jsonObject=new JSONObject(result);
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
            if (isCancelled()){ //取消线程
                return;
            }
            try {
            if (System.currentTimeMillis() - exitTime > 1000) {
                        exitTime = System.currentTimeMillis();
                        if (!commentsData.isEmpty()) {
                            commentsData.clear();
                        }
                        JSONArray jsonArray1 = jsonObject.getJSONArray("JsonArry");
                        if (jsonArray1.getJSONObject(0).opt("message") != null) {
                            Log.d("pullHandler", "error");
                            Toast.makeText(mContext, "没有评论，来做第一个评论的人吧", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("pullHandler", "refreshing");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObjectSon = (JSONObject) jsonArray1.opt(i);
                                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
                                IComments coment = (IComments) JSONToObj(jsonObjectSon.toString(), IComments.class);
                                commentsData.add(coment);
                            }
                            Log.d("pullHandler", jsonArray1.toString());
                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString(topicid + "", jsonArray1.toString());
                            editor.commit();
                            commentadapter = new CommentAdapter(mContext, commentsData, ComHandler);
                            mListData.setAdapter(commentadapter);
                            commentadapter.notifyDataSetChanged();
                            Log.d("pullHandler", "refreshing");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "下拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
    /**
     * 上拉加载的异步处理
     */
    class OnloadTask extends AsyncTask<Map, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject1 = null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.TopicUrl + "?operation=up" + "&topicId=" + topicid + "&position=" + count);
                jsonObject1=new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject1;
        }
        /**
         * 进行更新UI操作
         *
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){ //取消线程
                return;
            }
            try {
            if (System.currentTimeMillis() - exitTime > 1000) {
                        exitTime = System.currentTimeMillis();
//                        if (!commentsData.isEmpty()) {
//                            commentsData.clear();
//                        }
                        Log.d("pullHandler", "up");
                        JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                        if (jsonArray.getJSONObject(0).opt("message") != null) {
                            Log.d("pullHandler", "error");
                            Toast.makeText(mContext, "没有评论咯", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
                                if (i == 0) {
                                    Log.d("pullHandler", jsonObject.toString());
                                }
                                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonObjectSon.toString());
                                IComments coment = (IComments) JSONToObj(jsonObjectSon.toString(), IComments.class);
                                commentsData.add(coment);
                            }
                            commentadapter = new CommentAdapter(mContext, commentsData, ComHandler);
                            commentadapter.notifyDataSetChanged();
                            Log.d("pullHandler", "refreshing");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "上拉太过频繁，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
}
