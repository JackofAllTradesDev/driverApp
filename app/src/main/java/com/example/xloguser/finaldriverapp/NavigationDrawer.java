package com.example.xloguser.finaldriverapp;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static int request_user_location = 99;
    private boolean locationPermissionGranted = false;
    private boolean locationPermissionGrantedContacts = false;
    private CircularImageView imageCircleView;
    private CircularImageView profileImageView;
    private NavigationView navView;
    private DrawerLayout drawer;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        imageCircleView = (CircularImageView) findViewById(R.id.imageCircleView);
        navView = (NavigationView) findViewById(R.id.nav_view);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        tabPagerAdapter TabPagerAdapter = new tabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(TabPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView versionTxt = (TextView) navView.findViewById(R.id.appVersionTxt);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String verionCodeNumber = versionName + versionCode;
        versionTxt.setText("Version "+verionCodeNumber);


        String url = "https://i.ytimg.com/vi/P2AE3J0BB2o/maxresdefault.jpg";

        Picasso.get()
                .load(url)
                .resize(6000, 2000)
                .centerCrop()
                .into(imageCircleView);

        if (navView != null) {
            LinearLayout mParent = (LinearLayout) navView.getHeaderView(0);

            if (mParent != null) {

                profileImageView = (CircularImageView) mParent.findViewById(R.id.profileImageCirle);
                Picasso.get()
                        .load(url)
                        .resize(6000, 2000)
                        .centerCrop()
                        .into(profileImageView);
            }
        }


        imageCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.openDrawer(Gravity.LEFT);
            }
        });
        getLocalPermission();
        getLocalPermissionContacts();

    }


    private void getLocalPermission() {
        String[] permissions = {permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )  {
            if (ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Activated ",
                        Toast.LENGTH_LONG).show();
                locationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this, permissions, request_user_location);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, request_user_location);
        }
    }
    private void getLocalPermissionContacts() {
        String[] permissions = {permission.CALL_PHONE};

        if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE ) == PackageManager.PERMISSION_GRANTED )  {
            if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "CALL_PHONE Activated ",
                        Toast.LENGTH_LONG).show();
                locationPermissionGrantedContacts = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case request_user_location: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_LONG).show();
                }

            }
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pending) {
            // Handle the camera action
        } else if (id == R.id.nav_complete) {

        } else if (id == R.id.nav_all) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        }else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
