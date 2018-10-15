package com.xlog.xloguser.finaldriverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;



public class BackgroundReceiver extends BroadcastReceiver {
    MainMap mainMap;
    final static String MY_ACTION = "MY_ACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Location loc = (Location)extras.get("coordinates");


        Intent i = new Intent("mycustombroadcast");
        i.putExtra("phone_num", loc);
        context.sendBroadcast(i);
    }
}
