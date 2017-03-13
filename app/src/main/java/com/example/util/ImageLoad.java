package com.example.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.smsdemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/11/17.
 */

public class ImageLoad {
    public static void loadImg(String imageUrl, ImageView mImageView) {
//        String imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg";
            //显示图片的配置
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.plugin_camera_no_pictures)
                    .showImageOnFail(R.drawable.plugin_camera_no_pictures)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
                    //return mImageView;
            return;

    }

}
