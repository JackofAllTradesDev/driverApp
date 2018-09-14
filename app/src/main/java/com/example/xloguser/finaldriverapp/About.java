package com.example.xloguser.finaldriverapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


public class About extends AppCompatActivity {

    private TextView content;
    private TextView version;
    private TextView contactUs;
    private TextView contactUs2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        content = (TextView)findViewById(R.id.content);
        version = (TextView)findViewById(R.id.appVersionAbout);
        contactUs = (TextView)findViewById(R.id.contactUsTxt);
        contactUs2 = (TextView)findViewById(R.id.contactUsTxt2);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.aboutToolbar);
        mToolbar.setTitle("About");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String verionCodeNumber = versionName + versionCode;

        content.setText("XLOG is an online digital platform for the global shipping & logistics industry. Our business model " +
                "creates a compelling set of business values which promotes real-time logistics resulting into optimal " +
                "process efficiencies, unmatched savings and radical improvements in revenues.");

        version.setText("Version "+verionCodeNumber);
        contactUs.setText("info@myxlog.com");
        contactUs2.setText("+63 2 869 7340");

    }
}
