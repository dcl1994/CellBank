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
 * Created by szjdj on 2016-10-10.
 */
public class MyGridAdapter2 extends BaseAdapter{
    private Context mContext;


    public String[] img_text={"我的细胞","我的社群","专家团队","专家咨询","临床案例","公开课","行业动态"};
    public int[] imgs={R.drawable.test_img1,R.drawable.test_img05,R.drawable.test_img03, R.drawable.test_img09,R.drawable.test_img02,R.drawable.test_img03,R.drawable.test_img06};
    public MyGridAdapter2(Context mContext){
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
