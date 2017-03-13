package com.example.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smsdemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by szjdj on 2017-02-20.
 */
public class ListViewAdapter extends BaseAdapter {

    private String owner;   //群主
    private String groupname;   //群组名称

    private String groupid;     //群组ID
    private Context context;

    private String username;    //用户名
    private JSONArray groupsList=new JSONArray();

    public  ListViewAdapter(Context context,JSONArray groupsList,String username){
        this.context=context;
        this.groupsList = groupsList;
        this.username=username;
    }
    @Override
    public int getCount() {
        return groupsList.length();}

    @Override
    public Object getItem(int position) {
        try {
            return  groupsList.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view= LayoutInflater.from(context).inflate(R.layout.listview_item,null);
        TextView group_username = (TextView) view.findViewById(R.id.group_username);

        JSONObject jsonson;
        try {
            jsonson = groupsList.getJSONObject(position);
            owner=jsonson.getString("owner");
            groupname=jsonson.getString("groupname");
            Log.e("owner", owner);          //群主
            Log.e("groupname", groupname);   //群名称

            String group_owner=owner.replace("1182170210178310#lifebank_","");
            Log.e("group_owner", group_owner);
            group_username.setText(groupname);
            if (group_owner.equals(username)){
                Log.e("是群主", "group_owner.equals(username)");
                group_username.setTextColor(0xFF0B5797);
                group_username.setText(groupname + "(我创建的群)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
