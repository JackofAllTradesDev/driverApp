package com.example.xloguser.finaldriverapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
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

import com.example.xloguser.finaldriverapp.BottomSheetDialog.BottomSheetDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map_fragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    SupportMapFragment mMapFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageButton compassBtn;
    private ImageButton sendBtn;
    private ImageButton mapTypeBtn;
    private Button currentTransBtn;
    private int type;
    GoogleMap mapa;
//    private ImageButton satBtn;


    public Map_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_map_fragment, container, false);
        View v = inflater.inflate(R.layout.fragment_map_fragment, container, false);

        compassBtn = (ImageButton) v.findViewById(R.id.compassBtn);
        mapTypeBtn = (ImageButton) v.findViewById(R.id.mapTypeBtn);
        currentTransBtn = (Button) v.findViewById(R.id.currentTransactionBtn);
        sendBtn = (ImageButton) v.findViewById(R.id.sendBtn);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);


        compassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mBottomSheetBehavior.setPeekHeight(150);
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
                bottomSheetDialog.getMapParameter(mMap);

            }
        });

        currentTransBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Commodity.class);
                startActivity(intent);
            }
        });


        return v;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng origin = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(origin).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
    }


}
