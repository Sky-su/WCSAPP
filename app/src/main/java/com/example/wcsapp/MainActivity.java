package com.example.wcsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonSuppliersout;
    private Button buttonbinding;
    private Button moveButton;
    private Button buttonup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        init();
        buttonSuppliersout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTastList = new Intent(getApplicationContext(),ShowTastListActivity.class);
                startActivity(showTastList);
            }
        });
        buttonbinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTastList = new Intent(getApplicationContext(), PutWayActivity.class);
                startActivity(showTastList);
            }
        });
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTastList = new Intent(getApplicationContext(),MovingGoodsActivity.class);
                startActivity(showTastList);
            }
        });
        buttonup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showTastList = new Intent(getApplicationContext(),UpwayActivity.class);
                startActivity(showTastList);
            }
        });


    }
    private void init(){
        buttonSuppliersout = findViewById(R.id.buttonSuppliersout);
        buttonbinding = findViewById(R.id.buttonbinding);
        moveButton = findViewById(R.id.buttonSetting);
        buttonup = findViewById(R.id.buttongetstork);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("用户退出吗？")
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