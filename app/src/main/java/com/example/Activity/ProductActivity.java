package com.example.Activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.Adapter.ContentAdapter;
import com.example.smsdemo.R;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends Activity implements OnClickListener,ViewPager.OnPageChangeListener{
    /**
     * 公司产品界面
     * @param savedInstanceState
     */
    private Context mContext;

    //四个linearlayout

    private LinearLayout ll_bmsc_cell; //骨髓干细胞
    private LinearLayout ll_msc_cell;  //间充质干细胞
    private LinearLayout ll_adsc_cell; //脂肪干细胞
    private LinearLayout ll_nkcik_cell;//NK,CIK细胞

    //四个imageview

    private ImageView iv_bmsc;
    private ImageView iv_msc;
    private ImageView iv_adsc;
    private ImageView iv_nkcik;

    //四个菜单标题

    private TextView tv_bmsc;
    private TextView tv_msc;
    private TextView tv_adsc;
    private TextView tv_nkcik;


    private TextView tv_back;   //返回按钮

    //底部的内容
    private ViewPager viewPager;

    //ViewPager适配器ContentAdapter
    private ContentAdapter adapter;

    private List<View> views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        mContext=ProductActivity.this;
        initView();

        initEvent();

    }
    //初始化view
    private void initView() {

        //四个菜单栏
        ll_bmsc_cell= (LinearLayout) findViewById(R.id.bmsc_cell);
        ll_msc_cell= (LinearLayout) findViewById(R.id.msc_cell);
        ll_adsc_cell= (LinearLayout) findViewById(R.id.adsc_cell);
        ll_nkcik_cell= (LinearLayout) findViewById(R.id.nkcik_cell);


        //四个Imageview
        iv_bmsc= (ImageView) findViewById(R.id.iv_bmsc);
        iv_msc= (ImageView) findViewById(R.id.iv_msc);
        iv_adsc= (ImageView) findViewById(R.id.iv_adsc);
        iv_nkcik= (ImageView) findViewById(R.id.iv_nkcik);


        //四个textview
        tv_bmsc= (TextView) findViewById(R.id.tv_bmsc);
        tv_msc= (TextView) findViewById(R.id.tv_msc);
        tv_adsc= (TextView) findViewById(R.id.tv_adsc);
        tv_nkcik= (TextView) findViewById(R.id.tv_nkcik);

        tv_back= (TextView) findViewById(R.id.text_back);


        // 底部内容区域ViewPager
        this.viewPager= (ViewPager) findViewById(R.id.vp_content);


        //设置适配器
        View page_01 = View.inflate(mContext, R.layout.page_01, null);
        View page_02 = View.inflate(mContext, R.layout.page_02, null);
        View page_03 = View.inflate(mContext, R.layout.page_03, null);
        View page_04 = View.inflate(mContext, R.layout.page_04, null);

        views=new ArrayList<View>();
        views.add(page_01);
        views.add(page_02);
        views.add(page_03);
        views.add(page_04);

        this.adapter=new ContentAdapter(views);
        viewPager.setAdapter(adapter);
    }
    //初始化事件
    private void initEvent() {
        //设置按钮监听
        ll_bmsc_cell.setOnClickListener(this);
        ll_msc_cell.setOnClickListener(this);
        ll_adsc_cell.setOnClickListener(this);
        ll_nkcik_cell.setOnClickListener(this);

        //设置返回键的监听
        tv_back.setOnClickListener(this);

        //设置滑动监听
       // viewPager.addOnPageChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }
    @Override
    public void onClick(View v) {
        // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为蓝色，然后根据点击着
        restartBotton();
        // ImageView和TetxView置为绿色，页面随之跳转
        switch (v.getId()) {
            case R.id.bmsc_cell:
                iv_bmsc.setImageResource(R.drawable.bmsc_cell_pressed);
                tv_bmsc.setTextColor(0xff4f9cda);
                viewPager.setCurrentItem(0);
                break;
            case R.id.msc_cell:
                iv_msc.setImageResource(R.drawable.msc_cell_pressed);
                tv_msc.setTextColor(0xff4f9cda);
                viewPager.setCurrentItem(1);
                break;
            case R.id.adsc_cell:
                iv_adsc.setImageResource(R.drawable.adsc_cell_pressed);
                tv_adsc.setTextColor(0xff4f9cda);
                viewPager.setCurrentItem(2);
                break;
            case R.id.nkcik_cell:
                iv_nkcik.setImageResource(R.drawable.nkcik_cell_pressed);
                tv_nkcik.setTextColor(0xff4f9cda);
                viewPager.setCurrentItem(3);
                break;
            case R.id.text_back:
                finish();
                break;
        }
    }
    private void restartBotton() {

        // ImageView置为灰色
        iv_bmsc.setImageResource(R.drawable.bmsc_cell_normal);
        iv_msc.setImageResource(R.drawable.msc_cell_normal);
        iv_adsc.setImageResource(R.drawable.adsc_cell_normal);
        iv_nkcik.setImageResource(R.drawable.nkcik_cell_normal);

        // TextView置为灰色
        tv_bmsc.setTextColor(0xff373434);
        tv_msc.setTextColor(0xff373434);
        tv_adsc.setTextColor(0xff373434);
        tv_nkcik.setTextColor(0xff373434);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {
        restartBotton();
        //当前view被选择的时候,改变底部菜单图片，文字颜色
        switch (position){
            case 0:
                iv_bmsc.setImageResource(R.drawable.bmsc_cell_pressed);
                tv_bmsc.setTextColor(0xff4f9cda);
                break;
            case 1:
                iv_msc.setImageResource(R.drawable.msc_cell_pressed);
                tv_msc.setTextColor(0xff4f9cda);
                break;
            case 2:
                iv_adsc.setImageResource(R.drawable.adsc_cell_pressed);
                tv_adsc.setTextColor(0xff4f9cda);
                break;
            case 3:
                iv_nkcik.setImageResource(R.drawable.nkcik_cell_pressed);
                tv_nkcik.setTextColor(0xff4f9cda);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
