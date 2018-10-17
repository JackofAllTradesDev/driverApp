package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.BottomSheetDialog.BottomSheetDialog;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
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
 * Created by Jaymon Rivera on 09/16/2018.
 */
public class TrasactionView extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "TranView";
    private GoogleMap mMap;
    SupportMapFragment mMapFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageButton compassBtn;
    private ImageButton sendBtn;
    private ImageButton mapTypeBtn;
    private Button currentTransBtn;
    private TextView destination;
    private Button startTransactionBtn;
    private Retrofit retrofit;
    ArrayList<LatLng> list = new ArrayList<LatLng>();
    private ProgressDialog progressDialogdialog;
    private BitmapDescriptor userPositionMarkerBitmapDescriptor;
    private Marker userPositionMarker;
    Context context;
    String transNumberPass, currentTrans;
    int driverId;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasaction_view);
         mToolbar = (Toolbar) findViewById(R.id.transactionViewToolbar);
        compassBtn = (ImageButton) findViewById(R.id.compassBtn);
        mapTypeBtn = (ImageButton) findViewById(R.id.mapTypeBtn);
        sendBtn = (ImageButton) findViewById(R.id.sendBtn);
        startTransactionBtn = (Button) findViewById(R.id.startTripBtn);
        currentTransBtn = (Button) findViewById(R.id.currentTransactionBtn);
        destination = (TextView) findViewById(R.id.originLocation);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadApi();
        list = new ArrayList<LatLng>();
        Fabric.with(this, new Crashlytics());


        compassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getSupportFragmentManager(), "Navigation Dialog");
                bottomSheetDialog.getid(1);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getSupportFragmentManager(), "Contact Dialog");
                bottomSheetDialog.getid(2);
            }
        });

        mapTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrasactionView.this, Commodity.class);
                intent.putExtra("tr_number",currentTrans);
                startActivity(intent);
            }
        });
        startTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrasactionView.this, MainMap.class);
                intent.putExtra("transNumber", transNumberPass);
                intent.putExtra("driverId", driverId);
                startActivity(intent);

            }
        });
        getData();

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

    private void loadWaypoints(String transNumber, String tokenString) {
        String tok = tokenString;
        String num = transNumber;
        Api api = retrofit.create(Api.class);
        Call<List<ReservationList>> call = api.getInfo(tok, num);

        call.enqueue(new Callback<List<ReservationList>>() {
            @Override
            public void onResponse(Call<List<ReservationList>> call, Response<List<ReservationList>> response) {
                if(response.isSuccessful()){
                    LatLng latLng = null;
                    String num ="";
                    int val = response.body().get(0).getRoutes().size();
                    driverId = response.body().get(0).getId();
                    for(int v = 0; v < val; v++){
                        Double lat = response.body().get(0).getRoutes().get(v).getGeometry().getLocation().getLat();
                        Double lang = response.body().get(0).getRoutes().get(v).getGeometry().getLocation().getLng();
                        String name = response.body().get(0).getRoutes().get(v).getName();
                        latLng = new LatLng(lat, lang);
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(name));
                    }
                    for(int a = 0; a < val; a++){
                        if(a == 1){
                            String name = response.body().get(0).getRoutes().get(1).getName();
                            num = response.body().get(0).getRoutes().get(1).getFormattedPhoneNumber();
                            destination.setText(name);
                        }

                    }
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                    bottomSheetDialog.getNumbers(num);
                    int waypoint = response.body().get(0).getWaypoints().size();
                    for(int b = 0; b < waypoint; b++){
                        decodePoly(response.body().get(0).getWaypoints().get(b));
                    }
                    progressDialogdialog.dismiss();
                }else{


                }


            }


            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });



    }

    private void getData() {
        progressDialogdialog = new ProgressDialog(TrasactionView.this);
        progressDialogdialog.setMessage("Loading");
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
                transNumberPass = extras.getString("tr_number");
                currentTrans =  transNumberPass;
                mToolbar.setTitle(currentTrans);
                for (int a = 0; a < db.rmDao().getToken().size(); a++) {
                    Log.e("LOG___", "fetch_____ " + a + " " + db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();

                }

                loadWaypoints(transNumberPass, value);
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
//            Log.e("poly","POLY___ "+ poly);
            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(poly));
            polyline1.setWidth(15);
            polyline1.setColor(Color.rgb(	0,191,255));
            polyline1.setEndCap(new RoundCap());
            centerIncidentRouteOnMap(poly);
        }

        return poly;
    }
    public void centerIncidentRouteOnMap(List<LatLng> copiedPoints) {
        double minLat = Integer.MAX_VALUE;
        double maxLat = Integer.MIN_VALUE;
        double minLon = Integer.MAX_VALUE;
        double maxLon = Integer.MIN_VALUE;
        for (LatLng point : copiedPoints) {
            maxLat = Math.max(point.latitude, maxLat);
            minLat = Math.min(point.latitude, minLat);
            maxLon = Math.max(point.longitude, maxLon);
            minLon = Math.min(point.longitude, minLon);
        }
        final LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(maxLat, maxLon)).include(new LatLng(minLat, minLon)).build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
    }





}



