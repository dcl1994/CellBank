package com.example.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smsdemo.R;
import com.example.util.Bimp;
import com.example.util.FileUtils;
import com.example.util.HttpUtil;
import com.example.util.ImageItem;
import com.example.util.MyGridView;
import com.example.util.PublicWay;
import com.example.util.Res;
import com.example.util.UploadUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发表话题界面
 */
public class PublishActivity extends Activity implements OnClickListener {
    private final String PREFERENCES_NAME = "userinfo";
    private TextView mtextView;
    private EditText edit_pub, title_pub;
    private Button Mysubmit;
    private String title, content = "", userid;
    private Context mContext;
    private PullToRefreshLayout ptrl;
    private MyGridView noScrollgridview;
     private GridAdapter adapter;
    private View parentView;
    private PopupWindow pop;
    private LinearLayout ll_popup;
    public static Bitmap bimap ;
    private PublishAsynctask publishAsynctask;  //发表的异步线程
    public Map<String,File> files= new HashMap<String, File>();
    private Map<String,String> params = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Res.init(this);
//        HttpUtil.isTopic = true;//判断是否进入话题界面，用于图片选择器的跳转判断
        bimap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_publish, null);
        setContentView(parentView);
        init();
        mContext = PublishActivity.this;
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", userid);
        Log.d("userid", userid);
        title_pub.setFilters(new InputFilter[]{filter});
        //初始化map 存入title,userid和内容
        params=new HashMap<String, String>();
        params.put("title",title);
        params.put("userid",userid);
        params.put("content",content);

    }
    //标题栏不能输入空格和换行符
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };
    private void init() {

        pop = new PopupWindow(PublishActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);

        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });


        noScrollgridview = (MyGridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    Log.i("ddddddd", "----------");
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view)); //刷新
        mtextView = (TextView) findViewById(R.id.topic_back);    //返回
        title_pub = (EditText) findViewById(R.id.topic_title);   //话题标题
        edit_pub = (EditText) findViewById(R.id.topic_content);  //话题内容
        Mysubmit = (Button) findViewById(R.id.submit);           //提交
        mtextView.setOnClickListener(this);
        Mysubmit.setOnClickListener(this);

    }
    @SuppressLint("HandlerLeak")
    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;
        public boolean isShape() {
            return shape;
        }
        public void setShape(boolean shape) {
            this.shape = shape;
        }
        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }
        public void update() {
            loading();
        }
        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }
        public Object getItem(int arg0) {
            return null;
        }
        public long getItemId(int arg0) {
            return 0;
        }
        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }
        public int getSelectedPosition() {
            return selectedPosition;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_photo,parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.img_photo);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }
            return convertView;
        }
        public class ViewHolder {
            public ImageView image;
        }
        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }
    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }
    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;
    private void photo() {
        HttpUtil.isopenCame =true;
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    /**
     * 打开拍照
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }
    /**
     * 重写onKeyDown,进行判断
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i<PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            Bimp.tempSelectBitmap.clear();  //返回清除图片
            finish();
        }
        return true;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topic_back://返回
                for(int i=0;i<PublicWay.activityList.size();i++){
                    if (null != PublicWay.activityList.get(i)) {
                        PublicWay.activityList.get(i).finish();
                    }
                }
                Bimp.tempSelectBitmap.clear();  //返回清除图片
                finish();
                break;
            /**
             * 提交的点击事件
             */
            case R.id.submit:
                title = title_pub.getText().toString();
                content = edit_pub.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(mContext, "标题不能为空!", Toast.LENGTH_SHORT).show();
                } else if (content.equals("")) {
                    Toast.makeText(mContext, "内容不能为空!", Toast.LENGTH_SHORT).show();
                } else {
                    Integer count = 0;
                    for(File file: Bimp.tempFile) {
                        count++;
                        files.put("file"+count, file);
                        params.put("filename"+count,file.getName());
                    }
                    params.put("fileNUM",count+"");
                    params.put("title",title);
                    params.put("content",content);
                    params.put("type","");
                    params.put("userid",userid);

                    /**
                     * 开启一个线程
                     */
                    publishAsynctask=new PublishAsynctask();
                    publishAsynctask.execute(params);
                }
                break;
        }
    }

    /**
     * 将AsyncTasK与activity的事件进行绑定
     */
    @Override
    protected void onPause() {
        if (publishAsynctask!=null&&publishAsynctask.getStatus()==AsyncTask.Status.RUNNING){
            publishAsynctask.cancel(true);
        }
        super.onPause();
    }

    /**
     * 发表的异步处理
     */
    class PublishAsynctask extends AsyncTask<Map<String ,String>,Void,JSONObject>{
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
           try {
                String result= UploadUtil.post(HttpUtil.addTopicUrl,params[0],files);
                jsonObject=new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        /**
         * 更新UI线程
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                Log.e("handler",jsonArray.getJSONObject(0)+"");
                if (jsonArray.getJSONObject(0).opt("description").toString().indexOf("发布成功")!=-1) {
                    title_pub.setText("");
                    edit_pub.setText("");
                    Toast.makeText(mContext, "发表成功", Toast.LENGTH_SHORT).show();
                    HttpUtil.isPublish = true;
                    Bimp.tempSelectBitmap.clear();  //发表成功清除图片
                    Bimp.tempFile.clear();  //发表成功清除临时文件
                    finish();
                }
                else{
                    Toast.makeText(PublishActivity.this,"发表失败",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }
}
