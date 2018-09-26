package com.xlog.xloguser.finaldriverapp;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.BottomSheetDialog.BottomSheetDialog;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;
import com.xlog.xloguser.finaldriverapp.Room.RmDatabase;

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
    private Button startTransactionBtn;
    private Retrofit retrofit;
    ArrayList<LatLng> list = new ArrayList<LatLng>();
    private ProgressDialog progressDialogdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasaction_view);
        compassBtn = (ImageButton) findViewById(R.id.compassBtn);
        mapTypeBtn = (ImageButton) findViewById(R.id.mapTypeBtn);
        currentTransBtn = (Button) findViewById(R.id.currentTransactionBtn);
        sendBtn = (ImageButton) findViewById(R.id.sendBtn);
        startTransactionBtn = (Button) findViewById(R.id.startTripBtn);
        currentTransBtn = (Button) findViewById(R.id.currentTransactionBtn);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadApi();
        list = new ArrayList<LatLng>();

        getData();

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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getSupportFragmentManager(), "Map Dialog");
                bottomSheetDialog.getid(3);
                bottomSheetDialog.getMapParameter(mMap);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
                Log.e(TAG, "RESPONSE_________ " + response.body().get(0).getWaypoints().get(0));

               decodePoly(response.body().get(0).getWaypoints().get(0));

            LatLng origing = new LatLng(14.36237, 121.03493000000002);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origing, 12));
            }

            @Override
            public void onFailure(Call<List<ReservationList>> call, Throwable t) {

            }
        });


    }

    private void getData() {


        final RmDatabase db = Room.databaseBuilder(getApplicationContext(), RmDatabase.class, "Token")
                .build();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String value = "";
                Bundle extras = getIntent().getExtras();
                String transNumber = extras.getString("tr_number");
                currentTransBtn.setText(transNumber);
//                        db.rmDao().getAll();
                for (int a = 0; a < db.rmDao().getToken().size(); a++) {
                    Log.e("LOG___", "fetch_____ " + a + " " + db.rmDao().getToken().get(a).getAccess_token());
                    value = db.rmDao().getToken().get(a).getAccess_token();

                }

                loadWaypoints(transNumber, value);
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
//            Log.e("poly","POLY___ "+ poly);
            Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(poly));
            polyline1.setWidth(25);
            polyline1.setColor(Color.BLACK);
            polyline1.setEndCap(new RoundCap());
        }

        return poly;
    }

}



