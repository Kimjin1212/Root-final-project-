package com.example.photoblogapp.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.photoblogapp.R;
import com.example.photoblogapp.api.Api;
import com.example.photoblogapp.data.Photo;
import com.example.photoblogapp.databinding.HistoryDialogBinding;
import com.example.photoblogapp.ui.adapter.ImageAdapter;
import com.example.photoblogapp.ui.common.AppDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryDialog extends AppDialog<HistoryDialogBinding> {

    public HistoryDialog(@NonNull Context context, Activity activity) {
        super(context, activity);
    }

    private ImageAdapter adapter;
    private int pager = 1;
    private int total_pager = 1;
    Calendar startCalendar, endCalendar;
    String querytage = "";
    private List<Photo> imageList = new ArrayList<>();
    private List<Photo> imageShowList = new ArrayList<>();
    private List<String> alltage = new ArrayList<>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private void getdata(boolean isloadmore) {
        if (!isloadmore)
            pager = 1;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pager", pager);
        jsonObject.put("tag", querytage);

        if (startCalendar != null && endCalendar != null) {

            jsonObject.put("time_range[]", "123");
            jsonObject.put("starttime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startCalendar.getTime()));
            jsonObject.put("endtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endCalendar.getTime().getTime()));

        }

        Api.Get(Api.queryphotos + "?" + Api.jsonToQueryString(jsonObject), new Api.ApiCallBack() {
            @Override
            public void success(JSONObject result) {
//                updateUI(re);
                List<Photo> photos = JSON.parseArray(result.getString("items"), Photo.class);
                updateUI(photos, isloadmore);
                total_pager = result.getInteger("total_pages");
                total_pager = result.getInteger("total_pages");

            }

            @Override
            public void fail(JSONObject result) {

            }

            @Override
            public void successArray(JSONArray result) {

            }
        });
    }

    private void getAllTagedata() {

        Api.Get(Api.getalltags, new Api.ApiCallBack() {
            @Override
            public void success(JSONObject result) {

            }

            @Override
            public void successArray(JSONArray result) {
                List<String> dataarr = JSON.parseArray(result.toJSONString(), String.class);
                activity.runOnUiThread(() -> {
                    if (dataarr != null) {

                        alltage.add("");
                        alltage.addAll(dataarr);
                        String lastlog = "";
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, alltage);
                        binding.choosestage.setAdapter(adapter);
                        binding.choosestage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                imageList.clear();
                                querytage = alltage.get(position);
                                getdata(false);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }

            @Override
            public void fail(JSONObject result) {

            }
        });
    }

    @Override
    public void init() {
        adapter = new ImageAdapter(imageList, context);
        binding.close.setOnClickListener(v -> {
            if (callBack != null)
                callBack.doresult(true);
            dismiss();
        });

        binding.choosetime.setOnClickListener(v -> {
            showDatePickerDialog(context, new OnDateSelectedListener() {
                @Override
                public void onDateSelected(int startyear, int startmonth, int startday, int starthour, int startminute) {

                    showDatePickerDialog(context, new OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(int endyear, int endmonth, int endday, int endhour, int endminute) {

                            startCalendar = Calendar.getInstance();
                            startCalendar.set(Calendar.YEAR, startyear);
                            startCalendar.set(Calendar.MONTH, startmonth);
                            startCalendar.set(Calendar.DAY_OF_MONTH, startday);
                            startCalendar.set(Calendar.HOUR_OF_DAY, starthour);
                            startCalendar.set(Calendar.MINUTE, startminute);


                            endCalendar = Calendar.getInstance();
                            endCalendar.set(Calendar.YEAR, endyear);
                            endCalendar.set(Calendar.MONTH, endmonth);
                            endCalendar.set(Calendar.DAY_OF_MONTH, endday);
                            endCalendar.set(Calendar.HOUR_OF_DAY, endhour);
                            endCalendar.set(Calendar.MINUTE, endminute);
                            imageList.clear();
                            getdata(false);
                            binding.timerange.setText(simpleDateFormat.format(startCalendar.getTime()) + " ~ " + simpleDateFormat.format(endCalendar.getTime()));
                        }
                    });
                }
            });
        });
        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // 检查是否需要加载更多数据
                    if (!(visibleItemCount + firstVisibleItemPosition >= totalItemCount) && firstVisibleItemPosition >= 0) {
                        if (pager < total_pager) {
                            pager += 1;
                            getdata(true);
                        }
                    }
                }
            }
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(context));
        binding.recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.ImageCallBack() {
            @Override
            public void doClick(Photo photo) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Add Tag");

                final EditText input = new EditText(activity);
                builder.setView(input);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (!input.getText().toString().isEmpty()) {
                        String tag = input.getText().toString();
                        Api.Get(Api.settages + "" + photo.id + "/" + photo.tags + "_" + tag + "/", new Api.ApiCallBack() {
                            @Override
                            public void success(JSONObject result) {
                                activity.runOnUiThread(() -> Toast.makeText(activity, "Tag added: " + tag, Toast.LENGTH_SHORT).show());
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
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Input tage name !", Toast.LENGTH_SHORT).show());
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            }

            @Override
            public void doLongClick(Photo photo) {
                new AlertDialog.Builder(activity)
                        .setTitle("Confirm delete the photo ?")
                        .setPositiveButton("sure", (dialogInterface, i) -> {
                            Api.Get(Api.deletephotos + "" + photo.id + "/", new Api.ApiCallBack() {
                                @Override
                                public void success(JSONObject result) {
                                    activity.runOnUiThread(() -> Toast.makeText(activity, "delete success", Toast.LENGTH_SHORT).show());
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

        getdata(false);
        getAllTagedata();
    }

    public void updateUI(List<Photo> images, boolean isloadmore) {

        activity.runOnUiThread(() -> {

            if (!isloadmore)
                imageList.clear();
            imageList.addAll(images);
            adapter.notifyDataSetChanged();
        });
    }

    public void showDatePickerDialog(Context context, OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            // 选择完成后回调
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (TimePicker view2, int selectedHour, int selectedMinute) -> {
                // 选择完成后回调
                listener.onDateSelected(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
            }, hour, minute, true); // `true` 表示使用 24 小时制
            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day, int hour, int minute);
    }
}
