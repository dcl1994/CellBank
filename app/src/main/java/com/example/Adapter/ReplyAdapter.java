package com.example.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.model.IReplys;
import com.example.smsdemo.R;

import java.util.List;

/**
 * Created by szjdj on 2016-10-20.
 */
public class ReplyAdapter extends BaseAdapter {
    private Context context;
    private List<IReplys> replys;

    private TextView replytext; //回复人

    private String replyAccount;    //回复人账号

    private String commentAccount;    //被回复人账号

    private String ss;

    public ReplyAdapter(Context context, List<IReplys> replys, String commentAccount) {
        this.replys = replys;
        this.context = context;
        this.commentAccount = commentAccount;
    }


    @Override
    public int getCount() {
        return replys.size();
    }

    @Override
    public Object getItem(int position) {
        return replys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup viewGroup) {
        View view;
        ViewHolder viewHolder;
        if (convertview == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.reply_item, null);
            view.setTag(viewHolder);    //将ViewHolder存储在view中
        } else {
            view = convertview;
            viewHolder = (ViewHolder) view.getTag(); //重新获取ViewHolder
        }
        viewHolder.replytext = (TextView) view.findViewById(R.id.replyContent);
        viewHolder.replytext.setText(replys.get(position).getUsername() + ":" + replys.get(position).getContent());
        return view;
    }

    class ViewHolder {
        TextView replytext;
    }
}
