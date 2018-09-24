package com.xlog.xloguser.finaldriverapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * Created by Jaymon Rivera on 09/14/2018.
 */
public class ForgotPin extends AppCompatActivity {

    private Button sumbitCpNumber;
    private Button sumbitCodeBtn;
    private Button newPinBtn;
    private EditText enterCodeTxt;
    private EditText entercp;
    private EditText newPinEditTxt;
    private ConstraintLayout cellphoneNumber;
    private ConstraintLayout enterCode;
    private ConstraintLayout enterPin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);
        sumbitCpNumber = (Button)findViewById(R.id.submitBtn);
        sumbitCodeBtn = (Button)findViewById(R.id.submitCodeBtn);
        newPinBtn = (Button)findViewById(R.id.newPInBtn);
        enterCodeTxt = (EditText)findViewById(R.id.enterCodeEditTxt);
        entercp = (EditText)findViewById(R.id.phoneNumberEditTxt);
        newPinEditTxt = (EditText)findViewById(R.id.newPinEditText);
        cellphoneNumber = (ConstraintLayout)findViewById(R.id.enterNumber);
        enterCode = (ConstraintLayout)findViewById(R.id.enterCode);
        enterPin = (ConstraintLayout)findViewById(R.id.enterPIn);

        sumbitCpNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entercp.length() < 10) {
                    Toast.makeText(ForgotPin.this, "Please enter valid phone number",
                            Toast.LENGTH_SHORT).show();
                    Log.e("sad", "msg" + entercp.length());

                } else {
                    cellphoneNumber.setVisibility(View.INVISIBLE);
                    enterCode.setVisibility(View.VISIBLE);
                }
            }
        });


        sumbitCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterCodeTxt.length() < 6){
                    Toast.makeText(ForgotPin.this, "Please enter the code given",
                            Toast.LENGTH_SHORT).show();
                }else{
                    cellphoneNumber.setVisibility(View.INVISIBLE);
                    enterCode.setVisibility(View.INVISIBLE);
                    enterPin.setVisibility(View.VISIBLE);

                }

            }
        });

        newPinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPinEditTxt.length() < 6){
                    Toast.makeText(ForgotPin.this, "Please enter new pin",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(ForgotPin.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}
