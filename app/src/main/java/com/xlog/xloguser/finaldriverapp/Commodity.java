package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xlog.xloguser.finaldriverapp.Adapters.RoutesAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.Route;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class Commodity extends AppCompatActivity {
    private Retrofit retrofit;
    private TextView hsCode;
    private TextView commodity;
    private TextView cDesc;
    private TextView value;
    private TextView special;
    private TextView gross;
    private TextView volume;
    private ProgressDialog progressDialogdialog;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Route> routes;
    RoutesAdapter routesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        Bundle extras = getIntent().getExtras();
        String transNumber = extras.getString("tr_number");
        mToolbar.setTitle(transNumber);
        setSupportActionBar(mToolbar);
        commodity = (TextView)findViewById(R.id.commodityTxt);
        gross = (TextView)findViewById(R.id.totalGross);
        volume = (TextView)findViewById(R.id.totalVolume);
        recyclerView = (RecyclerView) findViewById(R.id.routesRecyclerViewCommodity);
        routes = new ArrayList<>();
        Fabric.with(this, new Crashlytics());
        loadApi();
        internetChecking();
    }
    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            getData();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Commodity.this);
            alertBuilder.setTitle("Somethings Wrong");
            alertBuilder.setMessage("Please Check your network");
            String positiveText = "Retry";
            alertBuilder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            internetChecking();
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = alertBuilder.create();
            dialog.show();
        }
    }

    public void loadApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URLUAT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public void getData(String token, String transactionNumber){
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getInfo(token, transactionNumber);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    String grossWeight = response.body().get(0).getReservation().getTotalGrossWeight();
                    String volume = response.body().get(0).getReservation().getTotalVolume();
                    setNames(grossWeight,volume);
                    routes = response.body().get(0).getRoutes();
                    loadDataAdapter();
                    progressDialogdialog.dismiss();

                }
                else {
                     String grossWeight = response.body().get(0).getReservation().getTotalGrossWeight();
                    String volume = response.body().get(0).getReservation().getTotalVolume();
                    setNames(grossWeight,volume);
                    routes = response.body().get(0).getRoutes();
                    loadDataAdapter();
                    progressDialogdialog.dismiss();

                }



            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                errorMessage();
            }
        });
    }


    private void getData() {
        progressDialogdialog = new ProgressDialog(Commodity.this);
        progressDialogdialog.setMessage("Getting Data");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Token").addMigrations(MIGRATION_1_2)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value = "";
                Bundle extras = getIntent().getExtras();
                String transNumber = extras.getString("tr_number");
//                        db.rmDao().getAll();
                for (int a = 0; a < db.rmDao().getToken().size(); a++) {
                    Log.e("LOG___", "fetch_____ " + a + " " + db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();

                }

                getData(value, transNumber);
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


    private void setNames( String grossString, String volumeString ){
        gross.setText(grossString);
        volume.setText(volumeString);

    }

    private void loadDataAdapter(){
        routesAdapter = new RoutesAdapter(routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routesAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        internetChecking();
    }
    public void errorMessage(){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Commodity.this);
        alertBuilder.setTitle("Try Again");
        alertBuilder.setMessage("Unable to Fetch Data\nPlease wait for a few minutes.");
        String positiveText = "Retry";
        String negativeText = "Ok";
        alertBuilder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialogdialog.dismiss();
                dialog.dismiss();
            }
        });
        alertBuilder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialogdialog.dismiss();
                        getData();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
