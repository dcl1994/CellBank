package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.CustomDialog;
import com.example.widget.PopupMenu;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 群详细介绍页面。
 * 1：拿到groupId:
 * 2：通过点击事件将groupId传递到聊天界面
 * 3：调用获取群信息接口，拿到群组的详细信息显示到页面上，在onstart中处理
 */
public class GroupDetailsActivity extends Activity {
    private String groupId = null;
    private Button mybtn;
    private Context mContext;
    private TextView groupname; //群名称
    private TextView groupdes;  //群简介
    EMGroup group = null;
    private PopupMenu mPopupMenu;
    private TextView mytextview;
    String username=null;
    private final String PREFERENCES_NAME = "userinfo";
    String expert;  //从缓存中获取的专家列表项
    private LinearLayout mylinearLayout;
    private String owner="";    //群主
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        mContext = GroupDetailsActivity.this;

        /**
         * 返回上一个界面
         */
        mytextview= (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mylinearLayout= (LinearLayout) findViewById(R.id.toolbar_menu);


        // initialize popup menu (force measure to get width)
        View menuLayout = getLayoutInflater().inflate(R.layout.toolbar_menu, null);
        menuLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupMenu = new PopupMenu((ViewGroup) menuLayout);
        mPopupMenu.setMenuItemBackgroundColor(0xffb1df83);
        mPopupMenu.setMenuItemHoverBackgroundColor(0x22000000);
        /**
         * 子菜单的点击事件
         */
        mPopupMenu.setOnMenuItemSelectedListener(new PopupMenu.OnMenuItemSelectedListener() {
            @Override
            public void onMenuItemSelected(View menuItem) {
                switch (menuItem.getId()) {
                    case R.id.menu01:
                        Intent intent = new Intent(mContext, ChangeGroupMsg.class);
                        intent.putExtra("owner",owner);
                        intent.putExtra("groupId",groupId);
                        Log.e("gd_group_owner",owner);
                        startActivity(intent);
                        break;
                    case R.id.menu02:
                        Intent intent2 = new Intent(mContext, UserControlActivity.class);
                        intent2.putExtra("groupId",groupId);
                        startActivity(intent2);
                        break;
                    case R.id.menu03:
                        //弹出dialog，点击确定跳转到登录界面(暂时这样写)
                        CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                        builder.setTitle("提示");
                        builder.setMessage("确定删除此群组？");
                        //添加AlertDialog.Builder对象的setPositiveButton()方法
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                DeleteGroupAsyncTask deleteGroupAsyncTask = new DeleteGroupAsyncTask();
                                deleteGroupAsyncTask.execute();
                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
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
                        break;
                }
            }
        });
        // show or dismiss popup menu when clicked
        final float offsetX = 0;
        final float offsetY = 0;
        final float menuWidth = menuLayout.getMeasuredWidth();
        final View menu = findViewById(R.id.toolbar_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupMenu.isShowing()) {
                    mPopupMenu.dismiss();
                } else {
                    // based on bottom-left, need take menu width and menu icon width into account
                    mPopupMenu.show(menu, (int) (menu.getWidth() - offsetX - menuWidth), (int) offsetY);
                }
            }
        });
        groupname = (TextView) findViewById(R.id.groupname); //群名称
        groupdes = (TextView) findViewById(R.id.groupdes);//群描述

        mybtn = (Button) findViewById(R.id.joingroup);
        mybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 跳转到聊天界面
                 */
                Intent intent1 = new Intent(mContext, ChatActivity.class);
                intent1.putExtra("groupId",groupId);
                startActivity(intent1);
            }
        });
        /**
         * 获取username
         */
        SharedPreferences pref = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        username=pref.getString("Phone","");
        Log.e("gd_username", username);
    }

    /**
     * 根据传递过来的groupid，调用获取群信息接口获取群的群名称和群简介
     */
    @Override
    protected void onStart() {
        /**
         * 获取缓存中的groupId
         */
//        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
//        groupId=pref.getString("groupId","");

//
        groupId=getIntent().getStringExtra("groupId");  //获取传递过来的groupID
        Log.e("groupId", groupId);
        //根据群组ID从服务器获取群组基本信息
        GetgroupAsyncTask getgroupAsyncTask = new GetgroupAsyncTask();
        getgroupAsyncTask.execute();
        /**
         * 获取专家列表和username进行对比
         */
        Intent intent=getIntent();
        owner=intent.getStringExtra("owner");
        if (owner.equals(username)){
            mylinearLayout.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    /**
     * 删除群组的异步处理
     */
    class DeleteGroupAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                if(!TextUtils.isEmpty(groupId)) {
                    EMClient.getInstance().groupManager().destroyGroup(groupId);//需异步处理
                }else {
                    Log.e("gd_DeleteGroupAsyncTask","fail");
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
            super.onPostExecute(aVoid);
        }
    }
    /**
     * 获取群组详细信息，包括群组名称，群组简介使用异步线程
     */
    class GetgroupAsyncTask extends AsyncTask<String, Void, EMGroup> {
        @Override
        protected EMGroup doInBackground(String... params) {
            try {
                group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return group;
        }
        @Override
        protected void onPostExecute(EMGroup group) {
            Log.e("gd_phone",username);
            Log.e("gd_group.getOwner", group.getOwner());

            if (username.equals(group.getOwner())){
                Log.e("=","相等");
            }
            if (username.equals(owner)){
                Log.e("=","相等");
            }

            Log.e("gd_backServer",owner);
            groupname.setText(group.getGroupName());
            Log.e("gd_groupname", group.getGroupName());
            groupdes.setText(group.getDescription());//群简介
            Log.e("gd_groupdes", group.getDescription());
            super.onPostExecute(group);
        }
    }
}
