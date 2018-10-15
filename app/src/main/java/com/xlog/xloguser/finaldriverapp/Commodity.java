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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        Bundle extras = getIntent().getExtras();
        String transNumber = extras.getString("tr_number");
        mToolbar.setTitle(transNumber);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hsCode = (TextView)findViewById(R.id.hsCodeTxt);
        commodity = (TextView)findViewById(R.id.commodityTxt);
        cDesc = (TextView)findViewById(R.id.descTxt);
        value = (TextView)findViewById(R.id.valueOfGoods);
        special = (TextView)findViewById(R.id.specialConditionTxt);
        gross = (TextView)findViewById(R.id.totalGross);
        volume = (TextView)findViewById(R.id.totalVolume);
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
    public void getData(String token, String transactionNumber){
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getInfo(token, transactionNumber);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    String hsCode = response.body().get(0).getReservation().getCommodity().getCode();
                    String commodityName = response.body().get(0).getReservation().getCommodityDescription();
                    String commodityDesc = response.body().get(0).getReservation().getCommodity().getTranslation().getDescription();
                    String valueOfGoods = response.body().get(0).getReservation().getValueOfGoods();
                    String otherSpecialCondition = response.body().get(0).getReservation().getOtherSpecialConditions();
                    String grossWeight = response.body().get(0).getReservation().getTotalGrossWeight();
                    String volume = response.body().get(0).getReservation().getTotalVolume();
                    setNames(hsCode,commodityName,commodityDesc,valueOfGoods,otherSpecialCondition,grossWeight,volume);
                    progressDialogdialog.dismiss();
                }
                else {
                    String hsCode = response.body().get(0).getReservation().getCommodity().getCode();
                    String commodityName = response.body().get(0).getReservation().getCommodityDescription();
                    String commodityDesc = response.body().get(0).getReservation().getCommodity().getTranslation().getDescription();
                    String valueOfGoods = response.body().get(0).getReservation().getValueOfGoods();
                    String otherSpecialCondition = response.body().get(0).getReservation().getOtherSpecialConditions();
                    String grossWeight = response.body().get(0).getReservation().getTotalGrossWeight();
                    String volume = response.body().get(0).getReservation().getTotalVolume();
                    setNames(hsCode,commodityName,commodityDesc,valueOfGoods,otherSpecialCondition,grossWeight,volume);
                    progressDialogdialog.dismiss();
                }



            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

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


    private void setNames(String code, String name, String Desc, String val, String specialString, String grossString, String volumeString ){
        hsCode.setText(code);
        commodity.setText(name);
        cDesc.setText(Desc);
        value.setText(val);
        special.setText(specialString);
        gross.setText(grossString);
        volume.setText(volumeString);

    }

}
