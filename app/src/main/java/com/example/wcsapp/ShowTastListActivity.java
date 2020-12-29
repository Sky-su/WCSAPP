package com.example.wcsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.wcsapp.util.CustomToast;
import com.example.wcsapp.util.DialogCustom;
import com.example.wcsapp.util.LayList;
import com.example.wcsapp.util.MyLOg;
import com.example.wcsapp.util.NetWorkPost;
import com.example.wcsapp.util.NetWorkUtils;
import com.example.wcsapp.util.RefreshableView;
import com.example.wcsapp.util.ReplenishEntity;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowTastListActivity extends AppCompatActivity {
    Toolbar toolbar;
    ListView listview;
    List<ReplenishEntity> list;
    LayList replenishview;
    private RefreshableView refreshableView;
    private DialogCustom dialogCustom;
    private EditText timerTaskdialog;
    boolean isList = true;
    private ProgressDialog progressDialog;
    private String refreshtime ;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tast_list);
        toolbar = findViewById(R.id.toolbar);
        initToolbar();
        listview = findViewById(R.id.showTastList);
        refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
        setupProgressDialog();
        refreshtime = gettimetext();
        Log.d("time",refreshtime);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkUtils.getAPNType(ShowTastListActivity.this)!=0){
                    try {
                        Thread.sleep(2000);
                        list.clear();
                        handler.sendEmptyMessage(0x002);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    refreshableView.finishRefreshing();
                }else{
                    CustomToast.showToast(getApplicationContext(),"无网络",1000);
                    refreshableView.finishRefreshing();
                }
            }
        }, 0);
        list = new ArrayList<ReplenishEntity>();
        if (NetWorkUtils.getAPNType(ShowTastListActivity.this)!=0){
            mHandler.postDelayed(r, 100);
        }else{
            if (progressDialog.isShowing())dialogCustom.dismiss();
            CustomToast.showToast(getApplicationContext(),"无网络",1000);
        }
    }
    Runnable r = new Runnable() {
        @Override
        public void run() {
            //默认每隔60s循环执行run方法
                list.clear();
                handler.sendEmptyMessage(0x002);
                mHandler.postDelayed(this,(Integer.valueOf(refreshtime)*60000));
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch(msg.what){
                case 0x11:
                    showlist(list);
                    progressDialog.dismiss();
                    CustomToast.showToast(getApplicationContext(),"刷新成功",1000);
                    break;
                    case 0x002:
                        try {
                            if (!progressDialog.isShowing())progressDialog.show();
                            sendByOKHttpio(NetWorkPost.URLInfos.GETTASKLIST,"");
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                case 0x005:
                    MyLOg.e("show","服务器加载错误");
                    CustomToast.showToast(ShowTastListActivity.this, "服务器加载错误", 1500);
                    break;
                case 0x006:
                        CustomToast.showToast(getApplicationContext(),"连接超时",1000);
                        break;
                case 0x007:
                    CustomToast.showToast(getApplicationContext(),"连接错误",1000);
                    break;

            }
        }
    };


    //标题栏
    private void initToolbar() {
        //设置支持toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        // 设置主标题或者子标题是否应该被显示，隐藏掉toolbar自带的主标题和子标题
        actionBar.setDisplayShowTitleEnabled(false);
        //返回按钮点击事件
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }
    //赋值
    private void showlist(List<ReplenishEntity> a) {
        if (isList){
            replenishview = new LayList(a,this);
            listview.setAdapter(replenishview);
            isList = false;
        }else{
            replenishview.showaddlist(list);
        }

    }

    //创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_settingtime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            dialogCustom = new DialogCustom(this,R.layout.dialog_time);
            dialogCustom.setDismissButtonId(R.id.dialog_no);
            View viewtime = dialogCustom.setString(R.id.dialog_yes);
            timerTaskdialog= dialogCustom.setString1(R.id.dialog_time);
            if (refreshtime != null) timerTaskdialog.setText(refreshtime);
            viewtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref = getSharedPreferences("timeName",MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putString("time",timerTaskdialog.getText().toString());
                    prefEditor.commit();
                    mHandler.removeCallbacks(r);
                    mHandler.postDelayed(r,(Integer.valueOf(timerTaskdialog.getText().toString())*60000));
                    dialogCustom.dismiss();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //储存定时器
    private String gettimetext(){
        SharedPreferences sharedPreferences= getSharedPreferences("timeName", Context.MODE_PRIVATE);
        String timetext = sharedPreferences.getString("time","");
        if(!timetext.equals("")){
            MyLOg.d("showtime",timetext);
           return timetext;
        }
        MyLOg.d("showtime","10");
        return "10";
    }

    public void sendByOKHttpio(final String url, final String parments) throws IOException {
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"),parments);
        Request request = new Request.Builder().url(url)
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e instanceof SocketTimeoutException){//判断超时异常
                    MyLOg.e("replenishmenttime","连接超时");
                   handler.sendEmptyMessage(0x006);
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    MyLOg.e("replenishmenterror","连接错误");
                    handler.sendEmptyMessage(0x007);
                }
            }
            @Override
            public void onResponse(Call call, Response response)  {
                if (response.code() == 200) {
                    MyLOg.d("showtimeconnect","sucess!");
                    try {
                        String s = response.body().string();
                        //MyLOg.d("replenishment", s);
                        showlist(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showlist(String response){
        list = JSON.parseArray(response, ReplenishEntity.class);
        Log.d("shop",list.toString());
        handler.sendEmptyMessage(0x11);
    }

    private void setupProgressDialog() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.app_show));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    protected void onStop() {
        super.onStop();
        list.clear();
        mHandler.removeCallbacks(r);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}