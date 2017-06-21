package com.google.android.gms.location.sample.basiclocationsample;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by HIMANSHU on 4/5/2017.
 */

public class LoginActivity extends AppCompatActivity implements Response.Listener<String>, Response.ErrorListener {
    private static final String TWITTER_KEY = "FLGMzv3fwF2Rx5TSdWhx6rJ0U";
    private static final String TWITTER_SECRET = "1iD6VgbTKl2eIeVWvbJbWrn4EaJR34V1KpzHa9d5lBGq4DvOSd";
    private static final String URL_PHONE_CHECK="http://abhidwivedi.16mb.com/meet.php";
    private String phone;
    private String twitterSecret;
    private boolean conditions=true;

    @Override
    protected void onPostResume() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            conditions=false;
        }
        else {
            conditions=true;
            checkPhoneNumber(phone);
        }
        super.onPostResume();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActionBar ab=getSupportActionBar();
        ab.hide();
        // Configure the twitter OTP based APi and register callbacks.
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);

        Fabric.with(getApplicationContext(), new TwitterCore(authConfig), digitsBuilder.build());
        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setText("Welcome");
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                phone=phoneNumber;
                twitterSecret=session.getAuthToken().toString();
                Log.e("Baba",""+phoneNumber+session.getAuthToken());
                //Toast.makeText(getApplicationContext(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();

                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                    conditions=false;
                }

               if(conditions==true){
                   checkPhoneNumber(phoneNumber);
               }


            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });

    }

    //Function that checks if GPS is enabled or not.
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    private void checkPhoneNumber(final String phoneNumber) {
        try {

            StringRequest signInRequest=new StringRequest(Request.Method.POST,URL_PHONE_CHECK,this,this){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params=new HashMap<>();
                    params.put("phone",phoneNumber);
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(signInRequest);

        }catch (Exception e){}
    }

    @Override
    public void onErrorResponse(VolleyError error) {
       // Toast.makeText(this,"No Internet Access",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(String response) {
        //Toast.makeText(this, "Response:" + response, Toast.LENGTH_LONG).show();
       // Log.e("Apple", response);

        if(response.length()>7){
        String responseCode = response.substring(0, 7);
        Log.i("substring", responseCode);
        if (responseCode.trim().compareTo("success") == 0) {
            //TODO: Call Home Screen Activity directly
            //TODO: Update user preferences as the user does not need to add his details again
            UserHelper.storePhone(phone, this);
            UserHelper.storeUsername(response.substring(8), this);
            HomeScreen.launchActivity(this);
        }
        else {
            Toast.makeText(this, "Some error occured!",Toast.LENGTH_LONG).show();

        }
        }
        else if(response.compareTo("failure")==0) {

            Intent i=new Intent(this, EnterName.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("phone",phone);
            i.putExtra("secret",twitterSecret);
            startActivity(i);
        }
    }
}
