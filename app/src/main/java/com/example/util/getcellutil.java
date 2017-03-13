package com.example.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by szjdj on 2016-10-22.
 */
public class getcellutil {
    /**
     * 使用httpclient来接收后台数据
     * @param fromurl
     * @return
     * @throws IOException
     */
    public static String getHttpJsonByhttpclient(String fromurl) throws IOException {
        Log.v("zms", "使用httget");
        HttpGet geturl=new HttpGet(fromurl);
        Log.e("CheckUrl","URL正确");
        DefaultHttpClient httpclient=new DefaultHttpClient();
        HttpResponse response=httpclient.execute(geturl);
        Log.v("zms","响应码"+response.getStatusLine().getStatusCode());

        if (response.getStatusLine().getStatusCode()==200){
            String returnStr= EntityUtils.toString(response.getEntity(), "utf-8");
            Log.v("zms","返回值"+returnStr);
            return returnStr;

        }else{
            Log.v("zms","访问网络返回数据失败，错误码:"+response.getStatusLine().getStatusCode());



        }
        return null;
    }

//    /**
//     * 使用httpURLconnection来接收后台数据
//     * @param fromurl
//     * @return
//     */
//    public static String getHttpJsonByhttpurlconnection(String fromurl){
//        try {
//            Log.v("zms", "使用httpurlconnection");
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            byte[] data = new byte[1024];
//            int len = 0;
//            URL url = new URL(fromurl);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            InputStream in = conn.getInputStream();
//            while ((len = in.read(data)) != -1) {
//                os.write(data, 0, len);
//            }
//            in.close();
//            return new String(os.toByteArray());
//         }catch (Exception e){
//            e.printStackTrace();
//          }
//          return null;
//    }
    private static final String TAG = "uploadFile";

    private static final int TIME_OUT = 10 * 1000; // 超时时间

    private static final String CHARSET = "utf-8"; // 设置编码
    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输，get的方式
     *
     * @param url
     *            Service net address
     * @return String result of Service response
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        try {
            URL uri = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) uri.openConnection();
            InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            String result = "";
            while ((inputLine = buffer.readLine()) != null) {
                result += inputLine + "\n";
            }
            in.close();
            urlConn.disconnect();
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            return "请求异常，请重试";
        }
    }

}

