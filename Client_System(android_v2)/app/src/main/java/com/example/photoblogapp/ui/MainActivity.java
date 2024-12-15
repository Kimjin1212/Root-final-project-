package com.example.photoblogapp.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.photoblogapp.NotificationService;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.databinding.ActivityMainBinding;
import com.example.photoblogapp.ui.adapter.ImageAdapter;
import com.example.photoblogapp.ui.common.AppActivity;
import com.example.photoblogapp.ui.common.AppDialog;
import com.example.photoblogapp.utils.GsonUtils;
import com.example.photoblogapp.utils.PermissionUtils;
import com.example.photoblogapp.data.Photo;
import com.example.photoblogapp.R;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppActivity<ActivityMainBinding> {
    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 2901;

    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Photo> imageList = new ArrayList<>();
    private ImageAdapter adapter;
    private int pager = 1;
    private int total_pager = 1;

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Photo lastphoto = null;

    private void getLastdata() {

        Api.Get(Api.getLastImage, new Api.ApiCallBack() {
            @Override
            public void success(JSONObject result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lastphoto = JSON.parseObject(result.toJSONString(), Photo.class);
                        if (lastphoto != null) {
                            String lastlog = "";
                            if (lastphoto.tags.toLowerCase().contains("fall".toLowerCase())) {
                                lastlog += getResources().getString(R.string.falldown);
                                binding.nowstate.setText(R.string.falldown);
                            } else if (lastphoto.tags.toLowerCase().contains("nohuman".toLowerCase())) {
                                lastlog += getResources().getString(R.string.nohuman);
                                binding.nowstate.setText(getResources().getString(R.string.nohuman));
                            } else {
                                lastlog += getResources().getString(R.string.normal);
                                binding.nowstate.setText(R.string.normal);
                            }
                            lastlog += sdf.format(lastphoto.created_date);
                            lastlog += lastphoto.tags.replace("_", "     ");
                            binding.lastrecord.setText(lastlog);
                        }
                    }
                });
            }

            @Override
            public void successArray(JSONArray result) {

            }

            @Override
            public void fail(JSONObject result) {

            }
        });
    }

    private void getdata(Boolean loadmore) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pager", pager);
        jsonObject.put("time_range", "");
        jsonObject.put("tag", "");
        Api.Get(Api.queryphotos + "?" + Api.jsonToQueryString(jsonObject), new Api.ApiCallBack() {
            @Override
            public void success(JSONObject result) {
//                updateUI(re);
                List<Photo> photos = JSON.parseArray(result.getString("items"), Photo.class);
                updateUI(photos,loadmore);
                total_pager = result.getInteger("total_pages");
                total_pager = result.getInteger("total_pages");

            }

            @Override
            public void successArray(JSONArray result) {

            }

            @Override
            public void fail(JSONObject result) {

            }
        });
    }

    boolean isLoading = false;


    @Override
    public void init() {

        adapter = new ImageAdapter(imageList, MainActivity.this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // 上传按钮监听器
        binding.btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        // 同步按钮监听器
        binding.btnSync.setOnClickListener(v -> {
            pager = 1;
            imageList.clear();
            getdata(false);
            getLastdata();
        });
        binding.historyrecord.setOnClickListener(v -> {
            HistoryDialog dialog = new HistoryDialog(context, activity);
            dialog.callBack = result -> getdata(false);
            dialog.show();
        });
        adapter.setOnItemClickListener(new ImageAdapter.ImageCallBack() {
            @Override
            public void doClick(Photo photo) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add Tag");

                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (!input.getText().toString().isEmpty()) {
                        String tag = input.getText().toString();
                        Api.Get(Api.settages + "" + photo.id + "/" + photo.tags + "_" + tag + "/", new Api.ApiCallBack() {
                            @Override
                            public void success(JSONObject result) {
                                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Tag added: " + tag, Toast.LENGTH_SHORT).show());
                                getdata(false);
                            }

                            @Override
                            public void successArray(JSONArray result) {

                            }

                            @Override
                            public void fail(JSONObject result) {

                            }
                        });

                        getdata(false);
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Input tage name !", Toast.LENGTH_SHORT).show());
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }

            @Override
            public void doLongClick(Photo photo) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm delete the photo ?")
                        .setPositiveButton("sure", (dialogInterface, i) -> {
                            Api.Get(Api.deletephotos + "" + photo.id + "/", new Api.ApiCallBack() {
                                @Override
                                public void success(JSONObject result) {
                                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "delete success", Toast.LENGTH_SHORT).show());
                                    getdata(false);
                                }

                                @Override
                                public void successArray(JSONArray result) {

                                }

                                @Override
                                public void fail(JSONObject result) {

                                }
                            });
                        })
                        .setNegativeButton("No", null)
                        .create().show();
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // 检查是否需要加载更多数据
                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition >= totalItemCount) && firstVisibleItemPosition >= 0) {
                        if (pager < total_pager) {
                            pager += 1;
                            getdata(true);
                        }
                    }
                }
            }
        });
        binding.lastrecord.setOnClickListener(v -> {
            if (lastphoto != null) {
                PhotoDialog dialog = new PhotoDialog(context, activity);
                dialog.photo = lastphoto;
                dialog.callBack = new AppDialog.DialogCallBack() {
                    @Override
                    public void doresult(Object result) {
                        initdata();
                    }
                };
                dialog.show();
            }
        });
        initpremission();
    }

    @Override
    public void initdata() {

        getLastdata();
        getdata(false);
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }

    private void initpremission() {

        String[] permissions = {
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION, // 粗略位置权限
                Manifest.permission.READ_PHONE_STATE, // 读取电话状态权限
        };

        if (!PermissionUtils.hasPermissions(this, permissions)) {
            PermissionUtils.requestPermissions(this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(proj[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return null; // 如果无法获取路径，返回 null
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                uploadImage(new File(getRealPathFromURI(imageUri)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void uploadImage(File file) {
        OkHttpClient httpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");//设置类型，类型为八位字节流
        RequestBody requestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), requestBody)//文件名
                .addFormDataPart("post", "1")
                .addFormDataPart("tags", "UserUpload_")
                .addFormDataPart("create_time", sdf.format(new Date()))
                .build();
        Log.d(TAG, "uploadImage: " + Api.addphotos);
        Request request = new Request.Builder()
                .url(Api.addphotos)
                .post(multipartBody)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload fail", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //此方法运行在子线程中，不能在此方法中进行UI操作。
                String result = response.body().string();
                Log.d("upload file :", result);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload success", Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void updateUI(List<Photo> images,Boolean loadmore) {
        runOnUiThread(() -> {
            if (images.isEmpty()) {
                binding.textView.setText("불러온 이미지가 없습니다.");
            } else {
                binding.textView.setText("이미지 로드 성공!");
                if(!loadmore)
                    imageList.clear();;
                imageList.addAll(images);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
