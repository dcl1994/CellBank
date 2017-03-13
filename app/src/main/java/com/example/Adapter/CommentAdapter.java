package com.example.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.IComments;
import com.example.model.IReplys;
import com.example.model.ITopics;
import com.example.smsdemo.R;
import com.example.util.ChangeTime;
import com.example.util.CustomDialog;
import com.example.util.HttpUtil;
import com.example.util.ImageLoad;
import com.example.util.MyGridView;
import com.example.util.RequestUtil;

import java.util.List;
import java.util.Map;

/**
 * Topic的adapter
 */
public class CommentAdapter extends BaseAdapter {

    private List<IComments> comments;
    private IReplys ireplys;
    private Context context;

    private ImageButton commentreplay, commentdelet;

    private ListView noscrolllistview;  //回复的listview

    private Adapter replayadapter;  //回复的adapter

    List<Map<String, String>> replyData;    //回复的list

    private String replyAccount;    //回复人账号

    private String commentAccount;    //被回复人账号

    private TextView replytext; //回复人
    private Handler ComHandler;
    private Map<String, String> resultmap;
    private String delPosition;  //删除的位置

    public CommentAdapter(Context context, List<IComments> comments, Handler ComHandler) {
        this.comments = comments;
        this.context = context;
        this.ComHandler = ComHandler;
    }

    @Override
    public int getCount() {
        return comments.size() + HttpUtil.Top2ComList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return HttpUtil.Top2ComList.get(0);
        } else {
            return comments.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 删除的handler
     */
    private Handler handlerdele = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("kkkkkkkkkkkkk", "handlerMessage");
            Thread thread = new Thread() {
                @Override
                public void run() {
                    resultmap = RequestUtil.getPDAServerData("delComment.action?commentId=" + comments.get(Integer.parseInt(delPosition)).getId());
                    super.run();
                }
            };
            thread.start();
            while (resultmap == null) {
            }
            if (resultmap.get("message").equals("success")) {
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                int p = Integer.parseInt(delPosition);
                super.handleMessage(msg);
                Message msg1 = new Message();
                msg1.what = 12;
                msg1.obj = Integer.parseInt(delPosition);
                ComHandler.sendMessage(msg1);
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }
            resultmap = null;
        }
    };
    /**
     * 回复的handler
     *
     * @param position
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        /**
         * 将第一个item设置成list_item_layout2
         * gridview名字：topic_gridview
         */
        if (position == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_layout2, null);
            ITopics topic = HttpUtil.Top2ComList.get(0);
            Log.e("MyAdapter", "话题评论回复页");
            ImageView img_topichead = (ImageView) view.findViewById(R.id.img_topichead);
            ImageLoad.loadImg(topic.getHeadUrl(), img_topichead);

            final MyGridView mygridview;
            mygridview = (MyGridView) view.findViewById(R.id.topic_gridview);
            mygridview.setAdapter(new MyGridAdapterPhoto(context, topic.getUrlList()));

            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(topic.getTitle());
            TextView mContent = (TextView) view.findViewById(R.id.content);
            mContent.setText(topic.getContent().replace("\\n", "\n"));
            TextView mUsername = (TextView) view.findViewById(R.id.username);
            mUsername.setText(topic.getUsername());
            TextView mTime = (TextView) view.findViewById(R.id.time);
            String time = ChangeTime.TimeStamp2Date("" + topic.getClock(), "yyyy-MM-dd HH:mm:ss");
            mTime.setText(time);
        } else {
            /**
             * 下面的listview使用comment_item_list
             * gridview名字：comment_gridview
             */
            Log.d("Comment", "话题评论回复页");
            view = LayoutInflater.from(context).inflate(R.layout.comment_item_list, null);
            TextView mContent = (TextView) view.findViewById(R.id.comment_content);
            mContent.setText(comments.get(position - 1).getContent());
            TextView mUsername = (TextView) view.findViewById(R.id.comment_name);
            mUsername.setText(comments.get(position - 1).getUsername());
            /**
             * 获取用户的头像,评论的头像和Gridview图片
             */
            ImageView img_comment_item = (ImageView) view.findViewById(R.id.img_comment_item);
            ImageLoad.loadImg(comments.get(position - 1).getHeadUrl(), img_comment_item);
            final MyGridView mygridview;
            mygridview = (MyGridView) view.findViewById(R.id.comment_gridview);

            if (comments.get(position - 1).getUrlList() != null) {
                List<String> urlList = comments.get(position - 1).getUrlList();
                mygridview.setAdapter(new MyGridAdapterPhoto(context,urlList));
            }

            TextView mTime = (TextView) view.findViewById(R.id.comment_time);
            String time = ChangeTime.TimeStamp2Date("" + comments.get(position - 1).getClock(), "yyyy-MM-dd HH:mm:ss");
            mTime.setText(time);
            commentdelet = (ImageButton) view.findViewById(R.id.btn_comment_delet);
            LinearLayout layout_del = (LinearLayout) view.findViewById(R.id.layout_del);
            String commentid = "" + comments.get(position - 1).getUserid();

            /**
             * 如果是本人的就把删除显示
             */
            SharedPreferences spf = context.getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            String userid = spf.getString("userid","");
            if (userid.equals(commentid)) {
                layout_del.setVisibility(View.VISIBLE);
            }
            commentdelet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除listview中的items
                    CustomDialog.Builder builder = new CustomDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("确定删除此话题?");

                    //添加AlertDialog.Builder对象的setPositiveButton()方法
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delPosition = "" + (position - 1);
                            dialogInterface.dismiss();
                            Message msg = new Message();
                            handlerdele.sendMessage(msg);
                        }
                    });
                    //添加AlertDialog.Builder对象的setNegativeButton()方法
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
            /**
             * 回复的点击事件
             */
            commentreplay = (ImageButton) view.findViewById(R.id.btn_comment_replay);
            commentreplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Message msg = new Message();
                    Message msg = ComHandler.obtainMessage();
                    msg.what = 13;
                    msg.obj = position - 1;
                    ComHandler.sendMessage(msg);
                }
            });
            /**
             * 找到NoScrollListearlistview回复的listview对象
             */
            replytext = (TextView) view.findViewById(R.id.replyContent);
            noscrolllistview = (ListView) view.findViewById(R.id.no_scroll_list_reply);
            List<IReplys> ireplys = comments.get(position - 1).getReplys();
            if (ireplys.isEmpty() || ireplys == null || ireplys.size() == 0) {
            } else {
                replayadapter = new ReplyAdapter(context, ireplys, commentAccount);
                noscrolllistview.setAdapter((ListAdapter) replayadapter);
            }
        }
        return view;
    }

}
