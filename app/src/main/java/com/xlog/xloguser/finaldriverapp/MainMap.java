package com.xlog.xloguser.finaldriverapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.SendBase;
import com.xlog.xloguser.finaldriverapp.Model.SnapToRoad;
import com.xlog.xloguser.finaldriverapp.Room.Entity.Coordinates;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
 * Created by Jaymon Rivera on 09/19/2018.
 */

public class MainMap extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    SupportMapFragment mMapFragmentMain;
    private ImageButton cameraBtn;
    private Button signatureBtn;
    private Button endTripBtn;
    private Button mainSubmitBtn;
    private ImageButton mainMapTypeBtn;
    private ImageButton attachBtn;
    private ImageButton routeBtn;
    private TextView filename;
    private CardView mainCV;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;
    private BitmapDescriptor userPositionMarkerBitmapDescriptor;
    private Marker userPositionMarker;
    final int CAMERA_PIC_REQUEST = 1337;
    public static final int SIGNATURE_ACTIVITY = 10;
    private static final int PICK_FROM_GALLERY = 101;
    public static final int REQUEST_LOCATION_PERMISSION = 100;
    private static final int PICKFILE_RESULT_CODE = 1;
    private static final String TAG = "MainMap";
    boolean zoomable = true;
    boolean didInitialZoom;
    private Retrofit retrofit;
    ArrayList<String> coordinateToSnap;
    ArrayList<LatLng> saveCoordinates;
    ArrayList<LatLng> coordinatesList;
    ArrayList<ArrayList<LatLng>> saved;
    ArrayList<SendBase> sendBases;
    private ProgressDialog progressDialogdialog;
    public static  String transNumberPass;
    int driverID;
    ArrayList<String> storage;
    ArrayList<String> retrieve;
    BroadcastReceiver broadcastReceiver;
    BackgroundReceiver backgroundReceiver;
    String IMAGE_FILENAME = "img";
    private Uri imageToUploadUri;
    String imgString;
    String image2, ext;
    EditText received, contact;
    InputStream is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mainMapToolbar);
        Bundle extras = getIntent().getExtras();
        transNumberPass = extras.getString("transNumber");
        mToolbar.setTitle(transNumberPass);
        setSupportActionBar(mToolbar);
        coordinateToSnap = new ArrayList<>();
        coordinatesList = new ArrayList<>();
        saveCoordinates = new ArrayList<>();
        saved = new ArrayList<ArrayList<LatLng>>();
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        mainMapTypeBtn = (ImageButton) findViewById(R.id.mainMapTypeButton);
        signatureBtn = (Button) findViewById(R.id.signatureBtn);
        attachBtn = (ImageButton) findViewById(R.id.attachmentBtn);
        endTripBtn = (Button) findViewById(R.id.endTripBtn);
        mainCV = (CardView) findViewById(R.id.endRouteCv);
        mainSubmitBtn = (Button) findViewById(R.id.mainSubmitBtn);
        routeBtn = (ImageButton) findViewById(R.id.routeBtn);
        received = (EditText) findViewById(R.id.receivedTxt);
        contact = (EditText) findViewById(R.id.contactTxt);
        filename = (TextView) findViewById(R.id.fileNameTxt);
        storage = new ArrayList<>();
        retrieve = new ArrayList<>();
        sendBases = new ArrayList<>();
        Fabric.with(this, new Crashlytics());


         backgroundReceiver = new BackgroundReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BackgroundService.MY_ACTION);

        registerReceiver(backgroundReceiver, intentFilter);

        Intent intent = new Intent(MainMap.this,
               BackgroundService.class);
        startService(intent);

        mMapFragmentMain = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mainMap);
        mMapFragmentMain.getMapAsync(this);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        });

        signatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMap.this, SignatureActivity.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);
            }
        });
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();

            }
        });
        endTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainCV.setVisibility(View.VISIBLE);
            }
        });
        mainSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationCheck();

            }
        });
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMap.this, RoutesActivity.class);
                intent.putExtra("tr_number", transNumberPass);
                startActivity(intent);
//                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        });
        mainMapTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buildApi();
        getData();
        getloc();

    }

    private void getloc(){
         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                Location loc = (Location)bundle.get("phone_num");
                internetChecking(loc);
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("mycustombroadcast"));
    }




    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainMap.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                return;
            }
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
//                mLocation = location;



            }
        }
    };


    public void sendCoordinatesToApi(Location location){
        Location newLocation = location;
        String concatenatedString = null;
        double lat = newLocation.getLatitude();
        double lng = newLocation.getLongitude();
        String concatCoordinate = Double.toString(lat) + "," + Double.toString(lng);
        StringBuilder sb = new StringBuilder();
        coordinateToSnap.add(concatCoordinate);
        if(coordinateToSnap. size() > 5){
            Log.e(TAG, "Wait for 10 coordinates");
            coordinateToSnap.clear();
        }else {
            for(String val : coordinateToSnap){
                sb.append(val + "|");
                concatenatedString = sb.toString();
                Log.e(TAG, "snap count " + coordinateToSnap.size());
            }
        }
        if(coordinateToSnap.size() == 5){
            snapToRoadApi(concatenatedString.substring(0, concatenatedString.length() - 1));
        }



    }
    public void sendCoordinatesToLocal(Location location){
        Location newLocation = location;
        String concatenatedString = null;
        double lat = newLocation.getLatitude();
        double lng = newLocation.getLongitude();
        String concatCoordinate = Double.toString(lat) + "," + Double.toString(lng);
        StringBuilder sb = new StringBuilder();
        coordinateToSnap.add(concatCoordinate);
        if(coordinateToSnap. size() > 5){
            Log.e(TAG, "Wait for 10 coordinates");
            coordinateToSnap.clear();
        }else {
            for(String val : coordinateToSnap){
                sb.append(val + "|");
                concatenatedString = sb.toString();
                Log.e(TAG, "snap count " + coordinateToSnap.size());
            }
        }
        if(coordinateToSnap.size() == 5){
            storage.add(concatenatedString.substring(0, concatenatedString.length() - 1));
        }



    }
    private void snapToRoadApi(String coordinates){
        String cord = coordinates;

        Log.e(TAG, "num+ "+transNumberPass);
        Log.e(TAG, "cord+ "+cord);

        Log.e(TAG, "driverID "+driverID);

        Api api = retrofit.create(Api.class);
        Call<SnapToRoad> call = api.getCoordinates(cord, driverID, transNumberPass);

        call.enqueue(new Callback<SnapToRoad>() {
            @Override
            public void onResponse(Call<SnapToRoad> call, Response<SnapToRoad> response) {
                int value = response.body().getSnappedPoints().size();

                double latLocation = 0;
                double lngLocation = 0;
                for (int i = 0; i < value; i++) {
                    String lat = response.body().getSnappedPoints().get(i).getLocation().getLatitude().toString();
                    String lng = response.body().getSnappedPoints().get(i).getLocation().getLongitude().toString();
                    try {
                        latLocation = Double.parseDouble(lat);
                        lngLocation = Double.parseDouble(lng);
                        LatLng latLng = new LatLng(latLocation, lngLocation);
                        coordinatesList.add(latLng);
                        Log.e(TAG, "coordinatesList "+ coordinatesList);

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Convert to Double Failed : " );
                    }

                }

            }

            @Override
            public void onFailure(Call<SnapToRoad> call, Throwable t) {
                Log.e(TAG, "Failed : " +t.getMessage());
            }
        });

        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(coordinatesList));
        polyline1.setWidth(15);
        polyline1.setColor(Color.BLACK);
        polyline1.setEndCap(new RoundCap());
        Log.e(TAG, "plotted" );
    }
    private void snapToRoadLocalApi(String coordinates){
        String cord = coordinates;

        Log.e(TAG, "num+ "+transNumberPass);
        Log.e(TAG, "cord+ "+cord);

        Log.e(TAG, "driverID "+driverID);

        Api api = retrofit.create(Api.class);
        Call<SnapToRoad> call = api.getCoordinates(cord, driverID, transNumberPass);

        call.enqueue(new Callback<SnapToRoad>() {
            @Override
            public void onResponse(Call<SnapToRoad> call, Response<SnapToRoad> response) {
                int value = response.body().getSnappedPoints().size();

                double latLocation = 0;
                double lngLocation = 0;
                for (int i = 0; i < value; i++) {
                    String lat = response.body().getSnappedPoints().get(i).getLocation().getLatitude().toString();
                    String lng = response.body().getSnappedPoints().get(i).getLocation().getLongitude().toString();
                    try {
                        latLocation = Double.parseDouble(lat);
                        lngLocation = Double.parseDouble(lng);
                        LatLng latLng = new LatLng(latLocation, lngLocation);
                        saveCoordinates.add(latLng);
                        Log.e(TAG, "saveCoordinates "+ saveCoordinates);

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Convert to Double Failed : " );
                    }

                }
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .addAll(saveCoordinates));
                polyline1.setWidth(15);
                polyline1.setColor(Color.BLACK);
                polyline1.setEndCap(new RoundCap());
                Log.e(TAG, "plotted" );

            }

            @Override
            public void onFailure(Call<SnapToRoad> call, Throwable t) {
                Log.e(TAG, "Failed : " +t.getMessage());
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.broadcastReceiver !=null){
            unregisterReceiver(broadcastReceiver);
        }

    }

    private void validationCheck(){
        if(contact.getText().toString().isEmpty() && received.getText().toString().isEmpty()){
            Toast.makeText(this, "Please input empty fields",
                    Toast.LENGTH_SHORT).show();
        }
        else{


            new Get_User_Data().execute();

        }
    }


    private void buildApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.snapToRoadUrl)

                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    private void zoomMapTo(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.didInitialZoom == false) {
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
                this.didInitialZoom = true;
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        if (zoomable) {
            try {
                zoomable = false;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                zoomable = true;
                            }

                            @Override
                            public void onCancel() {
                                zoomable = true;
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void drawUserPositionMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (this.userPositionMarkerBitmapDescriptor == null) {
            userPositionMarkerBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.user_truck);
        }

        if (userPositionMarker == null) {
            userPositionMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(this.userPositionMarkerBitmapDescriptor));
        } else {
            userPositionMarker.setPosition(latLng);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch(requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {

                     image2 = (String) data.getExtras().get("byte");
                    Log.e("log_tag", "Panel Saved " + image2);
                    byte[] decodedString = Base64.decode(String.valueOf(image2), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Log.e("log_tag", "decoded  " + decodedByte);
                    ImageView imageview = (ImageView) findViewById(R.id.signatureImg);
                    imageview.setImageBitmap(decodedByte);
                    signature();

                }
                break;
            case CAMERA_PIC_REQUEST:
                if (requestCode == CAMERA_PIC_REQUEST) {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");


                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                        byte[] profileImage = outputStream.toByteArray();

                         imgString = Base64.encodeToString(profileImage,
                                Base64.NO_WRAP);
                        ImageView imageview = (ImageView) findViewById(R.id.pictureImg);
                        imageview.setImageBitmap(imageBitmap);
                        camera();

                    }
                }
                break;
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String rName = received.getText().toString();
                    String conttact = contact.getText().toString();
                        File fileS = new File(data.getData().getPath());
                        Uri selectedFileURI = data.getData();
                        filename.setText(fileS.getName());
                        String fileExt = MimeTypeMap.getFileExtensionFromUrl(selectedFileURI.toString());
                        try {
                            is = getContentResolver().openInputStream(selectedFileURI);
                            Log.e(TAG, "Fpath. " + is);
                            Log.e(TAG, "fileExt. " + "." + fileExt);
                            Log.e(TAG, "Names  " + "." + rName+" "+conttact);
                            readBytes(is,".pdf",driverID,rName,conttact);


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }
        }
    }

    public class Get_User_Data extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(
                MainMap.this);

        protected void onPreExecute() {
            this.dialog.setMessage("Submitting...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            Gson gson = new GsonBuilder()
                .setLenient()
                .create();

                 Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.transactionNumber)
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
                Log.e(TAG, "error "+ t.getMessage());
            }
        });

            return null;
        }

        protected void onPostExecute(Void result) {

            // Here if you wish to do future process for ex. move to another activity do here

            if (dialog.isShowing()) {
                dialog.dismiss();
                mainCV.setVisibility(View.INVISIBLE);
                endTripBtn.setVisibility(View.INVISIBLE);
                Toast.makeText(MainMap.this, "SENT",
                        Toast.LENGTH_SHORT).show();
                finish();


            }

        }
    }
    public byte[] readBytes(InputStream inputStream, String ext, int drive, String name, String contact) throws IOException {
        byte[] data = IOUtils.toByteArray(inputStream);

        String encoded = Base64.encodeToString( data, Base64.DEFAULT );
        byte[] myByteArray = Base64.decode( encoded, Base64.DEFAULT );
        String base64 = Base64.encodeToString(myByteArray, Base64.DEFAULT);

        SendBase sendBase = new SendBase();
        sendBase.setExt(ext);
        sendBase.setDriverID(drive);
        sendBase.setName(name);
        sendBase.setContact(contact);
        sendBase.setEncodedfile(base64);

        sendBases.add(sendBase);
    return data;
    }

    private void signature(){
        Api api = retrofit.create(Api.class);

        SendBase sendBase = new SendBase();
        sendBase.setExt(".png");
        sendBase.setDriverID(driverID);
        sendBase.setName(received.getText().toString());
        sendBase.setContact(contact.getText().toString());
        sendBase.setEncodedfile(image2);

        sendBases.add(sendBase);

    }

    private void camera(){
        Api api = retrofit.create(Api.class);

        SendBase sendBase = new SendBase();
        sendBase.setExt(".png");
        sendBase.setDriverID(driverID);
        sendBase.setName(received.getText().toString());
        sendBase.setContact(contact.getText().toString());
        sendBase.setEncodedfile(imgString);

        sendBases.add(sendBase);
    }
    public void openFolder()
    {

//        "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        String[] mimeTypes =
                {"application/pdf"};

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
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "CONNECTED___");
//        startCurrentLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    private void getData() {
        progressDialogdialog = new ProgressDialog(MainMap.this);
        progressDialogdialog.setMessage("Loading");
        progressDialogdialog.show();
        progressDialogdialog.setCancelable(false);
        progressDialogdialog.setCanceledOnTouchOutside(false);

        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Token")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value = "";

                for (int a = 0; a < db.rmDao().getToken().size(); a++) {
                    Log.e("LOG___", "fetch_____ " + a + " " + db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();
                    driverID = db.rmDao().getToken().get(a).getDriverID();

                }

                loadWaypoints(value, transNumberPass);
            }
        });



    }
    private void loadWaypoints(String tok, String transNumber){
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getInfo(tok, transNumber);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    LatLng latLng = null;
                    String num ="";
                    int val = response.body().get(0).getRoutes().size();
                    for(int v = 0; v < val; v++){
                        Double lat = response.body().get(0).getRoutes().get(v).getGeometry().getLocation().getLat();
                        Double lang = response.body().get(0).getRoutes().get(v).getGeometry().getLocation().getLng();
                        String name = response.body().get(0).getRoutes().get(v).getName();
                        latLng = new LatLng(lat, lang);
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(name));
                    }
                    int waypoint = response.body().get(0).getWaypoints().size();
                    for(int b = 0; b < waypoint; b++){
                        decodePoly(response.body().get(0).getWaypoints().get(b));
                    }
                    progressDialogdialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {
                Log.e(TAG, "Response failed "+t.getMessage());
                progressDialogdialog.dismiss();
            }
        });

    }
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);

            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(poly));
            polyline1.setWidth(15);
            polyline1.setColor(Color.rgb(	57,135,252));
            polyline1.setEndCap(new RoundCap());
        }

        return poly;
    }

    public void internetChecking(Location location) {
        Location loc = location;
        Log.e(TAG, "location  ++++++++++++ " + loc);
        if (AppStatus.getInstance(getBaseContext()).isOnline()) {
            int size = storage.size();
            if(size == 0){
                zoomMapTo(loc);
                drawUserPositionMarker(loc);
                sendCoordinatesToApi(loc);
            }else{
                deleteArray();
                localToAPi();
                zoomMapTo(loc);
                drawUserPositionMarker(loc);

            }


        } else {
            sendCoordinatesToLocal(loc);
            zoomMapTo(loc);
            drawUserPositionMarker(loc);
            insertLocal();

        }
    }
    private void insertLocal(){
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Coordinates").addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration()
                .build();
        int a = 0;
        int size = storage.size();
        String cord = "";
        for(int z = 0; z < size; z++){
            cord = storage.get(z);
        }

        if(size == 0){
            Log.e(TAG, "Local ");
        }else{
            final Coordinates todoListItem= new Coordinates(null,cord);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    db.rmDao().AddCoordinates(todoListItem);
                }
            });
        }



            }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Coordinates` (`id` INTEGER, "
                    + "`latLang` TEXT, PRIMARY KEY(`id`))");
        }
    };

    private void localToAPi(){
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Coordinates").addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration()
                .build();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int size = db.rmDao().getAll().size();
                if(size < 0){
                    //do nothing

                }else{
                    for(int a = 0; a < size; a++){
                        Log.e("LOG___", "fetch_____ "+a +" "+ db.rmDao().getAll().get(a).getLatLang());
                        retrieve.add(db.rmDao().getAll().get(a).getLatLang());
                    }
                    deleteLocal();

                }

            }
        });
    }
    private void deleteLocal(){
        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Coordinates").addMigrations(MIGRATION_1_2)
                .build();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
              db.rmDao().deleteAll();

            }
        });
    }
    private void deleteArray(){
        int value = retrieve.size();
        for(int a= 0; a< value; a++){
            snapToRoadLocalApi(retrieve.get(a));
        }
        retrieve.clear();


    }



}

