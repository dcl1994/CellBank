package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smsdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

public class ChangeGroupMsg extends Activity {
    private Context mContext;
    private EditText mychangname;    //群名称
    private EditText mychangedes;
    private String groupId=null;
    private Button mychange;
    private TextView mytextview;    //返回上一个页面
    String changedGroupName;     //新的群名称
    String changedGroupDescription; //新的群简介
    private SharedPreferences pref;
    private String username;
    private final String PREFERENCES_NAME = "userinfo";
    EMGroup group = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_msg);
        mContext = ChangeGroupMsg.this;

        mychangname = (EditText) findViewById(R.id.changname);    //群名称
        mychangedes = (EditText) findViewById(R.id.changedes);    //群简介
        /**
         * 获取username
         */
        pref = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        username = pref.getString("Phone", "");
        Log.e("cg_username", username);


        mytextview = (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); //返回上一个页面


        /**
         * 修改群信息的时候先获取群组详细内容，然后在修改群名称和群简介
         */
        mychange = (Button) findViewById(R.id.change);
        mychange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //根据群组ID从服务器获取群组基本信息
//                GetgroupAsyncTask getgroupAsyncTask = new GetgroupAsyncTask();
//                getgroupAsyncTask.execute();
                /**
                 * 修改群名称和群简介，直接判空就行了，为空就不调用相关异步处理类
                 */
                changedGroupName = mychangname.getText().toString(); //拿到输入的新群名称
                changedGroupDescription = mychangedes.getText().toString();   //拿到新的群简介
                if (TextUtils.isEmpty(changedGroupName)) {
                } else {
                    ChangeGroupNameAsyncTask changeGroupNameAsyncTask = new ChangeGroupNameAsyncTask();
                    changeGroupNameAsyncTask.execute();
                }
                if (TextUtils.isEmpty(changedGroupDescription)) {
                } else {
                    ChangeGroupDesAsyncTask changeGroupDesAsyncTask = new ChangeGroupDesAsyncTask();
                    changeGroupDesAsyncTask.execute();
                }
                /**
                 * 跳转到专家咨询界面
                 */
                Intent intent1 = new Intent(mContext, FeedBack.class);
                startActivity(intent1);
            }
        });
    }


    @Override
    protected void onStart() {
        /**
         * 获取缓存中的groupId
         */
//        pref = getSharedPreferences("data", MODE_PRIVATE);
//        groupId = pref.getString("groupId", "");
        groupId=getIntent().getStringExtra("groupId");  //获取从群组列表界面传递过来的groupID
        Log.e("cg_groupid",groupId);
        super.onStart();
    }

    /**
     * 修改群名称的异步处理类
     */
    class ChangeGroupNameAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //groupId 需要改变名称的群组的id
            //changedGroupName 改变后的群组名称
            try {
                EMClient.getInstance().groupManager().changeGroupName(groupId, changedGroupName);//需异步处理
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    /**
     * 修改群简介的异步处理
     */
    class ChangeGroupDesAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                EMClient.getInstance().groupManager().changeGroupDescription(groupId, changedGroupDescription);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


//    /**
//     * 获取群组详细信息，包括群组名称，群组简介使用异步线程
//     */
//    class GetgroupAsyncTask extends AsyncTask<String, Void, EMGroup> {
//        @Override
//        protected EMGroup doInBackground(String... params) {
//            try {
//                group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
//            } catch (HyphenateException e) {
//                e.printStackTrace();
//            }
//            return group;
//        }
//
//        @Override
//        protected void onPostExecute(EMGroup group) {
//            Log.e("cg_groupname", group.getGroupName());
//            Log.e("cg_groupdes", group.getDescription());
//            Log.e("cg_owner", group.getOwner());
//            super.onPostExecute(group);
//        }
//    }

}
