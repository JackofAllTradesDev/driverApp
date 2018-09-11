package com.example.xloguser.finaldriverapp.BottomSheetDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class BottomSheetDialog extends BottomSheetDialogFragment{

    private Integer a;
    private ImageButton satBtn;
    SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private int type;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_map_type_layout, container, false);
        satBtn = (ImageButton) v.findViewById(R.id.satelliteBtn);
        satBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sat = 2;

            mMap.setMapType(2);



            }
        });
        return v;
    }

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



    }
    public void getid(Integer number){
        a = number;
       Log.e("LOG", "Logs: "+number);

    }


    
}
