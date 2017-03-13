package com.example.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by szjdj on 2016-11-15.
 */
public class UrlUtils {
    public String post(String target ,String parm){
        URL url;
        try{
            url = new URL(target);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("Post");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);

        }catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }
}
