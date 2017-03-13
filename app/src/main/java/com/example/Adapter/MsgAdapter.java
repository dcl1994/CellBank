package com.example.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smsdemo.R;
import com.example.util.Msg;

import java.util.List;
import java.util.Set;


/**
 * Created by szjdj on 2017-02-20.
 */
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;
    private Set<String> stringSet;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;

        LinearLayout rightLayout;

        TextView leftMsg;

        TextView rightMsg;

        TextView leftusername,rightusername;

        ImageView ExpertImg;


        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.ll_msg_left);
            rightLayout = (LinearLayout) view.findViewById(R.id.ll_msg_right);

            ExpertImg= (ImageView) view.findViewById(R.id.Imageleft);


            leftMsg = (TextView) view.findViewById(R.id.tv_msg_left);
            rightMsg = (TextView) view.findViewById(R.id.tv_msg_right);
            leftusername= (TextView) view.findViewById(R.id.left_username);
            rightusername= (TextView) view.findViewById(R.id.right_username);
        }
    }

    public MsgAdapter(List<Msg> msgList,Set<String> set){
        mMsgList = msgList;
        stringSet=set;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        /**
         * 如果是专家则显示专家的页面布局，否则显示普通用户布局
         */

        if (msg.getType() == Msg.TYPE_RECEIVED) {  //如果是接收到的消息，显示左边布局，隐藏右边布局
            if (stringSet.contains(msg.getUsername())) {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.ExpertImg.setImageResource(R.drawable.expert_group); //将头像设置为专家
                holder.leftMsg.setText(msg.getContent());
                holder.leftusername.setText("专家");
            }else {
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftMsg.setText(msg.getContent());
                holder.ExpertImg.setImageResource(R.drawable.user_group);
                holder.leftusername.setText("用户");
            }
        }
        if(msg.getType() == Msg.TYPE_SENT){ //如果是发出的消息，显示右边布局，隐藏左边布局
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightusername.setText("我");
        }
    }
    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}

