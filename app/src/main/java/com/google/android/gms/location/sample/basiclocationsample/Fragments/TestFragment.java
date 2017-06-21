/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.basiclocationsample.Fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.sample.basiclocationsample.Constants;
import com.google.android.gms.location.sample.basiclocationsample.R;
import com.google.android.gms.location.sample.basiclocationsample.Services.FetchAddressIntentService;

import java.text.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;
import java.util.Locale;

public class TestFragment extends Fragment implements
        ConnectionCallbacks, OnConnectionFailedListener,LocationListener,View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.


    protected static final String TAG = "TestFragment";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */

    protected Location mCurrentLocation;
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView mLastUpdateTimeTextView;
    protected LocationRequest mLocationRequest;
    protected Button mStartUpdatesButton;
    protected Button mAddressUpdateButton;
    protected ProgressBar mProgress;
    protected TextView mAddressTextView;
    protected Button mStopUpdatesButton;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    private AddressResultReceiver mReceiver;
    protected Boolean mRequestingAddressUpdates;
    protected String mAddressOutput;
    private View rootView;


    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected())
        stopLocationUpdate();

    }

    @Override
    public void onResume() {
        
        super.onResume();
        if(mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
            startLocationUpdate();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView=inflater.inflate(R.layout.main_activity, container,false);
        mStartUpdatesButton = (Button) rootView.findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) rootView.findViewById(R.id.stop_updates_button);
        mLatitudeTextView = (TextView) rootView.findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) rootView.findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) rootView.findViewById(R.id.last_update_time_text);
        mAddressUpdateButton= (Button)rootView.findViewById(R.id.address_receive);
        mProgress =(ProgressBar) rootView.findViewById(R.id.address_pBar);
        mAddressTextView =(TextView)rootView.findViewById(R.id.address_viewer);
        mStartUpdatesButton.setOnClickListener(this);
        mStopUpdatesButton.setOnClickListener(this);
        mAddressUpdateButton.setOnClickListener(this);

        // Set labels.
        mReceiver = new AddressResultReceiver(new Handler());
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingAddressUpdates=false;
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mAddressOutput="";


        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);


        buildGoogleApiClient();
        return this.rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        //Update Values from Bundle
        if(savedInstanceState!=null){
            if(savedInstanceState.keySet().contains("RS")){
                mRequestingLocationUpdates=savedInstanceState.getBoolean("RS");
                setButtonEnabled();
            }
            if(savedInstanceState.keySet().contains("L")){
                mCurrentLocation=savedInstanceState.getParcelable("L");

            }
            if(savedInstanceState.keySet().contains("T")){
                mLastUpdateTime=savedInstanceState.getString("T");
            }
            if(savedInstanceState.keySet().contains("RA")){
                mRequestingAddressUpdates=savedInstanceState.getBoolean("RA");
            }
            if(savedInstanceState.keySet().contains("AA")){
                mAddressOutput=savedInstanceState.getString("AA");
            }
            updateUI();
            updateUIWidgets();
            displayAddressOutput();
        }

    }

    public void createLocationRequest()
    {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }
    public void startUpdatesButtonH()
    {
        if(!mRequestingLocationUpdates)
        {
            mRequestingLocationUpdates=true;
            setButtonEnabled();
            startLocationUpdate();
        }

    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    public void stopUpdatesButtonH()
    {
        if(mRequestingLocationUpdates)
        {
            mRequestingLocationUpdates=false;
            setButtonEnabled();
            stopLocationUpdate();
        }

    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    private void setButtonEnabled() {
        if(mRequestingLocationUpdates)
        {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        }
        else
        {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        if(mCurrentLocation==null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
        if(mRequestingLocationUpdates)
            startLocationUpdate();
        if(mRequestingAddressUpdates)
            startAddressUpdate();

    }

    private void updateUI() {
        mLatitudeTextView.setText(String.format(Locale.getDefault(),"%s: %f", mLatitudeLabel,
                mCurrentLocation.getLatitude()));

        mLongitudeTextView.setText(String.format(Locale.getDefault(),"%s: %f", mLongitudeLabel,
                mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(String.format("%s: %s", mLastUpdateTimeLabel,
                mLastUpdateTime));

    }
    private void updateUIWidgets(){
        if(mRequestingAddressUpdates){
            mAddressUpdateButton.setEnabled(false);
            mProgress.setVisibility(ProgressBar.VISIBLE);
        }
        else{
            mAddressUpdateButton.setEnabled(true);
            mProgress.setVisibility(ProgressBar.GONE);
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    public void startAddressUpdate()
    {
        Intent i=new Intent(getActivity(),FetchAddressIntentService.class);
        i.putExtra(Constants.RECEIVER,mReceiver);
        Log.wtf("APP","Send intent!");
        i.putExtra(Constants.LOCATION_DATA_EXTRA,mCurrentLocation);
        getActivity().startService(i);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("RS",mRequestingLocationUpdates);
        outState.putParcelable("L",mCurrentLocation);
        outState.putString("T",mLastUpdateTime);
        outState.putBoolean("RA",mRequestingAddressUpdates);
        outState.putString("AA",mAddressOutput);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation=location;
        mLastUpdateTime= DateFormat.getTimeInstance().format(new Date());
        updateUI();

    }

    public void receiveAddress() {
        if(!mGoogleApiClient.isConnected() || mCurrentLocation!=null){
            startAddressUpdate();
            mRequestingAddressUpdates=true;
            updateUIWidgets();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_updates_button:
                startUpdatesButtonH();
                break;
            case R.id.stop_updates_button:
                stopUpdatesButtonH();
                break;
            case R.id.address_receive:
                receiveAddress();
                break;
        }
    }

    private class AddressResultReceiver extends ResultReceiver {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.wtf("APP","came intent!");
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();
            mRequestingAddressUpdates=false;
            updateUIWidgets();


            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getActivity(),getString(R.string.address_found), Toast.LENGTH_SHORT).show();
            }

        }

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
    }

    private void displayAddressOutput() {
        mAddressTextView.setText(mAddressOutput);
    }
}
