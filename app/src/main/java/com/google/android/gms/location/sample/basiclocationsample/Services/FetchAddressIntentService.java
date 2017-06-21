package com.google.android.gms.location.sample.basiclocationsample.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.sample.basiclocationsample.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by HIMANSHU on 3/16/2017.
 * A service class that runs on a different thread that is used to retrieve the address using
 * reverse geocoding.
 */

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String errorMessage="";

        mReceiver=intent.getParcelableExtra(Constants.RECEIVER);
        if(mReceiver==null){
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
       Location mLocation=intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if(mLocation==null)
        {
         errorMessage="No Location Found!";
        sendResultToReceiver(Constants.FAILURE_RESULT,errorMessage);
        }
        else {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());//Initialize the geocoder object
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            } catch (IOException e) {
                errorMessage = "service not available!";
                Log.e("APP",errorMessage);
                sendResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid location!";
                Log.e("APP",errorMessage);
                sendResultToReceiver(Constants.FAILURE_RESULT,errorMessage);
            }
            if (addresses == null || addresses.size() == 0) {
                errorMessage = "No Address received!";
                Log.e("APP",errorMessage);
                sendResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> add = new ArrayList<String>();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    add.add(address.getAddressLine(i));
                    Log.v("Address:",address.getAddressLine(i));
                }
                sendResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(",", add));
            }
        }


    }

    private void sendResultToReceiver(int statusResult, String Message) {
        Bundle bundle=new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,Message);
        mReceiver.send(statusResult,bundle);
    }
}
