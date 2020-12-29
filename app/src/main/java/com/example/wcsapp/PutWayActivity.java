package com.example.wcsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.wcsapp.util.CustomToast;
import com.example.wcsapp.util.Datawedeentity;
import com.example.wcsapp.util.MyLOg;
import com.example.wcsapp.util.NetWorkPost;
import com.example.wcsapp.util.NetWorkUtils;
import com.example.wcsapp.util.ReplenishEntity;
import com.google.gson.JsonObject;

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

public class PutWayActivity extends AppCompatActivity {

    private static final String PROFILE1 = "Sacnner" ;
    private TextView goodshow;
    private TextView localid;
    private EditText PutWaynumber;
    private Button Apply;
    private Button Cancel;
    IntentFilter filter = new IntentFilter();
    //image
    private  ImageView imageViewbjLocalIDtext;
    private  ImageView imageViewbjPutWaynumbertext;
    private  TextView LocalIDtext;
    private  TextView PutWaynumbertext;
    private  TextView shownumber;
    private  TextView showlocalid;
    JSONObject json = new JSONObject();
    JSONObject showjson = new JSONObject();
    private List<ReplenishEntity>entityList ;


    private Handler handler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    CustomToast.showToast(PutWayActivity.this, json.getString("msg"), 1500);
                    goodshow.setText("");
                    localid.setText("");
                    Apply.setEnabled(false);
                    showlocalid.setText("");
                    PutWaynumber.setFocusable(false);
                    PutWaynumber.setText("");
                    break;
                case 0x002:
                    imageViewbjLocalIDtext.setVisibility(View.VISIBLE);
                    LocalIDtext.setVisibility(View.VISIBLE);
                    localid.setVisibility(View.VISIBLE);
                    break;
                case 0x003:
                    imageViewbjPutWaynumbertext.setVisibility(View.VISIBLE);
                    PutWaynumbertext.setVisibility(View.VISIBLE);
                    PutWaynumber.setVisibility(View.VISIBLE);
                    PutWaynumber.setFocusableInTouchMode(true);
                    Apply.setEnabled(true);
                    Apply.setTextColor(getResources().getColor(R.color.colorblack) );
                   // PutWaynumber.setEnabled(true);
                    break;
                case 0x005:
                    CustomToast.showToast(PutWayActivity.this, "服务器加载错误", 1500);
                    break;
                case 0x006:
                    MyLOg.e("putwaytime","连接超时");
                    CustomToast.showToast(getApplicationContext(),"连接超时",1000);
                    break;
                case 0x007:
                    CustomToast.showToast(getApplicationContext(),"连接错误",1000);
                    break;
                case 0x008:
                    if (!entityList.isEmpty()){
                        for (ReplenishEntity item:entityList) {
                            shownumber.setText("补货数量:"+item.getQty());
                            showlocalid.setText("补货库位:"+item.getLocation());
                            break;
                        }
                    }else {
                        CustomToast.showToast(getApplicationContext(),"此产品无上架任务！",2000);
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_way);
        filter.addAction(Datawedeentity.ACTION_RESULT_DATAWEDGE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(Datawedeentity.ACTIVITY_INTENT_FILTER_ACTION);  // The filtered action must match the "Intent action" specified in the DW Profile's Intent output configuration
        String Code128Value = "true";
        String EAN13Value = "true";
        initActivity();
        CreateProfile(PROFILE1, Code128Value, EAN13Value);
        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ko();
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodshow.setText("");
                localid.setText("");
                Apply.setEnabled(false);
                showlocalid.setText("");
                PutWaynumber.setFocusable(false);
                PutWaynumber.setText("");
              //  PutWaynumber.setText("");
            }
        });
    }

    public String id(String id) {
        JsonObject sku =new JsonObject();
        sku.addProperty("sku",id);
        return String.valueOf(sku);
    }
    private void ko(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                     sendByOKHttpio(NetWorkPost.URLInfos.PUTONSHELVES,surelightdata());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public  String surelightdata(){

       // String l = "{\"sku\":\""+goodshow.getText().toString()+"\",\"location\":\""+localid.getText().toString()+"\",\"qty\":\""+PutWaynumber.getText().toString()+"\"}";
        JsonObject js=new JsonObject();
        js.addProperty("sku",goodshow.getText().toString());
        js.addProperty("location",localid.getText().toString());
        js.addProperty("qty",PutWaynumber.getText().toString());
        MyLOg.d("putwaypostjson",String.valueOf(js));
        return String.valueOf(js);
    }
    //上架请求
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
                    Log.e("time","连接超时");
                    CustomToast.showToast(getApplicationContext(),"连接超时",1000);
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    Log.e("erro","连接错误");
                    CustomToast.showToast(getApplicationContext(),"连接错误",1000);
                }
            }
            @Override
            public void onResponse(Call call, Response response)  {
                if (response.code() == 200) {
                    try {
                        String s = response.body().string();
                        MyLOg.d("putway",s);
                        json = JSONObject.parseObject(s);
                        handler.sendEmptyMessage(0x001);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() != 200){
                    handler.sendEmptyMessage(0x005);
                }
            }
        });
    }

    //获取商品库位数量
    public void sendByOKHttpshow(final String url, final String parments) throws IOException {
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
                    Log.e("time","连接超时");
                    CustomToast.showToast(getApplicationContext(),"连接超时",1000);
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    Log.e("erro","连接错误");
                    CustomToast.showToast(getApplicationContext(),"连接错误",1000);
                }
            }
            @Override
            public void onResponse(Call call, Response response)  {
                if (response.code() == 200) {
                    try {
                        String s = response.body().string();
                        Log.d("oooo",s);
                            entityList = JSON.parseArray(s, ReplenishEntity.class);
                            handler.sendEmptyMessage(0x008);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() != 200){
                    handler.sendEmptyMessage(0x005);
                }
            }
        });
    }

    private void initActivity(){
        goodshow = findViewById(R.id.goodputshow);
        localid = findViewById(R.id.LocalIDputway);
        PutWaynumber = findViewById(R.id.PutWaynumber);
        Apply = findViewById(R.id.button_apply);
        Cancel = findViewById(R.id.button_clean);
        imageViewbjLocalIDtext = findViewById(R.id.imageViewbj);
        imageViewbjPutWaynumbertext = findViewById(R.id.imageViewbjPutWaynumbertext);
        LocalIDtext = findViewById(R.id.LocalIDtext);
        PutWaynumbertext = findViewById(R.id.PutWaynumbertext);
        shownumber = findViewById(R.id.textreplenishmentnumber);
        showlocalid = findViewById(R.id.textreplenishmentlocation);
        entityList = new ArrayList<>();
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(Broadcast, filter);
        Datawedeentity.sendDataWedgeIntentWithExtra(getApplicationContext(),
                Datawedeentity.ACTION_DATAWEDGE, Datawedeentity. EXTRA_GET_ACTIVE_PROFILE,
                Datawedeentity.EXTRA_EMPTY);
    }
    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(Broadcast);
    }
    private BroadcastReceiver Broadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Datawedeentity.EXTRA_RESULT_GET_ACTIVE_PROFILE)){
                if (intent.hasExtra(Datawedeentity.EXTRA_RESULT_GET_ACTIVE_PROFILE)) {
                    String activeProfile = intent.getStringExtra(Datawedeentity.EXTRA_RESULT_GET_ACTIVE_PROFILE);
                }
            }
            if (action.equals(Datawedeentity.ACTIVITY_INTENT_FILTER_ACTION)) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                    Toast.makeText(getApplicationContext(), "Error; " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }
    };
    // Display scanned data
    private void displayScanResult(Intent initiatingIntent, String howDataReceived)
    {
        //Id
        String decodedData = initiatingIntent.getStringExtra(Datawedeentity.DATAWEDGE_INTENT_KEY_DATA);
        //类型
        String decodedDecoder = initiatingIntent.getStringExtra(Datawedeentity.DATAWEDGE_INTENT_KEY_DECODER);
        if (!goodshow.getText().toString().equals("")&&!localid.getText().toString().equals("")) CustomToast.showToast(this,"请清空再扫描！",1500);
        if (goodshow.getText().toString().equals("")){
            goodshow.setText(decodedData);
            show(id(decodedData));
        }else if (localid.getText().toString().equals("")){
            localid.setText(decodedData);
            handler.sendEmptyMessage(0x003);
        }
    }

    public void show(final String de){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendByOKHttpshow(NetWorkPost.URLInfos.GETTASKLISTEXACT,de);
                    handler.sendEmptyMessage(0x002);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void CreateProfile (String profileName, String code128Value, String ean13Value){

        // Configure profile to apply to this app
        Bundle bMain = new Bundle();
        bMain.putString("PROFILE_NAME", profileName);
        bMain.putString("PROFILE_ENABLED", "true");
        bMain.putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST");  // Create profile if it does not exist

        // Configure barcode input plugin
        Bundle bConfigBarcode = new Bundle();
        bConfigBarcode.putString("PLUGIN_NAME", "BARCODE");
        bConfigBarcode.putString("RESET_CONFIG", "true"); //  This is the default

        // PARAM_LIST bundle properties
        Bundle bParamsBarcode = new Bundle();
        bParamsBarcode.putString("scanner_selection", "auto");
        bParamsBarcode.putString("scanner_input_enabled", "true");
        bParamsBarcode.putString("decoder_code128", code128Value);
        bParamsBarcode.putString("decoder_ean13", ean13Value);

        // Bundle "bParamsBarcode" within bundle "bConfigBarcode"
        bConfigBarcode.putBundle("PARAM_LIST", bParamsBarcode);

        // Associate appropriate activity to profile
        String activityName = new String();
        Bundle appConfig = new Bundle();
        appConfig.putString("PACKAGE_NAME", getPackageName());
        if (profileName.equals(PROFILE1))
        {
            activityName = PutWayActivity.class.getSimpleName();
        }
        String activityPackageName = getPackageName() + "." + activityName;
        appConfig.putStringArray("ACTIVITY_LIST", new String[] {activityPackageName});
        bMain.putParcelableArray("APP_LIST", new Bundle[]{appConfig});

        // Configure intent output for captured data to be sent to this app
        Bundle bConfigIntent = new Bundle();
        bConfigIntent.putString("PLUGIN_NAME", "INTENT");
        bConfigIntent.putString("RESET_CONFIG", "true");

        // Set params for intent output
        Bundle bParamsIntent = new Bundle();
        bParamsIntent.putString("intent_output_enabled", "true");
        bParamsIntent.putString("intent_action", Datawedeentity.ACTIVITY_INTENT_FILTER_ACTION);
        bParamsIntent.putString("intent_delivery", "2");

        // Bundle "bParamsIntent" within bundle "bConfigIntent"
        bConfigIntent.putBundle("PARAM_LIST", bParamsIntent);

        // Place both "bConfigBarcode" and "bConfigIntent" bundles into arraylist bundle
        ArrayList<Bundle> bundlePluginConfig = new ArrayList<>();
        bundlePluginConfig.add(bConfigBarcode);
        bundlePluginConfig.add(bConfigIntent);

        // Place bundle arraylist into "bMain" bundle
        bMain.putParcelableArrayList("PLUGIN_CONFIG", bundlePluginConfig);

        // Apply configs using SET_CONFIG: http://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
        Datawedeentity.sendDataWedgeIntentWithExtra(getApplicationContext(),
                Datawedeentity.ACTION_DATAWEDGE, Datawedeentity.EXTRA_SET_CONFIG, bMain);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}