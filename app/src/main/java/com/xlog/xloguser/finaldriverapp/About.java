package com.xlog.xloguser.finaldriverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class About extends AppCompatActivity {

    private TextView content;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        version = (TextView)findViewById(R.id.appVersionAbout);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.aboutToolbar);
        mToolbar.setTitle("About");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String verionCodeNumber = versionName + versionCode;


        version.setText("Version "+versionName);

    }
}
