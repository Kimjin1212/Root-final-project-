package com.example.photoblogapp.api;
//这段代码主要用于处理API请求它使用了OkHttp库来发送HTTP请求，并使用了Qianfan库来与ERNIE-Bot-4模型进行交互。

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.photoblogapp.MyApp;
import com.example.photoblogapp.utils.SPUtils;
import com.example.photoblogapp.utils.Utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {
    public static String ipaddr = "192.168.1.12";
    public static String port = "8000";
    public static String baseaddr = "http://" + ipaddr + ":" + port;
    public static String baseaddr_port = "http://" + ipaddr + ":" + port;
    public static String basehttpaddr = "http://" + ipaddr + ":" + port + "/";
    public static String base_media_httpaddr = "http://" + ipaddr + ":" + port + "/media/";

    public static String getposts = baseaddr + "/api/posts/";
    public static String addphotos = baseaddr + "/api/addPhotos";
    public static String settages = baseaddr + "/api/settages/";
    public static String deletephotos = baseaddr + "/api/deletephotos/";

    public static String loginurl = baseaddr + "/users/login";
    public static String registerurl = baseaddr + "/users/register";
    public static String getLastImage = baseaddr + "/api/getlastone";
    public static String getalltags = baseaddr + "/api/getalltags";
    public static String queryphotos = baseaddr + "/api/queryphotos";
    public static String queryNotify = baseaddr + "/api/queryNotify";

    public interface ApiCallBack {
        public void success(com.alibaba.fastjson.JSONObject result);

        public void fail(com.alibaba.fastjson.JSONObject result);
        public void successArray(JSONArray result);
    }

    public static void Get(String url, ApiCallBack callBack) {
        Utils.loginfo(url);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        Request request = new Request
                .Builder()
                .addHeader("authorization", SPUtils.getString("token", ""))
                .url(url)
                .get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                SPUtils.clear();
                callBack.fail(new JSONObject());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String respstring = response.body().string();
                Utils.loginfo(respstring);
                try {
                    JSONObject respjson = JSONObject.parseObject(respstring);
                    callBack.success(respjson);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        JSONArray respjson = JSONArray.parseArray(respstring);
                        callBack.successArray(respjson);
                    }catch (Exception ex) {

                    }
                    callBack.fail(new JSONObject());
                }
            }
        });
    }

    public static void Post(String url, String jsonObject, ApiCallBack callBack) {
        Utils.loginfo(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonObject);
        Request request = new Request
                .Builder()
                .post(requestBody)  // Post请求的参数传递
                .addHeader("authorization", SPUtils.getString("token", ""))
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                SPUtils.clear();
                callBack.fail(new JSONObject());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String respstring = response.body().string();
                Utils.loginfo(respstring);
                try {
                    JSONObject respjson = JSONObject.parseObject(respstring);
                    callBack.success(respjson);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.fail(new JSONObject());
                }

            }
        });
    }

    public static String jsonToQueryString(JSONObject jsonObject) {
        StringBuilder queryString = new StringBuilder();
        for (String key : jsonObject.keySet()) {
            try {
                String value = jsonObject.getString(key);
                queryString.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(value, "UTF-8"))
                        .append("&");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 去掉最后一个 "&"
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

}
