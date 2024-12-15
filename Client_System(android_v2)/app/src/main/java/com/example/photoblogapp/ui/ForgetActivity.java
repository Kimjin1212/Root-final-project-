package com.example.photoblogapp.ui;


import android.content.Intent;
import android.view.View;

import com.example.photoblogapp.data.User;
import com.example.photoblogapp.databinding.ActivityForgetBinding;
import com.example.photoblogapp.ui.common.AppActivity;
import com.example.photoblogapp.utils.Utils;

import java.util.Random;

public class ForgetActivity extends AppActivity<ActivityForgetBinding> {


    @Override
    public void init() {

        binding.sendcap.setOnClickListener(view -> binding.capture.setText(getRandomString(6)));
        binding.findpass.setOnClickListener(view -> dofindpass());
        binding.createnewuser.setOnClickListener((View.OnClickListener) view -> startActivity(new Intent(ForgetActivity.this, RegistActivity.class)));

    }

    private void dofindpass() {
        if (
                binding.phone.getText().toString().trim().isEmpty() ||
                        binding.password.getText().toString().trim().isEmpty() ||
                        binding.password2.getText().toString().trim().isEmpty() ||
                        binding.capture.getText().toString().trim().isEmpty()
        ) {
            Utils.toastinfo(this, "请填写完整信息！");
            return;
        }
        if (!binding.password.getText().toString().trim().equals(binding.password2.getText().toString().trim())) {
            Utils.toastinfo(this, "两次密码不一致！");
            return;
        }

    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public void initdata() {

    }
}