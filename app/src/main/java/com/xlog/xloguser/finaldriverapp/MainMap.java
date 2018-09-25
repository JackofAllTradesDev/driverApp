package com.xlog.xloguser.finaldriverapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.Model.SnapToRoad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private static final String TAG = "MainMap";
    boolean zoomable = true;
    boolean didInitialZoom;
    private Retrofit retrofit;
    ArrayList<String> coordinateToSnap;
    ArrayList<LatLng> coordinatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mainMapToolbar);
        mToolbar.setTitle("TR-10000001");
        setSupportActionBar(mToolbar);
        coordinateToSnap = new ArrayList<>();
        coordinatesList = new ArrayList<>();
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        mainMapTypeBtn = (ImageButton) findViewById(R.id.mainMapTypeButton);
        signatureBtn = (Button) findViewById(R.id.signatureBtn);
        attachBtn = (ImageButton) findViewById(R.id.attachmentBtn);
        endTripBtn = (Button) findViewById(R.id.endTripBtn);
        mainCV = (CardView) findViewById(R.id.endRouteCv);
        mainSubmitBtn = (Button) findViewById(R.id.mainSubmitBtn);
        routeBtn = (ImageButton) findViewById(R.id.routeBtn);
        endTripBtn.setVisibility(View.INVISIBLE);

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
                mainCV.setVisibility(View.INVISIBLE);
            }
        });
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainMap.this, RoutesActivity.class);
//                startActivity(intent);
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        });
        mainMapTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCurrentLocationUpdates();
            }
        });
        buildApi();
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
//        locationRequest.setMaxWaitTime(1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                mLocation = location;
                zoomMapTo(mLocation);
                drawUserPositionMarker(mLocation);
                sendCoordinatesToApi(mLocation);



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
    private void snapToRoadApi(String coordinates){
        String cord = coordinates;

        Api api = retrofit.create(Api.class);
        Call<SnapToRoad> call = api.getCoordinates(cord);

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
                        Log.e(TAG, "coordinatesList___ "+ coordinatesList);

                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Convert to Double Failed : ");
                    }

                }
            }

            @Override
            public void onFailure(Call<SnapToRoad> call, Throwable t) {

            }
        });
      Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(coordinatesList));
        polyline1.setWidth(25);
        polyline1.setColor(Color.BLUE);
        polyline1.setEndCap(new RoundCap());
    }

    private void buildApi(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.snapToRoadUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
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

                    Bundle bundle = data.getExtras();
                    String status  = bundle.getString("status");

                    String image2 = (String) data.getExtras().get("status");
                    Log.e("log_tag", "Panel Saved "+image2);
                    byte[] decodedString = Base64.decode(String.valueOf(image2), Base64.DEFAULT );
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Log.e("log_tag", "decoded  "+decodedByte);
                    ImageView imageview = (ImageView) findViewById(R.id.sampleImage); //sets imageview as the bitmap
                    imageview.setImageBitmap(decodedByte);
                }
                break;
                case CAMERA_PIC_REQUEST:
            if (requestCode == CAMERA_PIC_REQUEST) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
            ImageView imageview = (ImageView) findViewById(R.id.sampleImage); //sets imageview as the bitmap
            imageview.setImageBitmap(image);
            }
            break;

        }

    }
    public void openFolder()
    {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), PICK_FROM_GALLERY);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "CONNECTED___");
        startCurrentLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}

