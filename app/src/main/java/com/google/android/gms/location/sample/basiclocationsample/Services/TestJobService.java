package com.google.android.gms.location.sample.basiclocationsample.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.sample.basiclocationsample.APIHelper;
import com.google.android.gms.location.sample.basiclocationsample.HomeScreen;
import com.google.android.gms.location.sample.basiclocationsample.Model.Friends;
import com.google.android.gms.location.sample.basiclocationsample.R;
import com.google.android.gms.location.sample.basiclocationsample.UserHelper;
import com.google.android.gms.location.sample.basiclocationsample.VolleySingleton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HIMANSHU on 4/14/2017.
 * A JobScheduler class that defines a Job Service which is used to schedule jobs based on the parameters passed.
 * The parameters will decide when the OnStartJob function will be called.
 */

public class TestJobService extends JobService implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, Response.Listener<String>, Response.ErrorListener
{
    private final String URL_INSERT_LATLON="http://abhidwivedi.16mb.com/update.php";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected JobParameters mJobParameters;
    protected String response_from_prefs=" ";
    public TestJobService() {
        super();
    }


        @Override
        // This function is called when the Google Api Client is connected
        public void onConnected(@Nullable Bundle bundle) {
            mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.i("JobSchedulerTest", "on start job: " + mJobParameters.getJobId() + "," +
                    DateFormat.getTimeInstance().format(new Date())+
                    ",Location("+mLastLocation.getLatitude()+","+mLastLocation.getLongitude()+")"+ UserHelper.getUsername(this)+UserHelper.getPhone(this));
            try {
                //Store the latitude and longitude as SharedPreference for future use.
                UserHelper.storeLatitude(String.valueOf(mLastLocation.getLatitude()),this);
                UserHelper.storeLongitude(String.valueOf(mLastLocation.getLongitude()),this);

                StringRequest pushLatLon=new StringRequest(Request.Method.POST,URL_INSERT_LATLON,this,this){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params=new HashMap<>();
                        params.put("phone",UserHelper.getPhone(getBaseContext()));
                        params.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        params.put("longitude",String.valueOf(mLastLocation.getLongitude()));

                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(pushLatLon);

            }catch (Exception e){}
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            jobFinished(mJobParameters, true);
        }



        //The main function that gets called when the Scheduler schedules job.
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i("JobSchedulerTest","Job Running!");
        mJobParameters=jobParameters;

            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        return true;

    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("JobSchedulerTest","Job Stopped!");
        return true;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        jobFinished(mJobParameters, true);
    }

    @Override
    public void onResponse(String response) {
        String notification = "";
        String alert_msg = "SOS Alert";
        String notification_msg = "";
        int i = 0;
        Log.i("Response from web:", response);
        if(response.equals("[]"))
        {
            Log.i("Came here!!","HERE");
            UserHelper.storeAlert("[]",this);
        }
        response_from_prefs+=UserHelper.getAlert(this);
        Log.i("Response from prefs",response_from_prefs);
        if (response != null ) {
            if (response.compareTo(response_from_prefs.trim()) != 0) {
                Log.i("ALERT!", "alert send");
                //======================================================================================
                try {
                    JSONArray json = new JSONArray(response);
                    for (i = 0; i < json.length(); i++) {
                        JSONObject j = json.getJSONObject(i);
                        if (i == 0)
                            notification = notification + j.getString("name");
                        notification_msg = notification_msg + j.getString("name") + ", ";
                    }
                    if (i > 1) {
                        notification = notification + " and " + i + " other friends are in danger!";
                        notification_msg = notification_msg + " are in danger!";
                    } else {
                        notification = notification + " is in danger!";
                        notification_msg = notification;
                    }

                } catch (Exception e) {
                }
                //aFriends.add(f);
                UserHelper.storeAlert(response, this);
                generateNotification(this, alert_msg, notification_msg);
                //====================================================================================

            }

            jobFinished(mJobParameters, true);
            Log.i(this.getPackageName(),"Job Stopped!");
        }
    }
    private void generateNotification(Context context, String title, String content) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.sos)
                        .setContentTitle(title)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(content)
                        .setFullScreenIntent(null, true)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, HomeScreen.class);
        resultIntent.putExtra("KEY_SOS","SOS");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeScreen.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Id allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }
}
