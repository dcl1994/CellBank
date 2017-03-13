package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.MsgAdapter;
import com.example.smsdemo.R;
import com.example.util.Msg;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 聊天界面
 */
public class ChatActivity extends Activity implements EMMessageListener {
    private List<Msg> msgList = new ArrayList<>();
    private String groupId = null;        //groupid，群组ID
    private Context mContext;
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String username = "";
    List<String> list;
    private int local_msg;      //本地消息总数
    private TextView mytextview;    //返回
    Set<String> stringSet;

    private String expert=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = ChatActivity.this;
        /**
//         * 从缓存中获取groupid
//         */
//        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
//        groupId = pref.getString("groupId", "");
//        Log.e("groupId", groupId);

        groupId=getIntent().getStringExtra("groupId");  //获取传递过来的groupID
        Log.e("groupId", groupId);

        /**
         * 从缓存中获取username
         */
        SharedPreferences sharedPreferences = getSharedPreferences("userinfo", MODE_PRIVATE);
        username = sharedPreferences.getString("Phone", "");
        Log.e("Phone", username);

        /**
         * 从缓存中获取专家专家列表中的所有专家
         */
        SharedPreferences prefexpert = getSharedPreferences("data", MODE_PRIVATE);
        stringSet = prefexpert.getStringSet("experts_list", null);
         for(String s:stringSet){
            expert =s;
            Log.e("expert",expert);
        }
        /**
         * 返回上一个页面
         */
        mytextview = (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initMsgs();
        initview();
    }

    private void initview() {
        inputText = (EditText) findViewById(R.id.input_text);

        send = (Button) findViewById(R.id.send);

        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        msgRecyclerView.setLayoutManager(layoutManager);

        adapter = new MsgAdapter(msgList,stringSet);

        msgRecyclerView.setAdapter(adapter);

        //发送消息的点击事件
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id
                    EMMessage message = EMMessage.createTxtSendMessage(content, groupId);
                    //设置为群聊，
                    // Chat 单聊
                    // GroupChat 群聊
                    // ChatRoom  聊天室
                    message.setChatType(EMMessage.ChatType.GroupChat);
                    EMClient.getInstance().chatManager().sendMessage(message);
                    message.setMessageStatusCallback(new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.i("long", "消息发送成功");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.i("long", "消息发送失败" + i + "," + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {
                        }
                    });
                    //发送消息
                    Msg msg = new Msg(content, Msg.TYPE_SENT, username);
                    msgList.add(msg);
                    //当有新消息时，刷新RecyclerView中的显示
                    adapter.notifyItemInserted(msgList.size() - 1);
                    //将RecyclerView定位到最后一行
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                }
            }
        });
    }
    /**
     * 消息初始化
     */
    private void initMsgs() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupId, EMConversation.EMConversationType.GroupChat, true);
        //获取此会话在本地的所有的消息数量
        local_msg = conversation.getAllMsgCount();
        if (local_msg == 0) {
            Toast.makeText(mContext, "没有消息", Toast.LENGTH_SHORT).show();
        } else {
            //获取此会话的所有消息
            List<EMMessage> messages = conversation.loadMoreMsgFromDB("", local_msg);//第一个参数为起始id，第二个参数为显示的消息数目
            Log.e("total", messages.size() + "");
            for (EMMessage message : messages) {
                String message1 = ((EMTextMessageBody) message.getBody()).getMessage();
                /**
                 * 获取接收方的名称
                 */
                if (username.equals(message.getFrom())) {
                    Msg msg1 = new Msg(message1, Msg.TYPE_SENT, message.getFrom());
                    msgList.add(msg1);
                }else{
                    Msg msg1 = new Msg(message1, Msg.TYPE_RECEIVED, message.getFrom());    //接收方（显示专家）
                    msgList.add(msg1);
                }
                Log.e("msg.from", message.getFrom());
                Log.e("msg.to", message.getTo());
                Log.e("msg.username", message.getUserName());
                Log.e("body", message1.toString());
                Log.e("bodyid", message.getMsgId());
                Log.e("expert",stringSet+"");
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        /**
         * 接收消息
         */
        for (EMMessage message : list) {
            EMTextMessageBody textbody = (EMTextMessageBody) message.getBody();
            Msg msg = new Msg(textbody.getMessage().toString(), Msg.TYPE_RECEIVED, message.getFrom());
            msgList.add(msg);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
            }
        });
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }
}
