package com.xlog.xloguser.finaldriverapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.support.annotation.Nullable;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
/**
 * Created by Jaymon Rivera on 09/19/2018.
 */

public class MainMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    SupportMapFragment mMapFragmentMain;
    private ImageButton cameraBtn;
    private Button signatureBtn;
    private Button endTripBtn;
    private Button mainSubmitBtn;
    private ImageButton attachBtn;
    private ImageButton routeBtn;
    private CardView mainCV;
    final int CAMERA_PIC_REQUEST = 1337;
    public static final int SIGNATURE_ACTIVITY = 10;
    private static final int PICK_FROM_GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mainMapToolbar);
        mToolbar.setTitle("TR-10000001");
        setSupportActionBar(mToolbar);
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        signatureBtn = (Button) findViewById(R.id.signatureBtn);
        attachBtn = (ImageButton) findViewById(R.id.attachmentBtn);
        endTripBtn = (Button) findViewById(R.id.endTripBtn);
        mainCV = (CardView) findViewById(R.id.endRouteCv);
        mainSubmitBtn = (Button) findViewById(R.id.mainSubmitBtn);
        routeBtn = (ImageButton) findViewById(R.id.routeBtn);

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
                Intent intent = new Intent(MainMap.this, RoutesActivity.class);
                startActivity(intent);
            }
        });
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
    }

