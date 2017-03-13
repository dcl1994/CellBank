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
 * Created by szjdj on 2016-12-23.
 * 临床案例的adapter
 */
public class ClinOpenAdapter extends BaseAdapter {

    private Context context;
    private ImageView myimag;   //图片
    private TextView titletext; //标题
    private TextView  cmttext;  //内容
    private TextView time;      //时间

    JSONArray jsonArray;
    public ClinOpenAdapter(Context context,JSONArray jsonArray){
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
    public View getView(int position, View convertview, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertview==null){
            if (position%2==0){
                view= LayoutInflater.from(context).inflate(R.layout.single,null);
            }else{
                view=LayoutInflater.from(context).inflate(R.layout.double0,null);
            }
            viewHolder=new ViewHolder();
            viewHolder.myimag= (ImageView) view.findViewById(R.id.coimgload);  //图片
            viewHolder.titletext= (TextView) view.findViewById(R.id.cotext_title); //标题
            viewHolder.cmttext= (TextView) view.findViewById(R.id.coteamcontent); //内容
            viewHolder.time= (TextView) view.findViewById(R.id.cotime);        //时间
            view.setTag(viewHolder);    //将Viewholder存储在view中
        }else {
            view=convertview;
            viewHolder= (ViewHolder) view.getTag(); //重新获取viewholder
        }
        try {
            viewHolder.titletext.setText(jsonArray.getJSONObject(position).opt("title").toString());
            viewHolder.cmttext.setText(jsonArray.getJSONObject(position).opt("content").toString());
            //获取时间
            String ontime= ChangeTime.TimeStamp2Date(jsonArray.getJSONObject(position).opt("clock").toString(),"yyyy-MM-dd HH:mm:ss");
            viewHolder.time.setText(ontime);
            //获取图片
            String imgurl=jsonArray.getJSONObject(position).opt("picture").toString();
            ImageLoad.loadImg(imgurl,viewHolder.myimag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    class ViewHolder{
        ImageView myimag;
        TextView titletext;
        TextView cmttext;
        TextView time;
    }

}
