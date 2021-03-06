package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xlog.xloguser.finaldriverapp.Adapters.AllTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.DashboadAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.xlog.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

public class AllTrasactions extends AppCompatActivity {
    private static final String TAG = "AllTrasactions";
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Retrofit retrofit;
    List<ReservationList> transactionList;
    private AllTransactionAdapter transactionAdapter;
    private ProgressDialog progressDialogdialog;
    private TextView warn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trasactions);
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewAllTransactions);
        warn = (TextView) findViewById(R.id.warningAllTxt);
        recyclerView.setHasFixedSize(true);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.allTransactionToolbar);
        mToolbar.setTitle("All Transactions");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        transactionList = new ArrayList<>();
        Fabric.with(this, new Crashlytics());
        internetChecking();
    }
    private void loadDataAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        transactionAdapter = new AllTransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);

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
                .setDateFormat(DateFormat.LONG)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URLPROD)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public void getAccesToken(){
        progressDialogdialog = new ProgressDialog(AllTrasactions.this);
        progressDialogdialog.setMessage("Fetching Data");
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
                loadData(value);

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

    public void loadData(String Token) {
        transactionList.clear();
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getReservationList(Token);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    int value = response.body().size();
                    for(int t = 0; t < value; t++){
                        Log.e(TAG, "Response +"+response.body().get(t).getPrefixedId());
                        transactionList = response.body();

                    }
                    if(transactionList.size() == 0){
                        warn.setVisibility(View.VISIBLE);
                        warn.setText("You have no transactions yet");
                    }
                    loadDataAdapter();
                    progressDialogdialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                Log.e(TAG, "Response + "+t.getMessage());
                errorMessage();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search Transactions");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                transactionAdapter.getFilter().filter(newQuery);
                return false;
            }
        });
        return true;

    }

    @Override
    protected void onRestart() {
        internetChecking();
        super.onRestart();
    }

    public void errorMessage(){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AllTrasactions.this);
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
                        progressDialogdialog.dismiss();
                        dialog.dismiss();
                        getAccesToken();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            loadApi();
            getAccesToken();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AllTrasactions.this);
            alertBuilder.setTitle("You're Offline");
            alertBuilder.setMessage("Please Check your network");
            String positiveText = getString(android.R.string.ok);
            alertBuilder.setPositiveButton(positiveText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            internetChecking();
                        }
                    });

            AlertDialog dialog = alertBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}
