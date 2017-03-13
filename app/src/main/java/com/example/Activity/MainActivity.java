package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.IndestryAdapter;
import com.example.Adapter.MyGridAdapter;
import com.example.Adapter.MyGridAdapter2;
import com.example.smsdemo.R;
import com.example.util.ChangeTime;
import com.example.util.CustomDialog;
import com.example.util.HttpUtil;
import com.example.util.ImageLoad;
import com.example.util.MarqueeText;
import com.example.util.MyGridView;
import com.example.util.RequestUtil;
import com.example.util.getcellutil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private MyGridView gridView;
    private final String PREFERENCES_NAME = "userinfo";
    private long exitTime = 0;
    private Context mContext;
    private Button destroy;
    private ViewPager viewPager; // android-support-v4中的滑动组件
    private List<ImageView> imageViews = new ArrayList<ImageView>(); // 滑动的图片集合
    private List<String> titles = new ArrayList<String>(); // 图片标题
    //	private int[] imageResId; // 图片ID
    private List<String> imgList = new ArrayList<String>();    //图片ID
    private List<View> dots; // 图片标题正文的那些点
    private String headUrl = "";
    private String mqueetext;   //滚动条的内容
    private ListView mylistview;    //行业动态的listview
    private String username = null;

    private IndestryAdapter idadapter; //行业动态的adapter

    List<String> indeslist = new ArrayList<String>();

    private TextView tv_title;
    private int currentItem = 0; // 当前图片的索引号
    private ScheduledExecutorService scheduledExecutorService;
    private ImageView mytextviewuser;
    //行业新闻
    private RelativeLayout myindusy, myindusy1, myindusy2;
    private MarqueeText marqueetext; //滚动条的信息
    private JSONArray nativeJson;
    // 切换当前显示的图片
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
        }
    };
    private ChangeAsyncTask changeAsyncTask;    //滑动页面的asynctask
    private MarqueeTextAsynctask marqueeTextAsynctask;  //滚动条的asynctask
    private HealthAsynctask healthAsynctask;    //健康小知识的asynctask
    private ExpretListAsyncTask expretListAsyncTask;//获取专家列表的asynctask
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFinishOnTouchOutside(false);
        //拿到从登录界面传递过来的isChecked的信息
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        final boolean a = preferences.getBoolean("isChecked", false);


        SharedPreferences pref = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        username = pref.getString("Phone", "");
        Log.e("feed_username", username);


        mContext = MainActivity.this;
        initView();

        //注销，删除掉用户存储的信息然后关闭当前页面
        destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("确定退出？");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        cleanSharedPreference(mContext);
                        startActivity(intent);
                        finish();
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
        //判断传递过来的参数a
        if (a == true) {
            gridView.setAdapter(new MyGridAdapter(this));
        } else {
            gridView.setAdapter(new MyGridAdapter2(this));
        }
        //gridview的点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!RequestUtil.isNetworkAvailable(mContext)) {
                   // Toast.makeText(mContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
                } else {
                    if (a == true) {    //如果是医生登录则用这个
                        switch (i) {
                            case 0://我的细胞
                                Intent intent = new Intent(mContext, StemCellsActivity.class);
                                startActivity(intent);
                                break;
                            case 1://我的社群
                                Intent intent1 = new Intent(mContext, CommunityActivity.class);
                                startActivity(intent1);
                                break;
                            case 2://专家团队
                                Intent intent2 = new Intent(mContext, TeamExperts.class);
                                startActivity(intent2);
                                break;
                            case 3://专家咨询
                                login();
                                break;
                            case 4://临床案例
                                Intent intent4= new Intent(mContext, CliniCalcase.class);
                                startActivity(intent4);
                                break;
                            case 5://公开课
                                Intent intent5 = new Intent(mContext, OpenClass.class);
                                startActivity(intent5);
                                break;
                            case 6://产品介绍
                                Intent intent6 = new Intent(mContext, ProductActivity.class);
                                startActivity(intent6);
                                break;
                            case 7://行业动态
                                Intent intent7 = new Intent(mContext, IndustrytrendsActivity.class);
                                startActivity(intent7);
                                break;
                        }
                    } else {    //如果是普通用户登录则不显示公司产品介绍
                        switch (i) {
                            case 0://我的细胞
                                Intent intent = new Intent(mContext, StemCellsActivity.class);
                                startActivity(intent);
                                break;
                            case 1://我的社群
                                Intent intent1 = new Intent(mContext, CommunityActivity.class);
                                startActivity(intent1);
                                break;
                            case 2://专家团队
                                Intent intent2=new Intent(mContext,TeamExperts.class);
                                startActivity(intent2);
                                break;
                            case 3://专家咨询
                                 login();
                                break;
                            case 4://临床案例
                            Intent intent4 = new Intent(mContext, CliniCalcase.class);
                            startActivity(intent4);
                                break;
                            case 5://公开课
                                Intent intent5 = new Intent(mContext,OpenClass.class);
                                startActivity(intent5);
                                break;
                            case 6://行业动态
                                Intent intent6 = new Intent(mContext,IndustrytrendsActivity.class);
                                startActivity(intent6);
                                break;
                        }
                    }
                }
            }
        });
        mytextviewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RequestUtil.isNetworkAvailable(mContext)) {
                    Toast.makeText(mContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mContext, UploadActivity.class);
                    startActivity(intent);
                }
            }
        });
        /**
         *进行时间戳判断，如果用户7天未使用APP就重新登录604800
         */
        SharedPreferences preferences2 = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String time = preferences2.getString("time", "");  //获取时间缓存
        Log.e("huancuntime", time);
        if (!time.equals("")) {
            if (Long.parseLong(ChangeTime.timeStamp()) - Long.parseLong(time) > 604800) {
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("登录信息已过时请重新登录");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        cleanSharedPreference(mContext);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void initView() {
        marqueetext = (MarqueeText) findViewById(R.id.MarqueeText);

        if(RequestUtil.isNetworkAvailable(mContext)) {

            changeAsyncTask = new ChangeAsyncTask();
            changeAsyncTask.execute();  //开启滑动线程

            marqueeTextAsynctask = new MarqueeTextAsynctask();
            marqueeTextAsynctask.execute(); //开启滚动条线程

            healthAsynctask = new HealthAsynctask();
            healthAsynctask.execute();      //开启健康小知识的线程
        }else {
//            Toast.makeText(mContext,"网络异常，请检查您的网络连接",Toast.LENGTH_SHORT).show();
        }

        gridView = (MyGridView) findViewById(R.id.gridview);
        destroy = (Button) findViewById(R.id.destroy);    //注销
        //滚动的图片
//		titles[0] = "我们的技术";
//		titles[1] = "我们的主旨";
//		titles[2] = "我们的产品";
        //拿到三个新闻Relativelayout
        myindusy = (RelativeLayout) findViewById(R.id.rel_industry);
        mytextviewuser = (ImageView) findViewById(R.id.personage);
        mylistview = (ListView) findViewById(R.id.indestrylist);
        // 设置listview的点击事件
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取URL
                try {
                    Intent intent = new Intent(mContext, Rel_industry.class);
                    String url = nativeJson.getJSONObject(position).opt("url").toString();
                    Log.e("url", url);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒钟切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2, TimeUnit.SECONDS);
        //拿到从登录界面传递过来的isChecked的信息
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        headUrl = preferences.getString("headUrl", headUrl); //拿到缓存中的图片地址
        Log.e("headUrl", headUrl);
        //用户头像
        if (headUrl != null) {
            ImageLoad.loadImg(headUrl, mytextviewuser);
        } else {
            mytextviewuser.setImageResource(R.drawable.user_top);
        }
        /**
         * 获取专家列表的异步处理
         */
        expretListAsyncTask=new ExpretListAsyncTask();
        expretListAsyncTask.execute();
        super.onStart();
    }

    @Override
    protected void onStop() {
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
        super.onStop();
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理
     *
     * @param context
     */
    public void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 换行切换任务
     *
     * @author Administrator
     */
    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (viewPager) {
                System.out.println("currentItem: " + currentItem);
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
            }
        }
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private int oldPosition = 0;

        public void onPageSelected(int position) {
            currentItem = position;
            tv_title.setText(titles.get(position));
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 填充ViewPager页面的适配器
     *
     * @author Administrator
     */
    private class MyAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
    //重写返回按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 将Asynctask与Activity的事件进行绑定
     */
    @Override
    protected void onPause() {
        if (changeAsyncTask!=null&&changeAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            changeAsyncTask.cancel(true);
        }
        if (marqueeTextAsynctask!=null&&marqueeTextAsynctask.getStatus()==AsyncTask.Status.RUNNING){
            marqueeTextAsynctask.cancel(true);
        }
        if (healthAsynctask!=null&&healthAsynctask.getStatus()==AsyncTask.Status.RUNNING){
            healthAsynctask.cancel(true);
        }
        if (expretListAsyncTask!=null&&expretListAsyncTask.getStatus()==AsyncTask.Status.RUNNING){
            expretListAsyncTask.cancel(true);
        }
        super.onPause();
    }
    /**
     * 滑动页面的异步线程
     */
    class  ChangeAsyncTask extends AsyncTask<Map,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.pgreplacement);
                jsonObject =new JSONObject(result);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        /**
         * 更新UI
         * @param jsonObject
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            if(jsonObject==null){
                return;
            }
            try {
                imgList.clear();
                titles.clear();
                int code=jsonObject.optInt("code");
                String message=jsonObject.optString("msg");
                Log.e("code",code+"");
                if (code==100){
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    JSONArray jsonArray=jsonObject1.getJSONArray("LoopImages");
                    Log.e("msg", jsonObject+"");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
                        imgList.add(jsonObjectSon.opt("picture").toString());
                        titles.add(jsonObjectSon.opt("title").toString());
                    }
                    // 初始化图片资源
                    for (String i : imgList) {
                        ImageView imageView = new ImageView(MainActivity.this);
                        ImageLoad.loadImg(i, imageView);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageViews.add(imageView);
                    }
                    dots = new ArrayList<View>();
                    dots.add(findViewById(R.id.v_dot0));
                    dots.add(findViewById(R.id.v_dot1));
                    dots.add(findViewById(R.id.v_dot2));

                    tv_title = (TextView) findViewById(R.id.tv_title);
                    tv_title.setText(titles.get(0));

                    viewPager = (ViewPager) findViewById(R.id.vp);
                    viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
                    // 设置一个监听器，当ViewPager中的页面改变时调用
                    viewPager.setOnPageChangeListener(new MyPageChangeListener());
                }
                if (!message.equals("")){
                    Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                }
            }catch(NullPointerException e){
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("服务器维护中");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid()); //关掉应用程序
                    }
                });
                builder.create().show();
                e.printStackTrace();
            } catch (JSONException e) {
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("万海生命银行有最新版本请更新");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.example.smsdemo"));
                        startActivity(intent);
                    }
                });
                builder.create().show();
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 滚动条内容的异步线程
     */
    class  MarqueeTextAsynctask extends AsyncTask<Map,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.marqueetext);
                jsonObject=new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
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
                int code=jsonObject.optInt("code");
                String message=jsonObject.optString("msg");
                if (code==100){
                    JSONObject  jsonObject1=jsonObject.getJSONObject("data");
                    JSONArray jsonArray = jsonObject1.getJSONArray("ScrollBar");
                    for (int i=0;i<jsonArray.length();i++){
                        mqueetext=jsonArray.getJSONObject(0).opt("guide").toString();
                        Log.e("mqueetext",mqueetext);
                        marqueetext.setText(mqueetext);//将获取的滚动条内容设置到marqueetext中
                    }
                }
                if (!message.equals("")){
                    Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                }
            }catch(NullPointerException e){
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("服务器维护中");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid()); //关掉应用程序
                    }
                });
                builder.create().show();
            }
            catch (JSONException e) {
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("万海生命银行有最新版本请更新");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.example.smsdemo"));
                        startActivity(intent);
                    }
                });
                builder.create().show();
                e.printStackTrace();
            }
            super.onPostExecute(jsonObject);
        }
    }

    /**
     * indestryAsynctask 健康小知识的UI线程
     */
    class HealthAsynctask extends AsyncTask<Map,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.healthKnowledgeApp);
                jsonObject=new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            try {
                int code=jsonObject.optInt("code");
                if (code==100){
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    nativeJson=jsonObject1.getJSONArray("HealthTips");
                    idadapter = new IndestryAdapter(mContext, nativeJson);
                    mylistview.setAdapter(idadapter);
                }
            }
            catch (NullPointerException e){
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("服务器维护中");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid()); //关掉应用程序
                    }
                });
                builder.create().show();
            }
            catch (JSONException e) {
                //弹出更新提示dialog
                CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
                builder.setTitle("提示");
                builder.setMessage("万海生命银行有最新版本请更新");
                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.example.smsdemo"));
                        startActivity(intent);
                    }
                });
                builder.create().show();
                e.printStackTrace();
            }

            super.onPostExecute(jsonObject);
        }
    }

    /**
     * 获取专家列表的异步处理,拿到电话号码存缓存
     */
    class ExpretListAsyncTask extends AsyncTask<Map,String,JSONObject>{
        @Override
        protected JSONObject doInBackground(Map... params) {
            JSONObject jsonObject=null;
            try {
                String result=getcellutil.getHttpJsonByhttpclient(HttpUtil.getExpertsList);
                jsonObject=new JSONObject(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (isCancelled()){
                return;
            }
            if(jsonObject==null){
                return;
            }
            Integer code=jsonObject.optInt("code");
            String message=jsonObject.optString("msg");
            if (code==100){
                try {
                    JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    JSONArray jsonArray=jsonObject1.getJSONArray("experts");
                    Set<String> stringSet = new HashSet<>();
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObjectson=jsonArray.getJSONObject(i);
                        //将电话号码存缓存
                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        stringSet.add(jsonObjectson.getString("phone"));
                        editor.putStringSet("experts_list", stringSet);
                        editor.apply();
                    }
                    if (!message.equals("")){
                        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }


    /**
     * 用户登录
     */
    public void login(){
//        Log.e("isloginBefore", EMClient.getInstance().isLoggedInBefore()+"");
//      if ( EMClient.getInstance().isLoggedInBefore()){
//          Intent intent3 = new Intent(mContext, FeedBack.class);
//          startActivity(intent3);
//      }
        Log.e("onProgressUpdate", "asdasdasd");
        EMClient.getInstance().login(username, "123456", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                Log.d("main", "登录聊天服务器成功！");
                Intent intent3 = new Intent(mContext, FeedBack.class);
                startActivity(intent3);
            }
            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
                Log.e("long", "登录失败" + code + "," + message);
            }
        });


    }


}
