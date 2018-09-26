package com.xlog.xloguser.finaldriverapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.xlog.xloguser.finaldriverapp.Api.Api;
import com.xlog.xloguser.finaldriverapp.BottomSheetDialog.BottomSheetDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xlog.xloguser.finaldriverapp.Model.ModelReservationList.ReservationList;
import com.xlog.xloguser.finaldriverapp.Model.UserDetails;

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
 * Created by Jaymon Rivera on 09/14/2018.
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class Map_fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMaps;
    SupportMapFragment mMapFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageButton compassBtn;
    private ImageButton sendBtn;
    private ImageButton mapTypeBtn;
    private Button currentTransBtn;
    private Button startTransactionBtn;
    private boolean isTransaction = false;
    View viewSnackBar;
    private Retrofit retrofit;
    private static final String TAG = "map_fragment";
    String tkn;

    public Map_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_fragment, container, false);

        compassBtn = (ImageButton) v.findViewById(R.id.compassBtn);
        mapTypeBtn = (ImageButton) v.findViewById(R.id.mapTypeBtn);
        currentTransBtn = (Button) v.findViewById(R.id.currentTransactionBtn);
        sendBtn = (ImageButton) v.findViewById(R.id.sendBtn);
        startTransactionBtn = (Button) v.findViewById(R.id.startTripBtn);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        compassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getFragmentManager(), "Navigation Dialog");
                bottomSheetDialog.getid(1);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getFragmentManager(), "Contact Dialog");
                bottomSheetDialog.getid(2);
            }
        });

        mapTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.getActivity();
                bottomSheetDialog.show(getFragmentManager(), "Map Dialog");
                bottomSheetDialog.getid(3);
                Log.e(TAG, "MAPS___ "+mMaps );
//                bottomSheetDialog.getMapParameter(mMaps);

            }
        });

        currentTransBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Commodity.class);
                startActivity(intent);
            }
        });
        startTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTransactionBtn.setText("Return to transaction");
                Intent intent = new Intent(getActivity(), MainMap.class);
                startActivity(intent);

            }
        });

        return v;


    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMaps = googleMap;

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
            Log.e(TAG,"POLY___ "+ poly);


        }

        return poly;
    }





}
