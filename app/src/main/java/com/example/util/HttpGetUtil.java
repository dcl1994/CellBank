package com.example.util;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/11/3.
 */
public class HttpGetUtil {
    public static String httpPost(String url,String userid) {
//        String ProcessUrl = ;
        List list = new ArrayList();
//        list.add(new BasicNameValuePair("userid", "1"));
        list.add(new BasicNameValuePair("userid", userid));
        Log.e("httpGetUtil","1111userid="+userid);
        HttpPost httpPost = new HttpPost(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //设置编码
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            //发送Post,并返回一个HttpResponse对象
            HttpResponse response = httpclient.execute(httpPost);
            Log.d("httpGetUtil", "execute OK!");
            if (response.getStatusLine().getStatusCode() == 200) {
                Log.e("zms","请求成功");
                String returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                Log.e("zms", "返回值" + returnStr);
//                Log.d("httpGetUtil", "1execute OK!");
                return returnStr;
            } else {
                Log.e("zms", "访问网络返回数据失败，错误码:" + response.getStatusLine().getStatusCode());
                Log.d("httpGetUtil", "2execute OK!");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String httpPost(String url,String title,String userid,String type,String content) throws Exception {
        List list = new ArrayList();
        list.add(new BasicNameValuePair("content", content));
        list.add(new BasicNameValuePair("title", title));
        list.add(new BasicNameValuePair("userid", userid));
        list.add(new BasicNameValuePair("type", type));
        HttpPost httpPost = new HttpPost(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //设置编码
        httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        //发送Post,并返回一个HttpResponse对象
        HttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            String returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.e("zms", "返回值" + returnStr);
            return returnStr;
        } else {
            Log.e("zms", "访问网络返回数据失败，错误码:" + response.getStatusLine().getStatusCode());
            return null;
        }
    }

    /**
     * @param url   请求地址
     * @param userid  用户id
     * @param content  内容
     * @param type     类型，1为评论 2为回复
     * @param upId     上级id
     * @return
     * @throws Exception
     */
    public static String httpPost(String url,String userid,String content,Integer type,Long upId) throws Exception {
        List list = new ArrayList();
        list.add(new BasicNameValuePair("content", content));
        list.add(new BasicNameValuePair("userid", userid));
        if(type==1) {
            list.add(new BasicNameValuePair("topicId", upId+""));
        }
        if(type == 2){
            list.add(new BasicNameValuePair("commentId",upId+""));
        }
        HttpPost httpPost = new HttpPost(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //设置编码
        httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        //发送Post,并返回一个HttpResponse对象
        HttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            String returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.e("zms", "返回值" + returnStr);
            return returnStr;
        } else {
            Log.e("zms", "访问网络返回数据失败，错误码:" + response.getStatusLine().getStatusCode());
            return null;
        }
    }
    public static String httpPost(String url,Map<String ,String > params) throws Exception {
//        String ProcessUrl = "http://120.76.22.150:8080/CellBank/";
        List list = new ArrayList();
        for (Map.Entry entry : params.entrySet()) {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<"+entry.getKey()+":"+entry.getValue());
            list.add(new BasicNameValuePair(entry.getKey()+"",""+ entry.getValue()));
        }
        HttpPost httpPost = new HttpPost(url);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        //设置编码
        httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
        //发送Post,并返回一个HttpResponse对象
        HttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            String returnStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            Log.e("zms", "返回值" + returnStr);
            return returnStr;
        } else {
            Log.e("zms", "访问网络返回数据失败，错误码:" + response.getStatusLine().getStatusCode());
            return null;
        }
    }

    public static String httpGet(String fromurl) throws IOException {
        Log.e("zms", "使用httget");
        HttpGet geturl = new HttpGet(fromurl);
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpResponse response = httpclient.execute(geturl);
        Log.e("zms", "响应码" + response.getStatusLine().getStatusCode());

        if (response.getStatusLine().getStatusCode() == 200) {
            String returnStr = EntityUtils.toString(response.getEntity(), "utf-8");
            Log.e("zms", "返回值" + returnStr);
            return returnStr;

        } else {
            Log.e("zms", "访问网络返回数据失败，错误码:" + response.getStatusLine().getStatusCode());
        }
        return null;
    }
}
