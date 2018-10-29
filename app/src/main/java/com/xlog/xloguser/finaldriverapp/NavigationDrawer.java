package com.xlog.xloguser.finaldriverapp;

import android.Manifest.permission;
import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.Dash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.xlog.xloguser.finaldriverapp.Adapters.AllTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.DashboadAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.xlog.xloguser.finaldriverapp.Model.DashboardTransactionsModel;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;
import com.xlog.xloguser.finaldriverapp.Room.Entity.Coordinates;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
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
    private static final String TAG = "NavigationDrawer";
    private final static int request_user_location = 99;
    private boolean locationPermissionGranted = false;
    private boolean locationPermissionGrantedContacts = false;
    private CircularImageView imageCircleView;
    private CircularImageView profileImageView;
    private TextView userFullname;
    private TextView driverMobileNumber;
    private TextView today, username;
    private TextView upcoming, warning;
    private NavigationView navView;
    private DrawerLayout drawer;
    private ProgressDialog progressDialogdialog;
    private CardView todayTr, upcomingTr;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 120;
    View viewSnackBar;
    private Retrofit retrofit;
    private String profile_image = "";
    private DashboadAdapter dashboadAdapter;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    List<ReservationList> transactionList;
    List<ReservationList> reservationListList;
    List<ReservationList> upcomingList;
    String dateString ="";
    String tokens;
    int driverId;


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
        today = (TextView) findViewById(R.id.todayTxt);
        username = (TextView) findViewById(R.id.userNameDashTxt);
        upcoming = (TextView) findViewById(R.id.upcomingTxt);
        todayTr = (CardView) findViewById(R.id.cardViewToday);
        warning = (TextView) findViewById(R.id.warningTxt);
        upcomingTr = (CardView) findViewById(R.id.cardViewUpComing);
        transactionList = new ArrayList<>();
        reservationListList = new ArrayList<>();
        upcomingList = new ArrayList<>();
        todayTr.setEnabled(true);

            //        tabPagerAdapter TabPagerAdapter = new tabPagerAdapter(getSupportFragmentManager());
           //        viewPager.setAdapter(TabPagerAdapter);
          //        tabLayout.setupWithViewPager(viewPager);
        Fabric.with(this, new Crashlytics());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView versionTxt = (TextView) navView.findViewById(R.id.appVersionTxt);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String verionCodeNumber = versionName;
        versionTxt.setText("Version "+verionCodeNumber);
        todayTr.setEnabled(false);

        todayTr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getBaseContext()).isOnline()) {
                    todayList();
                }else{
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
                    alertBuilder.setTitle("You're Offline");
                    alertBuilder.setMessage("Please Check your network");
                    String positiveText = getString(android.R.string.ok);
                    alertBuilder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }



            }
        });
        upcomingTr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getBaseContext()).isOnline()) {
                   upcomingList();
                }else{
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
                    alertBuilder.setTitle("You're Offline");
                    alertBuilder.setMessage("Please Check your network");
                    String positiveText = getString(android.R.string.ok);
                    alertBuilder.setPositiveButton(positiveText,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }



            }
        });
        imageCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.openDrawer(Gravity.LEFT);
            }
        });

        getLocalPermissionContacts();
        internetChecking();
        loadApi();

        SimpleDateFormat formatter
                = new SimpleDateFormat ("yyyy-MM-dd");
        Date currentTime_1 = new Date();
         dateString = formatter.format(currentTime_1);
         Log.e(TAG, "DATE "+dateString);



    }

    private void todayList(){
        transactionList.clear();
        progressDialogdialog = new ProgressDialog(NavigationDrawer.this);
        progressDialogdialog.setMessage("Fetching Data");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call2 = api.getReservationList(tokens);

        call2.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    int value = response.body().size();
                    String date= "";

                        for(int t = 0; t < value; t++){
                            date = response.body().get(t).getDeliveryDates().get(0).getDeliveryAt().substring(0,10);
                            SimpleDateFormat formatApiDate = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date apiDate = formatApiDate.parse(date);
                                Date current = currentDate.parse(dateString);
                                if(dateString.equalsIgnoreCase(date)){
                                    transactionList.addAll(Collections.singleton(response.body().get(t)));

                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    if(transactionList.size() == 0){
                        warning.setVisibility(View.VISIBLE);
                        warning.setText("You don't have transactions for today");
                    }
                        generateEmployeeList();
                        upcomingTr.setEnabled(true);
                        todayTr.setEnabled(false);

                    }
                    progressDialogdialog.dismiss();
                }



            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                Log.e(TAG, "error today "+t.getMessage());
                erroMessageToday(t.getMessage());
            }
        });
    }
    private void upcomingList(){
        upcomingList.clear();
        progressDialogdialog = new ProgressDialog(NavigationDrawer.this);
        progressDialogdialog.setMessage("Fetching Data");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call2 = api.getReservationList(tokens);

        call2.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {

                    if(response.isSuccessful()){
                        int value = response.body().size();
                        String date= "";

                        for(int t = 0; t < value; t++){
                            date = response.body().get(t).getDeliveryDates().get(0).getDeliveryAt().substring(0,10);
                            SimpleDateFormat formatApiDate = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date apiDate = formatApiDate.parse(date);
                                Date current = currentDate.parse(dateString);
                                if(current.before(apiDate)){
                                    upcomingList.addAll(Collections.singleton(response.body().get(t)));
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e(TAG, "upcomingList "+upcomingList.size());
                        if(upcomingList.size() == 0){
                            warning.setVisibility(View.VISIBLE);
                            warning.setText("You don't have upcoming transactions");
                        }
                        generateUpcomingList();
                        upcomingTr.setEnabled(false);
                        todayTr.setEnabled(true);
                    }
                    progressDialogdialog.dismiss();
                }



            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                Log.e(TAG, "error upcoming "+t.getMessage());
                errorUpcoming(t.getMessage());
            }
        });
    }
    public void loadUserDetails(String Token){
        transactionList.clear();
        upcomingList.clear();
        Log.e(TAG, "TOKEN__ "+Token);

        tokens = Token;
        Api api = retrofit.create(Api.class);
        Call<UserDetails> call = api.getUserDetails(Token);
        Call<List<ReservationList>> call2 = api.getReservationList(Token);

        call2.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {

                if(response.isSuccessful()){
                    int value = response.body().size();
                    String date= "";
                    Log.e(TAG, "SIZE___ "+ value);

                    String transNumber ="";

                        for (int t = 0; t < value; t++) {
                            date = response.body().get(t).getDeliveryDates().get(0).getDeliveryAt().substring(0, 10);
                            SimpleDateFormat formatApiDate = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date apiDate = formatApiDate.parse(date);
                                Date current = currentDate.parse(dateString);
                                if (current.before(apiDate)) {
                                    upcomingList.addAll(Collections.singleton(response.body().get(t)));
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (dateString.equalsIgnoreCase(date)) {
                                transactionList.addAll(Collections.singleton(response.body().get(t)));

                            }
                            progressDialogdialog.dismiss();
                        }
                    if(transactionList.size() == 0){
                        warning.setVisibility(View.VISIBLE);
                        warning.setText("You don't have transactions for today");
                    }

                    int sizeTrans = transactionList.size();
                    int sizeUpcoming = upcomingList.size();
                    today.setText(Integer.toString(sizeTrans));
                    upcoming.setText(Integer.toString(sizeUpcoming));
                    generateEmployeeList();
                    }
                    else {
                }


            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                Log.e(TAG, "onFailure "+t.getMessage());
                errorMessage(t.getMessage());
            }
        });

        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if(response.isSuccessful()){
                    String value = response.body().getEntity().getImage();
                    Log.e(TAG, " value "+value);
                    if(value == null){

                        String full_name = response.body().getEntity().getFirstName() +" "+ response.body().getEntity().getLastName();
                        String mobile = response.body().getEntity().getMobileNumber();
                        profile_image = "https://d2gg9evh47fn9z.cloudfront.net/800px_COLOURBOX8345221.jpg";
                        loadImages(profile_image);
                        loadDetails(full_name, mobile);
                        getID(response.body().getEntity().getId());

                    }else{
                        String image_url = "https://xlog-dev.s3.amazonaws.com/";
                        String image_urlQA = "https://xlog-qa.s3.amazonaws.com/";
                        getID(response.body().getEntity().getId());
                        String full_name = response.body().getEntity().getFirstName() +" "+ response.body().getEntity().getLastName();
                        String mobile = response.body().getEntity().getMobileNumber();
                        profile_image = image_urlQA+value;
                        loadImages(profile_image);
                        loadDetails(full_name, mobile);

                    }

                }else{

                }

            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Log.e(TAG, "Response Failure "+ t.getMessage());
                errorMessage(t.getMessage());
            }
        });



    }

    public void errorMessage(String message){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
        alertBuilder.setTitle("Try Again");
        alertBuilder.setMessage(message);
        String positiveText = "Retry";
        alertBuilder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAccesToken();
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    public void erroMessageToday(String message){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
        alertBuilder.setTitle("Try Again");
        alertBuilder.setMessage(message);
        String positiveText = "Retry";
        alertBuilder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        todayList();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }
    public void errorUpcoming(String message){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
        alertBuilder.setTitle("Try Again");
        alertBuilder.setMessage(message);
        String positiveText = "Retry";
        alertBuilder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                     upcomingList();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }
    public void loadApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URLQA)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
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
        username.setText(fullname);
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
            Intent intent = new Intent(this, PendingTransactions.class);
            startActivity(intent);
        }  else if (id == R.id.nav_all) {
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
            getAccesToken();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(NavigationDrawer.this);
            alertBuilder.setTitle("You're Offline");
            alertBuilder.setMessage("Please Check your network");
            String positiveText = "Retry";
            alertBuilder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                      internetChecking();
                        }
                    });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }


    private void generateEmployeeList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DashboadAdapter(transactionList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    private void generateUpcomingList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DashboadAdapter(upcomingList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    public void getAccesToken(){
        progressDialogdialog = new ProgressDialog(NavigationDrawer.this);
        progressDialogdialog.setMessage("Loading");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class,"Token").addMigrations(MIGRATION_1_2)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value ="";
                for(int a = 0; a < db.rmDao().getToken().size(); a++){
                    Log.e("LOG___", "fetch_____ "+a +" "+ db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();

                }

            loadUserDetails(value);

            }
        });

    }

    public void getID(final int id){
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class,"Token").addMigrations(MIGRATION_1_2)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {



               db.rmDao().updateId(id, 1);

            }
        });
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Coordinates` (`id` INTEGER, "
                    + "`latLang` TEXT, PRIMARY KEY(`id`))");
        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        internetChecking();
    }
}
