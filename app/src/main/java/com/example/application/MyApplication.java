package com.example.application;


import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Iterator;
import java.util.List;

public class MyApplication extends Application{
    public static ImageLoader imageLoader=ImageLoader.getInstance();
    @Override
    public void onCreate() {
        super.onCreate();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs() //打印log信息
                .build();
        initEasemob();

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//        .threadPriority(Thread.NORM_PRIORITY - 2)
//        .denyCacheImageMultipleSizesInMemory()
//        .discCacheFileNameGenerator(new Md5FileNameGenerator())
//        .tasksProcessingOrder(QueueProcessingType.LIFO)
//        .enableLogging() // Not necessary in common
//        .build();
//       ImageLoader.getInstance().init(config);

//		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
//		        .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
//		        .diskCacheExtraOptions(480, 800, null)
//		        .taskExecutor(...)
//		        .taskExecutorForCachedImages(...)
//		        .threadPoolSize(3) // default
//		        .threadPriority(Thread.NORM_PRIORITY - 2) // default
//		        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
//		        .denyCacheImageMultipleSizesInMemory()
//		        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//		        .memoryCacheSize(2 * 1024 * 1024)
//		        .memoryCacheSizePercentage(13) // default
//		        .diskCache(new UnlimitedDiskCache(cacheDir)) // default
//		        .diskCacheSize(50 * 1024 * 1024)
//		        .diskCacheFileCount(100)
//		        .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
//		        .imageDownloader(new BaseImageDownloader(getApplicationContext())) // default
//		        .imageDecoder(new BaseImageDecoder()) // default
//		        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//		        .writeDebugLogs()
//		        .build();
    }

    private void initEasemob() {
        EMOptions options = new EMOptions();

        //SDK 中自动登录属性默认是 true 打开的，如果不需要自动登录，在初始化 SDK 初始化的时候，调用options.setAutoLogin(false);设置为 false 关闭。
        options.setAutoLogin(false);
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
//        options.setNumberOfMessagesLoaded(1);

// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e("long", "enter the service process!");
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);

    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.d("Process", "Error>> :" + e.toString());
            }
        }
        return processName;
    }




}
