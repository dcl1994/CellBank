package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smsdemo.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by szjdj on 2016-12-21.
 */
public class TeamAdapter  extends BaseAdapter{
    private Context context;
    private ImageView mimage;
    private TextView mtext;

    JSONArray jsonArray;

    public TeamAdapter(Context context,JSONArray jsonArray){
        this.context=context;
        this.jsonArray=jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view= LayoutInflater.from(context).inflate(R.layout.teaminflate,null);
        mimage= (ImageView) view.findViewById(R.id.imgload);
        mtext= (TextView) view.findViewById(R.id.teamcontent);
        try {
            mtext.setText(jsonArray.getJSONObject(position).opt("content").toString().replace("\\n","\n"));

            /**
             * 使用glide加载图片
             */
            Glide.with(context).load(jsonArray.getJSONObject(position).opt("picture").toString()).into(mimage);

            //ImageLoad.loadImg(jsonArray.getJSONObject(position).opt("picture").toString(), mimage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
