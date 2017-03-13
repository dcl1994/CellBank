package com.example.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.model.ITopics;
import com.example.smsdemo.R;
import com.example.util.ChangeTime;
import com.example.util.ImageLoad;
import com.example.util.MyGridView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    List<ITopics> topics;
    Context context;
    String page;

    public MyAdapter(Context context, List<ITopics> topics, String page) {
        this.context = context;
        this.topics = topics;
        this.page = page;
    }


    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public Object getItem(int position) {
        return topics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ImageView img_topichead;
        if (page.equals("话题")) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_layout2, null);
            Log.e("MyAdapter", "话题评论回复页");
            img_topichead = (ImageView) view.findViewById(R.id.img_topichead);

            final MyGridView mygridview;
            mygridview = (MyGridView) view.findViewById(R.id.topic_gridview);
            mygridview.setAdapter(new MyGridAdapterPhoto(context, topics.get(position).getUrlList()));
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_layout, null);
            Log.e("MyAdapter", "话题页");
            img_topichead = (ImageView) view.findViewById(R.id.heard_img);

            //找到imageview显示第一张图片
            final ImageView myimg;
            myimg = (ImageView) view.findViewById(R.id.community_img);
            if (topics.get(position).getUrlList() != null && topics.get(position).getUrlList().size() > 0) {
                myimg.setVisibility(View.VISIBLE);
                Log.e("MyAdapter", "" + topics.get(position).getUrlList().get(0));
                String firstimg = topics.get(position).getUrlList().get(0).toString();
                ImageLoad.loadImg(firstimg, myimg);
            }
        }
        TextView commentNum = (TextView) view.findViewById(R.id.topic_number_text);
        if (commentNum != null) {
            Log.d("MyAdapter", "找到了textview");
            if (topics.get(position).getCommentNum() != null) {
                Integer Num = topics.get(position).getCommentNum();
                commentNum.setText(Num + "");
                Log.d("MyAdapter", commentNum.getText().toString());
            }
        } else {
            Log.d("MyAdapter", "找不到textview");
        }
        TextView mContent = (TextView) view.findViewById(R.id.content);
        mContent.setText(topics.get(position).getContent().replace("\\n", "\n"));

        TextView mUsername = (TextView) view.findViewById(R.id.username);
        mUsername.setText(topics.get(position).getUsername());
        if (img_topichead != null) {
            if (topics.get(position).getHeadUrl() != null) {
                String headUrl = topics.get(position).getHeadUrl();
                if (!headUrl.equals("")) {
                    Log.e("MyAdapter", "headUrl:" + headUrl);
                    ImageLoad.loadImg(headUrl, img_topichead);
                }
            }
        }
        TextView mTime = (TextView) view.findViewById(R.id.time);
        String time = ChangeTime.TimeStamp2Date("" + topics.get(position).getClock(), "yyyy-MM-dd HH:mm:ss");
        mTime.setText(time);

        TextView mTitle = (TextView) view.findViewById(R.id.title);
        mTitle.setText(topics.get(position).getTitle());

        return view;
    }

}
