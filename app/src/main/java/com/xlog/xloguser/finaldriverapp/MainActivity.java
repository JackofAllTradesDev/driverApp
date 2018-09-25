package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
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
                submitForm();
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
                internetChecking();
                String access_token = response.body().getAccessToken();
                Log.e(TAG, "token___"+access_token);
                Intent intent = new Intent(MainActivity.this, NavigationDrawer.class);
                intent.putExtra("access_Token", access_token);
                startActivity(intent);
                finish();
                Bundle bundle = new Bundle();
                bundle.putString("access_tokenKey", access_token);
                Map_fragment fragobj = new Map_fragment();
                fragobj.setArguments(bundle);
                progressDialogdialog.dismiss();

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.e(TAG, "Response Failure "+ t.getMessage());
                errorMessage(t.getMessage());
            }
        });

    }

    public void succes(){
        progressDialogdialog = new ProgressDialog(MainActivity.this);
        progressDialogdialog.setMessage("Connecting");
        progressDialogdialog.show();
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
    public void errorMessage(String message){
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle(message);
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

    public void submitForm(){

        if(userName.getText().toString().isEmpty() && passWord.getText().toString().isEmpty()){
            inputCredentials();
        }
        else{
            succes();
        }

    }
    public void reset(){
        Toast.makeText(MainActivity.this, "Wait for 5 seconds",
                Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                counterText.setVisibility(View.INVISIBLE);
                counter = 3;
                loginBtn.setEnabled(true);
                loginBtn.setBackgroundColor(Color.parseColor("#4180f4"));
            }
        }, 5000);
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
            Toast.makeText(MainActivity.this, "Ooops! No WiFi/Mobile Networks Connected!", Toast.LENGTH_SHORT).show();
            int duration = Snackbar.LENGTH_LONG;
            String message = " No Internet Connection";
//            Snackbar.make(viewSnackBar, message, duration).show();
        }
    }
}
