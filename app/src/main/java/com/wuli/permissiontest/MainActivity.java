package com.wuli.permissiontest;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import core.NeedPermissons;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.premission_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissonCall();
            }


        });
    }

    @NeedPermissons(value = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void permissonCall() {
    }
}
