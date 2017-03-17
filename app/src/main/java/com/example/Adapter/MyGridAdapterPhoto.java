package com.example.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.Activity.SelectBigImg;
import com.example.smsdemo.R;
import com.example.util.HttpUtil;

import java.util.List;

/**
 * Created by szjdj on 2016-11-12.
 */
public class MyGridAdapterPhoto extends BaseAdapter {
    private Context mContext;
    private List<String> list;
    String a;
    private Bitmap bitmap;

    public MyGridAdapterPhoto(Context mContext, List<String> list) {
        super();
        this.mContext = mContext;
        this.list = list;
    }

    //决定了有多少个items
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        a = list.get(position).toString(); //图片对象
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_photo, viewGroup, false);
            ImageView iv = (ImageView) convertView.findViewById(R.id.img_photo); //获取图片
            Log.e("图片", a);
            //ImageLoad.loadImg(a, iv);

            /**
             * 使用glide加载图片
             */
            Glide.with(mContext).load(a).into(iv);


            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SelectBigImg.class);
                    //跳转将图片传递过去
                    a = list.get(position).toString(); //图片对象
                    intent.putExtra("url", a);
                    HttpUtil.isOpenImg = true;
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }
}
