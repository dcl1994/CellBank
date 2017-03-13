package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 添加群组界面
 */
public class AddGroupActivity extends Activity {
    private EditText name;  //名称
    private EditText describe;  //描述
    private EditText usernumber;    //成员数量
    private Button setupGroup;         //创建群
    private Context mContext;
    private TextView mytextview;    //返回
    private String groupId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        mContext=AddGroupActivity.this;
        mytextview= (TextView) findViewById(R.id.text_back);

        //拿到传递过来的groupID
        groupId= getIntent().getStringExtra("groupId");


//        /**
//         * 从缓存中获取groupid
//         */
//        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
//        groupId=pref.getString("groupId","");
//        Log.e("groupId", groupId);
        /**
         * 返回上一个页面
         */
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }
    /**
     * 初始化
     */
    private void init() {
        name = (EditText) findViewById(R.id.groupname);
        describe = (EditText) findViewById(R.id.groupdescribe);
        usernumber = (EditText) findViewById(R.id.Maxusernumber);
        setupGroup = (Button) findViewById(R.id.set_up);
        setupGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupname = name.getText().toString();
                String groupdescribe = describe.getText().toString();
                String Maxusernumber= usernumber.getText().toString();
                if (TextUtils.isEmpty(groupname)){
                    Toast.makeText(mContext, "群组名称不能为空", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(groupdescribe)){
                    Toast.makeText(mContext,"群组简介不能为空",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Maxusernumber+"")){
                    Toast.makeText(mContext,"最大成员数不能为空",Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(Maxusernumber)>1000){
                    Toast.makeText(mContext,"最大成员数不能大于1000",Toast.LENGTH_SHORT).show();
                }else {
                    /**
                     * 调用创建群接口
                     */
                    /**
                     * 创建群组
                     * @param groupName 群组名称
                     * @param desc 群组简介
                     * @param allMembers 群组初始成员，如果只有自己传空数组即可
                     * @param reason 邀请成员加入的reason
                     * @param option 群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}
                     * @return 创建好的group
                     * @throws HyphenateException
                     */
                    int a=Integer.parseInt(Maxusernumber);
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = a;    //最大用户数
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    try {
                        EMClient.getInstance().groupManager().createGroup(groupname, groupdescribe, new String[1], "", option);
                        finish();
                        Toast.makeText(mContext,"聊天群创建成功",Toast.LENGTH_SHORT).show();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



}
