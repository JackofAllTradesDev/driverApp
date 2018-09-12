package com.example.xloguser.finaldriverapp.BottomSheetDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.xloguser.finaldriverapp.MainActivity;
import com.example.xloguser.finaldriverapp.Map_fragment;
import com.example.xloguser.finaldriverapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Map;

public class BottomSheetDialog extends BottomSheetDialogFragment{

    private Integer a;
    private ImageButton satBtn;
    private ImageButton defaultBtn;
    private GoogleMap map;
    private int type;


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);


        View nagivationDialog = View.inflate(getContext(), R.layout.bottom_nav_layout, null);
        View contactDialog = View.inflate(getContext(), R.layout.bottom_contact_layout, null);
        View mapTypeDialog = View.inflate(getContext(), R.layout.bottom_map_type_layout, null);

        Log.e("LOG", "number: "+a);
        if(a.equals(1)){
            dialog.setContentView(nagivationDialog);
        }
        if(a.equals(2)){
            dialog.setContentView(contactDialog);
        }
        if(a.equals(3)){
            dialog.setContentView(mapTypeDialog);
        }

        satBtn = (ImageButton) mapTypeDialog.findViewById(R.id.satelliteBtn);
        defaultBtn = (ImageButton) mapTypeDialog.findViewById(R.id.defaultBtn);
        satBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        defaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });



    }

    public void getMapParameter(GoogleMap googleMap){
        map = googleMap;
        Log.e("LOG", "map =  "+map);

    }
    public void getid(Integer number){
        a = number;

    }



}
