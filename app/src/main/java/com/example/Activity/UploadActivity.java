package com.example.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smsdemo.R;
import com.example.util.HttpGetUtil;
import com.example.util.HttpUtil;
import com.example.util.UploadUtil;
import com.example.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息界面
 */
public class UploadActivity extends Activity implements OnClickListener {

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private ImageView myiv_personal_icon;
    private String imagePath;
    private String fileName;
    private Button mybtn_change;
    private Map<String, String> parms = new HashMap<String, String>();
    private File file;
    private TextView mytextback;
    private Context mcontext;
    private SharedPreferences preferences;//用户
    private final String PREFERENCES_NAME = "userinfo";
    private String userid;
    private RelativeLayout myrelay_user;
    private RelativeLayout myrelay_changepwd;
    private RelativeLayout myrelay_where;
    private RelativeLayout myrelay_email;
    private RelativeLayout myreplay_qrcode;
    private TextView tv_username;
    private TextView tv_eamil;
    private TextView tv_address;
    private String url = "";

    private PopupWindow mPopWindow; //弹出一个popupwinodw
    private LinearLayout ll_popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        init();
        findById();
        mcontext = UploadActivity.this;

    }
    private void findById() {
        mytextback.setOnClickListener(this);
        mybtn_change.setOnClickListener(this);
        myrelay_user.setOnClickListener(this);
        myrelay_changepwd.setOnClickListener(this);
        myrelay_where.setOnClickListener(this);
        myrelay_email.setOnClickListener(this);
        myreplay_qrcode.setOnClickListener(this);
        tv_username.setOnClickListener(this);
        tv_eamil.setOnClickListener(this);
        tv_address.setOnClickListener(this);
    }

    //初始化view
    private void init() {
        mybtn_change = (Button) findViewById(R.id.btn_change);
        myiv_personal_icon = (ImageView) findViewById(R.id.iv_personal_icon);
        mytextback = (TextView) findViewById(R.id.text_back);

        myrelay_user = (RelativeLayout) findViewById(R.id.relay_user);
        myrelay_changepwd = (RelativeLayout) findViewById(R.id.relay_changepwd);
        myrelay_where = (RelativeLayout) findViewById(R.id.relay_where);
        myrelay_email = (RelativeLayout) findViewById(R.id.relay_email);
        myreplay_qrcode = (RelativeLayout) findViewById(R.id.relay_qrcode);
        tv_username = (TextView) findViewById(R.id.username_edit);
        tv_address = (TextView) findViewById(R.id.username_address);
        tv_eamil = (TextView) findViewById(R.id.username_emailaddress);
    }


    /**
     * 显示popupwindow
     */
    private void showPopupWindow(){
        //设置contentView
        mPopWindow = new PopupWindow(UploadActivity.this);
        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.item_popupwindows, null);
        ll_popup = (LinearLayout) contentView.findViewById(R.id.ll_popup);
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setContentView(contentView);
        RelativeLayout parent = (RelativeLayout) contentView.findViewById(R.id.parent);
        //设置各个控件的点击响应
        Button bt1 = (Button)contentView.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button)contentView.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button)contentView.findViewById(R.id.item_popupwindows_cancel);

        //拍照
        bt1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                tempUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "image.jpg"));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
                mPopWindow.dismiss();
                ll_popup.clearAnimation();
            }
        });

        //从相册选取
        bt2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);

                mPopWindow.dismiss();
                ll_popup.clearAnimation();
            }
        });

        //取消
        bt3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
                ll_popup.clearAnimation();
            }
        });

        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mPopWindow.dismiss();
                ll_popup.clearAnimation();
            }
        });
        //显示PopupWindow
        View rootview = LayoutInflater.from(mcontext).inflate(R.layout.activity_upload, null);
        rootview.startAnimation(AnimationUtils.loadAnimation(mcontext, R.anim.activity_translate_in));
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示修改头像的对话框
     */
//    protected void showChoosePicDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("设置头像");
//        String[] items = {"选择本地照片", "拍照"};
//        builder.setNegativeButton("取消", null);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case CHOOSE_PICTURE: // 选择本地照片
//                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                        openAlbumIntent.setType("image/*");
//                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
//                        break;
//                    case TAKE_PICTURE: // 拍照
//                        Intent openCameraIntent = new Intent(
//                                MediaStore.ACTION_IMAGE_CAPTURE);
//                        tempUri = Uri.fromFile(new File(Environment
//                                .getExternalStorageDirectory(), "image.jpg"));
//                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
//                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
//                        break;
//                }
//            }
//        });
//        builder.create().show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param data
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            myiv_personal_icon.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    //点击事件
    @Override
    public void onClick(View v) {
        String username = tv_username.getText().toString();
        String address = tv_address.getText().toString();
        switch (v.getId()) {



            case R.id.btn_change:   //修改头像的点击事件
//                showChoosePicDialog();
                showPopupWindow();  //显示popupwindow
                break;
            case R.id.text_back:
                finish();
                break;
            //修改昵称
            case R.id.relay_user:
                Intent intent = new Intent(mcontext, user.class);
                startActivity(intent);
                break;
            //修改修改密码
            case R.id.relay_changepwd:
                Intent intent2 = new Intent(mcontext, ChangePassword.class);
                startActivity(intent2);
                break;
            //我的二维码
            case R.id.relay_qrcode:
                Intent intent1 = new Intent(mcontext, QrCode.class);
                intent1.putExtra("url", url);  //传入Bitmap图片
                intent1.putExtra("username", username); //传入用户名
                intent1.putExtra("address", address);    //传入地址
                startActivity(intent1);
                break;
            //填写地址
            case R.id.relay_where:
                Intent intent3 = new Intent(mcontext, Address.class);
                startActivity(intent3);
                break;
            //邮箱地址
            case R.id.relay_email:
                Intent intent4 = new Intent(mcontext, EmailAddress.class);
                startActivity(intent4);
                break;
        }
    }

    //上传图片
    class MyThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 2;
            msg.obj = UploadUtil.uploadFile(file, HttpUtil.ProcessUrl
                    + "/photoUp1", parms);
            handler.sendMessage(msg);
            super.run();
        }
    }

    /**
     * 查看详细信息
     */
    class Mythread1 extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = HttpGetUtil.httpPost(HttpUtil.detailUrl, userid);
//			Log.e("Mythread1", msg.obj.toString());
            handler.sendMessage(msg);
            super.run();
        }
    }

    @Override
    protected void onStart() {
        //获取到userid
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        userid = preferences.getString("userid", "");
        Log.d("userid", userid);
        try {
            Mythread1 thread = new Mythread1();
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://读取详细信息
                    Log.e("handler", msg.obj.toString());
                    Log.e("handler", msg.obj.toString().replace("null", "\"\""));
                    msg.obj = msg.obj.toString().replace("null", "\"\"");
                    Log.e("handler", msg.obj.toString());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("JsonArry");
                        if (jsonArray.length() > 0) {
                            Log.e("handleraaa", jsonArray.getJSONObject(0).toString());
                            String username = jsonArray.getJSONObject(0).opt("username").toString();
                            String address = jsonArray.getJSONObject(0).opt("address").toString();
                            String email = jsonArray.getJSONObject(0).opt("email").toString();
                            String headUrl = "";
                            if (jsonArray.getJSONObject(0).opt("headUrl") != null) {
                                headUrl = jsonArray.getJSONObject(0).opt("headUrl").toString();
                            }
                            url = headUrl;
                            tv_address.setText(address);
                            tv_eamil.setText(email);
                            tv_username.setText(username);
                            if (!headUrl.equals("")) {
                                //  new ImageLoader().showImageByAsyncTask(myiv_personal_icon, headUrl);
                                //ImageLoad.loadImg(headUrl, myiv_personal_icon);

                                /**
                                 * 使用glide加载图片
                                 */
                                Glide.with(mcontext).load(headUrl).into(myiv_personal_icon);


                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    Log.e("handler02", msg.obj.toString());
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = jsonObject1.getJSONArray("JsonArry");
                        Log.e("handler", jsonArray.getJSONObject(0).toString());
                        String headUrl = jsonArray.getJSONObject(0).opt("headUrl").toString();
                        if (headUrl != null && !headUrl.equals("")) {
                           // ImageLoad.loadImg(headUrl, myiv_personal_icon);

                            /**
                             * 使用glide加载网络图片
                             */
                               Glide.with(mcontext).load(headUrl).into(myiv_personal_icon);

                            //将headurl存到缓存中
                            SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("headUrl", headUrl);
                            editor.commit();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
        fileName = String.valueOf(System.currentTimeMillis());
        imagePath = Utils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("uploadPic", imagePath + "");
        Log.e("uploadPic", "" + fileName);
        if (imagePath != null) {
            // 拿着imagePath上传了
            // ...
            file = new File(imagePath);
            parms.put("filename1", file.getName());
            parms.put("fileNUM", "1");
            parms.put("userid", userid);
            Log.e("filename:", file.getName());
            MyThread myThread = new MyThread();
            myThread.start();
        }
    }

}