package com.example.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求服务的工具类
 * <p/>
 * Created by szjdj on 2016-09-27.
 */
public class RequestUtil extends Activity {

    public static Map<String, String> getPDAServerData(String url) {
        String ProcessUrl ="http://120.76.22.150:8080/CellBank/";
        String description = "";
        String message = "";
        String username = "";
        String userid = "";
        ProcessUrl = ProcessUrl + url;
        HttpClient client = new DefaultHttpClient();
        HttpPost request;
        try {
            request = new HttpPost(new URI(ProcessUrl));
            HttpResponse response = client.execute(request);
            // 判断请求是否成功
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String out = EntityUtils.toString(entity);
                    JSONObject jsonobj = new JSONObject(out);
                    JSONArray jsonArray = jsonobj.getJSONArray(("JsonArry"));
                    message = (String) jsonArray.getJSONObject(0).opt("message");
                    description = (String) jsonArray.getJSONObject(0).opt("description");
                    username = (String) jsonArray.getJSONObject(0).opt("username");
                    userid = (String) jsonArray.getJSONObject(0).opt("userid");
                    System.out.println("##############" + jsonArray.toString());
                    Map<String, String> map = new HashMap<String, String>();
                    if (message != null) {
                        System.out.println("##############" + message);
                        map.put("message", message);
                    }
                    if (description != null) {
                        System.out.println("##############" + description);
                        map.put("description", description);
                    }
                    if (username != null) {
                        System.out.println("##############" + username);
                        map.put("username", username);
                    }
                    if (userid != null) {
                        System.out.println("##############" + userid);
                        map.put("userid", userid);
                    }
                    return map;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //判断是否有网络连接
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobNetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (activeNetInfo.isConnected()) {
            return true;
        }
        if (mobNetInfo.isConnected()) {
            return true;
        }
        if (!activeNetInfo.isConnected() && !mobNetInfo.isConnected()) {
            Toast.makeText(context, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }


}
