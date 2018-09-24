package com.xlog.xloguser.finaldriverapp.BottomSheetDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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

import com.xlog.xloguser.finaldriverapp.MainActivity;
import com.xlog.xloguser.finaldriverapp.Map_fragment;
import com.xlog.xloguser.finaldriverapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Map;
/**
 * Created by Jaymon Rivera on 09/10/2018.
 */
public class BottomSheetDialog extends BottomSheetDialogFragment{

    private Integer a;
    private ImageButton satBtn;
    private ImageButton defaultBtn;
    private ImageButton gmapBtn;
    private ImageButton wazeBtn;
    private ImageButton msgBtn;
    private ImageButton callBtn;
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
        gmapBtn = (ImageButton) nagivationDialog.findViewById(R.id.googleMapBtn);
        wazeBtn = (ImageButton) nagivationDialog.findViewById(R.id.wazeBtn);
        msgBtn = (ImageButton) contactDialog.findViewById(R.id.msgBtn);
        callBtn = (ImageButton) contactDialog.findViewById(R.id.callBtn);
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

        gmapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);


            }
        });
        wazeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = "com.waze"; // getPackageName() from Context or Activity object
                try {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setPackage(appPackageName);
                    startActivity(i);
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = "+63091234567890";
                Intent intent = new Intent("android.intent.action.VIEW");

                /** sample number only waiting for it to make it dynamic*/
                intent.setData(Uri.parse("smsto:"+ num));
                startActivity(intent);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = "+639064402708";
                String uri = "tel:" + num.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
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
