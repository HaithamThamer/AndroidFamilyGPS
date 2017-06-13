package com.familygps.familygps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class Receiver extends BroadcastReceiver {
    LocationManager locationManager;
    Location location;
    String provider;
    HDatabaseConnection db;
    SmsManager smsManager;
    Context context;
    public Receiver(){

    }
    private void headphone(Context context,Intent intent){
        switch (intent.getIntExtra("state",-1)){
            case 0:
                //unplugged
                ArrayList<Number> numbers = new HDatabaseConnection(context).getNumberInSaveMode();
                smsManager = SmsManager.getDefault();
                for (int i = 0; i < numbers.size(); i++){
                    smsManager.sendTextMessage(numbers.get(i).number,null,"HELP ME",null,null);
                }
                break;
            case 1:
                //plugged

                break;
            default:
                //unknown
                break;
        }
    }
    private void sms(Context context,Intent intent) throws InterruptedException {
        this.context = context; // get my app as variable
        Log.e("sms","111");
        Object[] pdus = (Object[]) intent.getExtras().get("pdus"); // get messages
        for (int i = 0; i < pdus.length; i++) {
            Log.e("sms","2222");
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]); //
            smsManager = SmsManager.getDefault();
            if (smsMessage.getMessageBody().contains("Location") ) {
                Log.e("sms","3333");
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                provider = LocationManager.NETWORK_PROVIDER;
                location = locationManager.getLastKnownLocation(provider); // location live
                db = new HDatabaseConnection(context); // initilize database
                if (location == null) {
                    provider = LocationManager.GPS_PROVIDER;
                    location = locationManager.getLastKnownLocation(provider); // location as offline
                    Log.e("sms","4444");
                }

                if (
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        ) {
                    Log.e("sms","5555");
                    return;
                }

                    locationManager.requestLocationUpdates(provider, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
                    Log.e("sms","A1111");
                    String num = smsMessage.getOriginatingAddress(); // get number of sender
                    Log.e("sms",num);
                    num = num.length() == 14 ? "0"+num.substring(4,14) : num; // fix +9647703867142
                    Log.e("sms",num);
                Log.e("sms","B3333");
                    if (db.isNumber(num)){
                        Log.e("sms",num);
                        //String msg = "https://www.google.iq/maps/@"+location.getLatitude()+","+location.getLongitude()+",14z";

                        String msg = "https://www.google.com/maps?q="+location.getLatitude()+","+location.getLongitude();
                        Log.e("sms",msg);

                    smsManager.sendTextMessage(num,null,msg,null,null);
                    Log.e("sms","6666");
                }
            }
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            switch (intent.getAction()){
                case "android.provider.Telephony.SMS_RECEIVED":
                    try {
                        sms(context,intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "android.intent.action.HEADSET_PLUG":
                    headphone(context,intent);
                    break;
                default:
                    break;
            }
        }
    }
}
