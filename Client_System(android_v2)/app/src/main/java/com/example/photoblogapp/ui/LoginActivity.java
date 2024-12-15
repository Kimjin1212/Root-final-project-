package com.example.photoblogapp.ui;

import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.data.User;
import com.example.photoblogapp.databinding.ActivityLoginBinding;
import com.example.photoblogapp.ui.common.AppActivity;
import com.example.photoblogapp.utils.GsonUtils;
import com.example.photoblogapp.utils.SPUtils;
import com.example.photoblogapp.utils.Utils;

public class LoginActivity extends AppActivity<ActivityLoginBinding> {


    @Override
    public void init() {
        binding.goregist.setOnClickListener(v -> {
            startActivity(new Intent(context, RegistActivity.class));
        });
        binding.login.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", binding.username.getText().toString().trim());
            jsonObject.put("password", binding.password.getText().toString().trim());
            User user = new User();
            user.username = binding.username.getText().toString().trim();
            user.password = binding.password.getText().toString().trim();
            Api.Post(Api.loginurl, GsonUtils.getGson().toJson(user), new Api.ApiCallBack() {
                @Override
                public void success(JSONObject result) {
                    Utils.toastinfo(activity, "登陆成功");
                    SPUtils.putString("token", result.getString("token"));
                    SPUtils.putString("loginusername", binding.username.getText().toString());
                    SPUtils.putString("loginpassword", binding.password.getText().toString());
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
                @Override
                public void successArray(JSONArray result) {

                }
                @Override
                public void fail(JSONObject result) {
                    Utils.toastinfo(activity, "登陆失败");
                }
            });
        });
    }

    @Override
    public void initdata() {
        binding.username.setText(SPUtils.getString("loginusername", ""));
        binding.password.setText(SPUtils.getString("loginpassword", ""));

    }
}