package com.example.wcsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.wcsapp.util.CustomToast;
import com.example.wcsapp.util.Datawedeentity;
import com.example.wcsapp.util.DialogCustom;
import com.example.wcsapp.util.MyLOg;
import com.example.wcsapp.util.NetWorkPost;
import com.example.wcsapp.util.NetWorkUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText loginUser;
    EditText loginPWD;
    Button loginButton;
    private  ProgressDialog progressDialog = null;
    JSONObject json = new JSONObject();
    private static final String PROFILE1 = "Sacnner" ;
    //全局定义
    private long lastClickTime = 0L;
    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                     progressDialog.dismiss();
                    CustomToast.showToast(LoginActivity.this, json.getString("msg"), 1500);
                    if ("1".equals(json.getString("msg"))){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                    break;
                case 0x002:
                    progressDialog.dismiss();
                    CustomToast.showToast(LoginActivity.this,"连接失败！", 1500);
                    break;
                case 0x005:
                    progressDialog.dismiss();
                    CustomToast.showToast(LoginActivity.this, "服务器加载错误", 1500);
                    break;
                case 0x006:
                    progressDialog.dismiss();
                    CustomToast.showToast(getApplicationContext(),"连接超时",1000);
                    break;
                case 0x007:
                    progressDialog.dismiss();
                    CustomToast.showToast(getApplicationContext(),"连接错误",1000);
                    break;
                case 0x008:
                    if (!progressDialog.isShowing())progressDialog.show();
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        String Code128Value = "true";
        String EAN13Value = "true";
        CreateProfile(PROFILE1, Code128Value, EAN13Value);
        setupProgressDialog();
        requestPermission();
       // String mac = getDeviceSN();
//       // if (!mac.equals("18062521401239")){
//            final DialogCustom dialogCustom = new DialogCustom(this,R.layout.dialog_mac);
//            View viewmac = dialogCustom.setString(R.id.dialog_yes);
//            viewmac.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialogCustom.dismiss();
//                    LoginActivity.this.finish();
//                }
//            });
//      //  }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i =  NetWorkUtils.getAPNType(LoginActivity.this);
                if (i != 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (getisedit()){
                                if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_DELAY_TIME ) {
                                    lastClickTime = System.currentTimeMillis();
                                    try {
                                        handler.sendEmptyMessage(0x008);
                                        sendByOKHttpio(NetWorkPost.URLInfos.PDALOGIN,NetWorkPost.logininfo(loginUser.getText().toString(),loginPWD.getText().toString()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    CustomToast.showToast(LoginActivity.this, "您点的太快啦！", 1500);
                                }
                            }
                        }
                    }).start();
                }else{
                    CustomToast.showToast(LoginActivity.this, "无网络！", 1500);

                }
            }
        });
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
            activityName = MovingGoodsActivity.class.getSimpleName();
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

    public static String getDeviceSN(){
        String serialNumber = android.os.Build.SERIAL;
        return serialNumber;
    }
    private int REQUEST_PERMISSION_CODE = 1000;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.INTERNET};
    private android.app.AlertDialog alertDialog;
    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 42) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onPermissionsResult:", "权限" + permissions[0] + "申请成功");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.i("onPermissionsResult:", "用户拒绝了权限申请");
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("permission")
                        .setMessage("点击允许才可以使用我们的app哦")
                        .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                ActivityCompat.requestPermissions(LoginActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        });
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }
    //实列化
    public void init() {
        loginUser = (EditText) findViewById(R.id.login_user);
        loginPWD = (EditText) findViewById(R.id.login_PASSword);
        loginButton = (Button) findViewById(R.id.button_apply);
    }
    private void loginsucess(){
        if (getisedit()){
            int i =  NetWorkUtils.getAPNType(LoginActivity.this);
            if(i!=0){
                if (!progressDialog.isShowing())progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendByOKHttpio(NetWorkPost.URLInfos.PDALOGIN,NetWorkPost.logininfo(loginUser.getText().toString(),md5(loginPWD.getText().toString())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            } else{
                CustomToast.showToast(getApplicationContext(),"无网络",1000);
            }

        }
    }
    public boolean getisedit() {
        if (loginUser.getText().toString().isEmpty()){
            CustomToast.showToast(this,"用户名为空！",1500);
            return false;
        }else if(loginPWD.getText().toString().isEmpty()) {
            CustomToast.showToast(this,"密码为空！",1500);
            return false;
        }
        return true;
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
                    MyLOg.e("time","连接超时");
                    handler.sendEmptyMessage(0x006);
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    MyLOg.e("erro","连接错误");
                    handler.sendEmptyMessage(0x007);
                }
            }
            @Override
            public void onResponse(Call call, Response response)  {
                if (response.code() == 200) {
                    try {
                        String s = response.body().string();
                        json = JSONObject.parseObject(s);
                        Log.d("login",s);
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

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setupProgressDialog() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.app_login_info));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }
}