package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsdemo.R;
import com.example.util.ChangeTime;
import com.example.util.ImageLoad;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 健康小知识的adapter
 */
public class IndestryAdapter extends BaseAdapter {
    private  Context context;
    JSONArray jsonArray;
    public IndestryAdapter(Context context,JSONArray jsonArray){
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
            return null;
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertview==null){
            view= LayoutInflater.from(context).inflate(R.layout.indestry,null);
            viewHolder=new ViewHolder();
            viewHolder.myimag=(ImageView) view.findViewById(R.id.title_img);
            viewHolder.titletext= (TextView) view.findViewById(R.id.title_tv);
            viewHolder.cmttext= (TextView) view.findViewById(R.id.textview_coment);
            viewHolder.time= (TextView) view.findViewById(R.id.time);
            view.setTag(viewHolder);    //将ViewHolder存储在view中
        }else {
            view = convertview;
            viewHolder = (ViewHolder) view.getTag(); //重新获取ViewHolder
        }
        try {
            //获取时间
            String ontime = ChangeTime.TimeStamp2Date(jsonArray.getJSONObject(position).opt("clock").toString(), "yyyy-MM-dd HH:mm:ss");
            //获取图片
            String imgurl = jsonArray.getJSONObject(position).opt("picture").toString();
            ImageLoad.loadImg(imgurl, viewHolder.myimag);
       //     viewHolder.myimag.setImageResource();
            viewHolder.titletext.setText(jsonArray.getJSONObject(position).opt("title").toString());
            viewHolder.cmttext.setText(jsonArray.getJSONObject(position).opt("content").toString());
            viewHolder.time.setText(ontime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    class ViewHolder{
        ImageView myimag;   //图片
        TextView titletext; //标题
        TextView cmttext;   //内容
        TextView time;      //时间
    }

}
