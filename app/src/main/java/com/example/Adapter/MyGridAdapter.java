package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smsdemo.R;
import com.example.util.BaseViewHolder;

/**
 * Created by szjdj on 2016-09-28.
 *
 */
public class MyGridAdapter extends BaseAdapter {

    private Context mContext;

    //更多功能里面放上 公司介绍
    public String[] img_text={"我的细胞","我的社群","专家团队","专家咨询","临床案例","公开课","产品介绍","行业动态"};

    public int[] imgs={R.drawable.test_img1,R.drawable.test_img05,R.drawable.test_img03, R.drawable.test_img04,R.drawable.test_img02,R.drawable.test_img08,R.drawable.test_img07,R.drawable.test_img06};
    public MyGridAdapter(Context mContext){
        super();
        this.mContext=mContext;
    }
    //决定了有多少个items
    @Override
    public int getCount() {
        return img_text.length;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView==null){

            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, viewGroup, false);
        }
        TextView tv= BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv=BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);
        tv.setText(img_text[position]);
        return convertView;
    }
}
