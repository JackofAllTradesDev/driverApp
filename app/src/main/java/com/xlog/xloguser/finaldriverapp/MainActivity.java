package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.Login;
import com.xlog.xloguser.finaldriverapp.Room.Entity.TokenEntity;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

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
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainLogin";
    private TextView forgotPin;
    private Button loginBtn;
    private EditText userName;
    private EditText passWord;
    private String client_secret = "Vb9YC88pcSdLgqwGGuciKDECzx0nQqZM2BmkLe6j";
    private String client_id = "3";
    private String grant_type = "password";
    private String scope = "*";
    private Retrofit retrofit;
    private String uname;
    private String pword;
    private ProgressDialog progressDialogdialog;
    private TextView counterText;
    int counter = 3;
    View viewSnackBar;
    String access_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewSnackBar = findViewById(R.id.drawer_layout);
        forgotPin = (TextView)findViewById(R.id.forgotPinTxtView);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passwordTxt);
        counterText = (TextView) findViewById(R.id.counterTxt);
         uname = userName.getText().toString();
         pword = passWord.getText().toString();



        runApi();

        forgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPin.class);
                startActivity(intent);
//
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                internetChecking();
                }
        });

    }
    public void runApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void login(String userName, String passWord){

        Api api = retrofit.create(Api.class);
        Call<Login> call = api.getToken(client_secret, client_id, userName, passWord, grant_type,scope);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if(response.isSuccessful()){
                    String access_token = response.body().getAccessToken();
                    Log.e(TAG, "token___"+access_token);
                    Intent intent = new Intent(MainActivity.this, NavigationDrawer.class);
                    saveTokenToDb(access_token);
                    startActivity(intent);
                    finish();
                    progressDialogdialog.dismiss();
                }
                else{

                    internetChecking();
                }


            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialogdialog.dismiss();
                Log.e(TAG, "Response Failure "+ t.getMessage());
                errorMessage();

            }
        });

    }

    public void succes(){
        progressDialogdialog = new ProgressDialog(MainActivity.this);
        progressDialogdialog.setMessage("Connecting");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);
        login(userName.getText().toString(), passWord.getText().toString());
    }
    public void inputCredentials(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Input Credentials");
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
    public void errorMessage(){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Timeout");
        alertBuilder.setMessage("Please Try Again");
        String positiveText = getString(android.R.string.ok);
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

    public void submitForm(){

        if(userName.getText().toString().isEmpty() && passWord.getText().toString().isEmpty()){
            inputCredentials();
        }
        else{
            succes();
        }

    }

    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            submitForm();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
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
    public void saveTokenToDb(final String token){
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class,"Token").addMigrations(MIGRATION_1_2)
                .build();

        int a = 1;
        final TokenEntity todoListItem= new TokenEntity(a,token);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
              Log.e(TAG, "COUNT____ "+ db.rmDao().countCountries());
              int count = db.rmDao().countCountries();
              if(count == 0){
                  db.rmDao().addToken(todoListItem);
              }else
              {
                  db.rmDao().update(token, 1);
              }
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
}
