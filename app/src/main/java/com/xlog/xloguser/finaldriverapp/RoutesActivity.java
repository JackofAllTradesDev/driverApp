package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.xlog.xloguser.finaldriverapp.Adapters.EndRouteAdapter;
import com.xlog.xloguser.finaldriverapp.Adapters.RoutesAdapter;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Interface.Attachment;
import com.xlog.xloguser.finaldriverapp.Model.EncodeFile;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.Route;
import com.xlog.xloguser.finaldriverapp.Model.SendBase;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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

public class RoutesActivity extends AppCompatActivity implements Attachment {
    private static final String TAG = "RoutesActivity";
    private Retrofit retrofit;
    private CardView cv;
    private static RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Route> routes;
    private ImageButton cameraBtn;
    private Button signatureBtn;
    private ImageButton attachBtn;
    private Button mainSubmitBtn;
    private Button endTrip;
    private ImageView imageview, imageviewSignature;
    final int CAMERA_PIC_REQUEST = 1337;
    public static final int SIGNATURE_ACTIVITY = 10;
    private static final int PICKFILE_RESULT_CODE = 1;
    private TextView filename;
    int driverID;
    EndRouteAdapter endRouteAdapter;
    EditText received, contact;
    int rID, truckerID;
    String placeID;
    private ProgressDialog progressDialogdialog;
    InputStream is;
    String rName, conttact, access_token, image2, imgString, token, stat;
    ArrayList<SendBase> sendBases;
    ArrayList<EncodeFile> encodeFiles;
    ArrayList<String> completeList;
    String transNumberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.routesToolbar);
        mToolbar.setTitle("Routes");
        setSupportActionBar(mToolbar);
        Fabric.with(this, new Crashlytics());
        recyclerView = (RecyclerView) findViewById(R.id.routeRecyclerView);
        signatureBtn = (Button) findViewById(R.id.signatureBtn);
        endTrip = (Button) findViewById(R.id.btnEndTrip);
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        attachBtn = (ImageButton) findViewById(R.id.attachmentBtn);
        received = (EditText) findViewById(R.id.receivedTxt);
        contact = (EditText) findViewById(R.id.contactTxt);
        cv = (CardView) findViewById(R.id.endRouteCv);
        filename = (TextView) findViewById(R.id.fileNameTxt);
        mainSubmitBtn = (Button) findViewById(R.id.mainSubmitBtn);
        imageview = (ImageView) findViewById(R.id.signatureImg);
        imageviewSignature = (ImageView) findViewById(R.id.pictureImg);
        routes = new ArrayList<>();
        sendBases = new ArrayList<>();
        encodeFiles = new ArrayList<>();
        completeList = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        transNumberPass = extras.getString("tr_number");
        loadApi();
        internetChecking();
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getText().toString().isEmpty() && received.getText().toString().isEmpty()) {
                    Toast.makeText(RoutesActivity.this, "Please input name and contact number first.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                }

            }
        });

        signatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getText().toString().isEmpty() && received.getText().toString().isEmpty()) {
                    Toast.makeText(RoutesActivity.this, "Please input name and contact number first.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RoutesActivity.this, SignatureActivity.class);
                    startActivityForResult(intent, SIGNATURE_ACTIVITY);
                }

            }
        });
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contact.getText().toString().isEmpty() && received.getText().toString().isEmpty()) {
                    Toast.makeText(RoutesActivity.this, "Please input name and contact number first.", Toast.LENGTH_SHORT).show();
                } else {
                    openFolder();
                }


            }
        });
        mainSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rName = received.getText().toString();
                conttact = contact.getText().toString();
                validationCheck();
                routes.clear();

            }
        });
        endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoutesActivity.this, TrasactionView.class);
                intent.putExtra("tr_number", transNumberPass);
                startActivity(intent);
                setStatusRoute();

            }
        });


    }

    private void validationCheck() {
        if (contact.getText().toString().isEmpty() && received.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input empty fields",
                    Toast.LENGTH_SHORT).show();
        } else {


            new RoutesActivity.Get_User_Data().execute();

        }
    }

    public void openFolder() {

//        "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        String[] mimeTypes =
                {"image/jpeg", "image/png","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/pdf" };

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
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

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.URLQA)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void loadDataAdapter() {
        endRouteAdapter = new EndRouteAdapter(routes, RoutesActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(endRouteAdapter);

    }

    public void getAccesToken() {
        progressDialogdialog = new ProgressDialog(RoutesActivity.this);
        progressDialogdialog.setMessage("Fetching Data");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);

        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Token").addMigrations(MIGRATION_1_2)
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value = "";
                for (int a = 0; a < db.rmDao().getToken().size(); a++) {
                    Log.e("LOG___", "fetch_____ " + a + " " + db.rmDao().getToken().get(a).getAccess_token());
                    token = db.rmDao().getToken().get(a).getAccess_token();
                }
                loadData(token, transNumberPass);

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
        Log.e(TAG, "Response" + transactionNum);
        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                int value = response.body().get(0).getRoutes().size();
                Log.e(TAG, "value" + value);
                driverID = response.body().get(0).getId();
                Log.e(TAG, "driverID" + driverID);
                routes = response.body().get(0).getRoutes();
                loadDataAdapter();

                rID = response.body().get(0).getTrucks().get(0).getTruckingReservationId();
                truckerID = response.body().get(0).getTrucks().get(0).getTruckerTruckId();
                int routeSize = response.body().get(0).getRoutes().size();
                for (int b = 0; b < routeSize; b++) {
                    String routeStatus = response.body().get(0).getRoutes().get(b).getRoutestatus();
                    if (routeStatus.equalsIgnoreCase("Completed")) {
                        endTrip.setVisibility(View.VISIBLE);
                    }else{
                        endTrip.setVisibility(View.INVISIBLE);
                    }
                }
                progressDialogdialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                errorMessage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    rName = received.getText().toString();
                    conttact = contact.getText().toString();
                    image2 = (String) data.getExtras().get("byte");
                    Log.e("log_tag", "Panel Saved " + image2);
                    byte[] decodedString = Base64.decode(String.valueOf(image2), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Log.e("log_tag", "decoded  " + decodedByte);
                    imageview.setImageBitmap(decodedByte);
                    signature(rName, conttact);

                }
                break;
            case CAMERA_PIC_REQUEST:
                if (requestCode == CAMERA_PIC_REQUEST) {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        rName = received.getText().toString();
                        conttact = contact.getText().toString();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                        byte[] profileImage = outputStream.toByteArray();

                        imgString = Base64.encodeToString(profileImage,
                                Base64.NO_WRAP);
                        imageview.setImageBitmap(imageBitmap);
                        camera(rName, conttact);

                    }
                }
                break;
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    File fileS = new File(data.getData().getPath());
                    Uri selectedFileURI = data.getData();
                    String uriString = selectedFileURI.toString();
                    File myFile = new File(uriString);
                    String displayName = null;

                    rName = received.getText().toString();
                    conttact = contact.getText().toString();

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(selectedFileURI, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    try {
                        String ext = displayName.substring(displayName.lastIndexOf(".") + 1);
                        is = getContentResolver().openInputStream(selectedFileURI);
                        Log.e(TAG, "Fpath. " + is);
                        Log.e(TAG, "fileExt. " + "." + ext);

                        filename.setText(displayName);
                        readBytes(is, ext, driverID, rName, conttact);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public byte[] readBytes(InputStream inputStream, String ext, int drive, String name, String contact) throws IOException {
        byte[] data = IOUtils.toByteArray(inputStream);

        String encoded = Base64.encodeToString(data, Base64.DEFAULT);
        byte[] myByteArray = Base64.decode(encoded, Base64.DEFAULT);
        String base64 = Base64.encodeToString(myByteArray, Base64.DEFAULT);

        EncodeFile encodeFile = new EncodeFile();
        encodeFile.setEncodedfile(base64);
        encodeFile.setExt(ext);
        encodeFiles.add(encodeFile);
        return data;
    }

    private void signature(String a, String b) {

        EncodeFile encodeFile = new EncodeFile();
        encodeFile.setEncodedfile(image2);
        encodeFile.setExt(".png");
        encodeFiles.add(encodeFile);


    }

    private void camera(String name, String number) {

        EncodeFile encodeFile = new EncodeFile();
        encodeFile.setEncodedfile(imgString);
        encodeFile.setExt(".png");
        encodeFiles.add(encodeFile);

    }

    public class Get_User_Data extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(
                RoutesActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage("Submitting...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SendBase sendBase = new SendBase();
            sendBase.setToken(token);
            sendBase.setDriverID(driverID);
            sendBase.setId(placeID);
            sendBase.setStatus(1);
            sendBase.setName(rName);
            sendBase.setContact(conttact);
            sendBase.setEncodeFile(encodeFiles);
            sendBases.add(sendBase);

            Log.e(TAG, "error " + rName + " " + conttact);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Api.URLQA)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            Api api = retrofit.create(Api.class);


            Call<List<SendBase>> userCall = api.sendBase64(sendBases);

            userCall.enqueue(new Callback<List<SendBase>>() {
                @Override
                public void onResponse(Call<List<SendBase>> call, Response<List<SendBase>> response) {
                    Log.e(TAG, "Success");
                }

                @Override
                public void onFailure(Call<List<SendBase>> call, Throwable t) {
                    Log.e(TAG, "error " + t.getMessage());
                    sendBases.clear();
                    encodeFiles.clear();
                    getAccesToken();
                    dialog.dismiss();
                    clear();
                }
            });

            return null;
        }

        protected void onPostExecute(Void result) {

            // Here if you wish to do future process for ex. move to another activity do here

            if (dialog.isShowing()) {
                dialog.dismiss();
                cv.setVisibility(View.INVISIBLE);
                Toast.makeText(RoutesActivity.this, "Success",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void unHide() {
        cv.setVisibility(View.VISIBLE);

    }

    @Override
    public void sendID(String id) {
        placeID = id;
        Log.e(TAG, "IDDDDDD+ " + id);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void clear(){
        contact.getText().clear();
        received.getText().clear();
        imageviewSignature.setImageBitmap(null);
        imageview.setImageBitmap(null);
        filename.setText(null);

    }

    public void errorMessage() {
        progressDialogdialog.dismiss();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoutesActivity.this);
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
                        getAccesToken();
                    }
                });

        AlertDialog dialog = alertBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void internetChecking() {
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            getAccesToken();
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RoutesActivity.this);
            alertBuilder.setTitle("You're Offline");
            alertBuilder.setMessage("Please Check your network");
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
    }

    private void setStatusRoute() {
        Api api = retrofit.create(Api.class);
        Call<JsonObject> call = api.setRoutStatus(2, rID, truckerID, transNumberPass);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG, "Successss_____ ");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }
}
