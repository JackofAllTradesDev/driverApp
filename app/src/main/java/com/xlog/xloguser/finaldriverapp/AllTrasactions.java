package com.xlog.xloguser.finaldriverapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.xlog.xloguser.finaldriverapp.Adapters.AllTransactionAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.DashboadAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Data.TrasactionListSet;
import com.xlog.xloguser.finaldriverapp.Model.AllTransactionModel;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.util.ArrayList;
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

public class AllTrasactions extends AppCompatActivity {
    private static final String TAG = "AllTrasactions";
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Retrofit retrofit;
    List<ReservationList> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trasactions);
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewAllTransactions);
        recyclerView.setHasFixedSize(true);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.allTransactionToolbar);
        mToolbar.setTitle("All Transactions");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        transactionList = new ArrayList<>();
        Fabric.with(this, new Crashlytics());
        loadApi();
        getAccesToken();
    }
    private void loadDataAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new AllTransactionAdapter(transactionList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
    public void loadApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.transactionNumber)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getAccesToken(){
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

        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getReservationList(Token);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                int value = response.body().size();
                for(int t = 0; t < value; t++){
                    Log.e(TAG, "Response +"+response.body().get(t).getPrefixedId());
                    transactionList = response.body();

                }
                loadDataAdapter();
            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });

    }
}
