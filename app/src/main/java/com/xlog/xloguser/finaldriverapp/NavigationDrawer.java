package com.xlog.xloguser.finaldriverapp;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xlog.xloguser.finaldriverapp.Adapters.DashboardTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Data.TransactionListDashboard;
import com.xlog.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.xlog.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.xlog.xloguser.finaldriverapp.Model.Login;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static int request_user_location = 99;
    private boolean locationPermissionGranted = false;
    private boolean locationPermissionGrantedContacts = false;
    private CircularImageView imageCircleView;
    private CircularImageView profileImageView;
    private TextView userFullname;
    private TextView driverMobileNumber;
    private NavigationView navView;
    private DrawerLayout drawer;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 120;
    View viewSnackBar;
    private static final String TAG = "NavigationDrawer";
    private Retrofit retrofit;
    private String profile_image = "";
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DashboardTransactionsModel> data;
    private static RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewSnackBar = findViewById(R.id.drawer_layout);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        imageCircleView = (CircularImageView) findViewById(R.id.imageCircleView);
        navView = (NavigationView) findViewById(R.id.nav_view);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.transactionRecycleViewer);
        recyclerView.setHasFixedSize(true);


//        tabPagerAdapter TabPagerAdapter = new tabPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(TabPagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView versionTxt = (TextView) navView.findViewById(R.id.appVersionTxt);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String verionCodeNumber = versionName + versionCode;
        versionTxt.setText("Version "+verionCodeNumber);
        loadApi();
        loadUserDetails();


        imageCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.openDrawer(Gravity.LEFT);
            }
        });

        getLocalPermissionContacts();
        internetChecking();
        recycleViewLoad();


    }
    public void loadUserDetails(){
        internetChecking();

        Bundle extras = getIntent().getExtras();
        String tokenString = extras.getString("access_Token");
        Log.e(TAG, "TOKEN__ "+tokenString);


        Api api = retrofit.create(Api.class);
        Call<UserDetails> call = api.getUserDetails(tokenString);
        Call<List<ReservationList>> call2 = api.getReservationList(tokenString);

        call2.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                int value = response.body().size();
                Log.e(TAG, "SIZE___ "+ value);
                for(int t = 0; t < value; t++){
                    Log.e(TAG, "Response +"+response.body().get(t).getPrefixedId());
                }
            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                String image_url = "https://xlog-dev.s3.amazonaws.com/";
                String value = response.body().getEntity().getImage().toString();
                String full_name = response.body().getEntity().getFirstName() + response.body().getEntity().getLastName();
                String mobile = response.body().getEntity().getMobileNumber();
                profile_image = image_url+value;
                loadImages(profile_image);
                loadDetails(full_name, mobile);
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.e(TAG, "Response Failure "+ t.getMessage());
                loadUserDetails();
            }
        });

    }
    public void loadApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.userDetails)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public void loadImages(String image_url){

        Picasso.get()
                .load(image_url)
                .resize(600, 200)
                .centerInside()
                .into(imageCircleView);

        if (navView != null) {
            LinearLayout mParent = (LinearLayout) navView.getHeaderView(0);

            if (mParent != null) {

                profileImageView = (CircularImageView) mParent.findViewById(R.id.profileImageCirle);
                Picasso.get()
                        .load(image_url)
                        .resize(600, 200)
                        .centerInside()
                        .into(profileImageView);
            }
        }
    }
    public void loadDetails(String fullname, String mobile_num){
        LinearLayout mParent = (LinearLayout) navView.getHeaderView(0);
        userFullname = (TextView) mParent.findViewById(R.id.fullNameTxt);
        driverMobileNumber = (TextView) mParent.findViewById(R.id.userNumberTxt);

        userFullname.setText(fullname);
        driverMobileNumber.setText(mobile_num);

    }






    private void getLocalPermissionContacts() {
        String[] permissions = {permission.CALL_PHONE, permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION, permission.CAMERA, permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission.CAMERA ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED)  {
            if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

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
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_SHORT).show();
                }

            }
            case READ_CONTACTS_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(this, "Permission Granted",
                            Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, CompleteTransactions.class);
            startActivity(intent);
        } else if (id == R.id.nav_complete) {
            Intent intent = new Intent(this, CompleteTransactions.class);
            startActivity(intent);

        } else if (id == R.id.nav_all) {
            Intent intent = new Intent(this, AllTrasactions.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);

        }else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawer.this);
            builder.setTitle("LOGOUT");
            builder.setMessage("Are you sure you want to logout");
            String positiveText = getString(android.R.string.ok);
            builder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // positive button logic
                            Intent intent = new Intent(NavigationDrawer.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            String negativeText = getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // negative button logic
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();
            // display dialog
            dialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            /**
             *Toast.makeText(getActivity(), "WiFi/Mobile Networks Connected!", Toast.LENGTH_SHORT).show();
             *int duration = Snackbar.LENGTH_LONG;
             *String message = "Internet Connection";
             *Snackbar.make(viewSnackBar, message, duration).show();
             */
        } else {
            int duration = Snackbar.LENGTH_LONG;
            String message = " No Internet Connection";
            Snackbar.make(viewSnackBar, message, duration).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        internetChecking();
    }

    private void recycleViewLoad(){
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        data = new ArrayList<DashboardTransactionsModel>();

        Log.e("LOG", "element: "+ TrasactionListSet.transactionID.length);
        for (int i = 0; i < TransactionListDashboard.transactionID.length; i++) {
            data.add(new DashboardTransactionsModel(
                    TrasactionListSet.transactionID[i]
            ));
        }
        adapter = new DashboardTransactionAdapter(data);
        recyclerView.setAdapter(adapter);
    }
}
