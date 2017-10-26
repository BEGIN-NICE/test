package com.example.administrator.myapplicationdownload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DownloadUtils downloadUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadUtils =   new DownloadUtils(MainActivity.this);
        downloadUtils.downloadAPK("http://download.app.2345.com/calendar2345/auto/12/my-toolsm_top.apk?12", "my-toolsm_top.apk");
    }
}
