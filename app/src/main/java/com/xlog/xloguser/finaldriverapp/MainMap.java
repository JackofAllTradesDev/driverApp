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
import android.content.res.Resources;
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
import com.google.android.gms.maps.model.MapStyleOptions;
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
    private ImageButton mainMapTypeBtn;
    private ImageButton routeBtn;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;
    private BitmapDescriptor userPositionMarkerBitmapDescriptor;
    private BitmapDescriptor locationPostionBitmapDescriptor;
    private Marker userPositionMarker;
    public static final int REQUEST_LOCATION_PERMISSION = 100;
    private static final String TAG = "MainMap";
    boolean zoomable = true;
    boolean didInitialZoom;
    private Retrofit retrofit;
    ArrayList<String> coordinateToSnap;
    ArrayList<LatLng> saveCoordinates;
    ArrayList<LatLng> coordinatesList;
    ArrayList<ArrayList<LatLng>> saved;
    private ProgressDialog progressDialogdialog;
    public static  String transNumberPass;
    int driverID;
    ArrayList<String> storage;
    ArrayList<String> retrieve;
    BroadcastReceiver broadcastReceiver;
    BackgroundReceiver backgroundReceiver;
    EditText received, contact;
    InputStream is;
    String rName;
    String conttact, access_token;
    int[] mImgArray = { R.drawable.a_green, R.drawable.b_green,
            R.drawable.c_green, R.drawable.d_green, R.drawable.e_green};

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
        mainMapTypeBtn = (ImageButton) findViewById(R.id.mainMapTypeButton);
        routeBtn = (ImageButton) findViewById(R.id.routeBtn);
        received = (EditText) findViewById(R.id.receivedTxt);
        contact = (EditText) findViewById(R.id.contactTxt);
        storage = new ArrayList<>();
        retrieve = new ArrayList<>();
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
                .baseUrl(Api.URLQA)

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
            userPositionMarkerBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_small);
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
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
                    access_token = db.rmDao().getToken().get(a).getAccess_token();
                    driverID = db.rmDao().getToken().get(a).getDriverID();

                }

                loadWaypoints(access_token, transNumberPass);
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
                        locationPostionBitmapDescriptor = BitmapDescriptorFactory.fromResource(mImgArray[v]);
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(name).icon(locationPostionBitmapDescriptor));
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

