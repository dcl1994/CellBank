package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * 用户管理
 */
public class UserControlActivity extends Activity implements View.OnClickListener{
    private EditText myEdittext; //username
    private Button Shielbtn;   //拉黑
    private Button removeBlock;  //    移除黑名单
    private Button getBlock;   //获取黑名单

    private String username;
    private String groupId = null;   //groupid，群组ID
    private Context mContext;
    List<String> list;      //黑名单列表
    private ListView mylistview;    //装黑名单的listview
    private TextView mytextview;    //返回上一个页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_control);
//        /**
//         * 获取缓存中的groupId
//         */
//        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
//        groupId=pref.getString("groupId","");
        groupId=getIntent().getStringExtra("groupId");  //获取传递过来的groupID
        Log.e("usc_groupId", groupId);
        mContext = UserControlActivity.this;
        init();
    }
    /**
     * 初始化view
     */
    private void init() {
        myEdittext = (EditText) findViewById(R.id.username);
        myEdittext.setOnClickListener(this);
        mytextview= (TextView) findViewById(R.id.text_back);
        mytextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mylistview= (ListView) findViewById(R.id.deletelistview);
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myEdittext.setText(list.get(position));
            }
        });
        Shielbtn = (Button) findViewById(R.id.shielding);    //拉黑
        Shielbtn.setOnClickListener(this);

        removeBlock = (Button) findViewById(R.id.removeBlock);   //移除黑名单
        removeBlock.setOnClickListener(this);

        getBlock = (Button) findViewById(R.id.getBlock);     //获取黑名单
        getBlock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        username = myEdittext.getText().toString();    //获取用户输入的用户名
        switch (v.getId()) {
            //拉黑用户
            case R.id.shielding:
                if (!TextUtils.isEmpty(username)){
                ShieldingUserAsyncTask shieldingUserAsyncTask = new ShieldingUserAsyncTask();
                shieldingUserAsyncTask.execute();
                }else {
                    Toast.makeText(mContext,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            //获取黑名单列表
            case R.id.getBlock:
                GetBlockAsyncTask getBlockAsyncTask = new GetBlockAsyncTask();
                getBlockAsyncTask.execute();
                break;
            //移除黑名单列表
            case R.id.removeBlock:
                if (!TextUtils.isEmpty(username)){
                    RemoveBlockAsyncTask removeBlockAsyncTask = new RemoveBlockAsyncTask();
                    removeBlockAsyncTask.execute();
                }else {
                    Toast.makeText(mContext,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 移除黑名单
     */
    class RemoveBlockAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                EMClient.getInstance().groupManager().unblockUser(String.valueOf(groupId), username);//需异步处理
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("usc_delete", "移除黑名单");
            Toast.makeText(mContext, "移除黑名单成功", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }
    /**
     * 获取黑名单列表
     */
    class GetBlockAsyncTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List doInBackground(String... params) {
            try {
                list = EMClient.getInstance().groupManager().getBlockedUsers(groupId);//需异步处理
                Log.e("usc_getblock", "获取黑名单成功");
            } catch (HyphenateException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            if (list.isEmpty()) {
                Toast.makeText(mContext, "黑名单没有数据", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "获取黑名单成功", Toast.LENGTH_SHORT).show();
                for (String s : list) {
                    Log.e("list", s + "");
                }
                //使用简单适配器存放黑名单用户
                ArrayAdapter adapter1 = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, list);
                mylistview.setAdapter(adapter1);
                super.onPostExecute(list);
            }
        }
    }
    /**
     * 拉黑
     */
    class ShieldingUserAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                EMClient.getInstance().groupManager().blockUser(String.valueOf(groupId), username);//需异步处理
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("usc_Shielding", "拉黑成功");
            Toast.makeText(mContext, "拉黑用户成功", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }

    }

}
