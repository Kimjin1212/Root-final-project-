package com.example.photoblogapp.ui;

import static com.example.photoblogapp.ui.MainActivity.sdf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.data.Photo;
import com.example.photoblogapp.databinding.PhotoDialogBinding;
import com.example.photoblogapp.ui.common.AppDialog;
import com.example.photoblogapp.utils.GlideUtils;
import com.example.photoblogapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PhotoDialog extends AppDialog<PhotoDialogBinding> {

    Photo photo;
    List<EditText> alltaged = new ArrayList<>();

    public PhotoDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    public void init() {

        if (photo != null) {
            binding.close.setOnClickListener(v -> {

                if (callBack != null) {
                    callBack.doresult(true);
                }
                dismiss();
            });
            binding.createtime.setText(sdf.format(photo.created_date));
            String[] tagsarr = photo.tags.split("_");
            for (String item : tagsarr) {
                ;
                binding.tags.addView(addTagItem(item));
            }
            Glide.with(context)
                    .load(Api.baseaddr + photo.image)
                    .apply(GlideUtils.requestOptions)
                    .into(binding.image);
            binding.addtage.setOnClickListener(v -> {


                binding.tags.addView(addTagItem(""));

            });
        } else
            dismiss();

    }

    public EditText addTagItem(String value) {
        EditText editText = new EditText(context);
        editText.setText(value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tags = "";
                for (EditText e : alltaged) {
                    tags += e.getText().toString() + "_";
                }

                Api.Get(Api.settages + photo.id + "/" + tags + "/", new Api.ApiCallBack() {
                    @Override
                    public void success(JSONObject result) {

                    }
                    @Override
                    public void successArray(JSONArray result) {

                    }
                    @Override
                    public void fail(JSONObject result) {

                    }
                });
            }
        });
        alltaged.add(editText);
        return editText;
    }
}
