package com.xlog.xloguser.finaldriverapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RoutesActivity extends AppCompatActivity {
    private static final String TAG = "RoutesActivity";
    private Retrofit retrofit;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Route> routes;
    RoutesAdapter routesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.routesToolbar);
        mToolbar.setTitle("Routes");
        setSupportActionBar(mToolbar);
        Fabric.with(this, new Crashlytics());
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.routeRecyclerView);
        routes = new ArrayList<>();
        loadApi();
        getAccesToken();
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

    private void loadDataAdapter(){
        routesAdapter = new RoutesAdapter(routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(routesAdapter);

    }

    public void getAccesToken(){
        Bundle extras = getIntent().getExtras();
        final String transNumberPass = extras.getString("tr_number");
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
                loadData(value, transNumberPass);

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
    public void loadData(String Token, String transactionNum) {

        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getInfo(Token, transactionNum);
        Log.e(TAG, "Response" + transactionNum );
        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                int value = response.body().get(0).getRoutes().size();
                Log.e(TAG, "value" + value );

                    routes = response.body().get(0).getRoutes();
                    loadDataAdapter();

            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });

    }

}
