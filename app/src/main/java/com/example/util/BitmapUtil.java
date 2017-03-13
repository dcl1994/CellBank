package com.example.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by szjdj on 2017-01-09.
 * bitmap图片处理工具类
 */
public  class BitmapUtil {
    /**
     * 图片按质量压缩
     */
    public static Bitmap compressImage(Bitmap image){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,70,baos); //质量压缩方法，这里100表示不压缩，
        // 把压缩后的数据存放在baos中
        int options=100;
        while(baos.toByteArray().length/1024>300){

            //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options-=10;
        }
        ByteArrayInputStream isBm=new ByteArrayInputStream(baos.toByteArray()); //把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap= BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
    /**
     * 图片按比例大小压缩
     */
    public static  Bitmap com(Bitmap image){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        if (baos.toByteArray().length/1024>1024){
            //判断图片大于1M，进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG,50,baos); //这里压缩50%，
            // 把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm=new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts=new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds设回为true了
        newOpts.inJustDecodeBounds=true;
        Bitmap bitmap=BitmapFactory.decodeStream(isBm,null,newOpts);
        newOpts.inJustDecodeBounds=false;
        int w=newOpts.outWidth;
        int h=newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh=800f;
        float ww=480f;

        int be=1; //be=1表示不缩放
        if (w>h&&w>ww){ //如果宽度大的话根据宽度固定大小比例
            be=(int)(newOpts.outWidth/ww);
        }else if (w<h && h>hh){//如果高度大的话根据高度固定大小比例
            be= (int) (newOpts.outHeight/hh);
        }
        if (be<=0) be=1;
        newOpts.inSampleSize=be; //设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm=new ByteArrayInputStream(baos.toByteArray());
        bitmap=BitmapFactory.decodeStream(isBm,null,newOpts);
        return compressImage(bitmap);
    }


    /**
     * Bitmap转file的算法
     */
     public static boolean saveBitmap2file(Bitmap bmp,String filename){
        Bitmap.CompressFormat format= Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream( filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }
}
