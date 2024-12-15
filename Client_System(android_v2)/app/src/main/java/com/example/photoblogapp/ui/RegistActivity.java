package com.example.photoblogapp.ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.data.User;
import com.example.photoblogapp.databinding.ActivityRegistBinding;
import com.example.photoblogapp.ui.common.AppActivity;
import com.example.photoblogapp.utils.GsonUtils;
import com.example.photoblogapp.utils.Utils;

public class RegistActivity extends AppActivity<ActivityRegistBinding> {


    @Override
    public void init() {
        binding.login.setOnClickListener(v -> {
            finish();
        });
        binding.registbtn.setOnClickListener(v -> {
            User user = new User();
            user.username = binding.phone.getText().toString().trim();
            user.password = binding.password.getText().toString().trim();
            Api.Post(Api.registerurl, GsonUtils.getGson().toJson(user), new Api.ApiCallBack() {
                @Override
                public void success(JSONObject result) {
                    Utils.toastinfo(activity,result.getString("msg"));
                    finish();
                }
                @Override
                public void successArray(JSONArray result) {

                }
                @Override
                public void fail(JSONObject result) {
                    Utils.toastinfo(activity,result.getString("msg"));
                }
            });
        });
    }

    @Override
    public void initdata() {

    }
}