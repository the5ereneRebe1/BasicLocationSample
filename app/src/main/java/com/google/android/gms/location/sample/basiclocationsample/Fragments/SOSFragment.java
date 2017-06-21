package com.google.android.gms.location.sample.basiclocationsample.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.location.sample.basiclocationsample.Model.Friends;
import com.google.android.gms.location.sample.basiclocationsample.R;
import com.google.android.gms.location.sample.basiclocationsample.UserHelper;
import com.google.android.gms.location.sample.basiclocationsample.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HIMANSHU on 4/23/2017.
 */

public class SOSFragment extends Fragment implements OnMapReadyCallback, GoogleMap.CancelableCallback {

    MapView mMapView;
    GoogleMap mGoogleMap;
    View v1;
    private  static final String URL_SEND_ALERT="http://abhidwivedi.16mb.com/insert_alert.php";
    private  static final String URL_CANCEL_ALERT="http://abhidwivedi.16mb.com/delete_alert.php";
    private  static final String URL_FETCH_USERS="http://abhidwivedi.16mb.com/alert_users.php";
    public ArrayList<Friends> aFriends=new ArrayList<Friends>();

    protected ImageButton alertButton;
    protected Button cancelButton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sos_fragment, container, false);
        v1=v;///Global view
        getActivity().setTitle("Explore");
        mMapView = (MapView) v.findViewById(R.id.mapview1);
        alertButton=(ImageButton) v.findViewById(R.id.alertButton);
        cancelButton=(Button) v.findViewById(R.id.cancelAlertButton);
        Log.i("why",""+UserHelper.getMode(getActivity()));
        if(UserHelper.getMode(getActivity())==true){
            alertButton.setEnabled(false);
            cancelButton.setVisibility(View.VISIBLE);
        }
        else{
            alertButton.setEnabled(true);
            cancelButton.setVisibility(View.GONE);
        }
        //OnClickListener for the cancel Button when a user cancel a SOS event
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlert();
                Toast.makeText(getActivity(),"Alert cancelled!",Toast.LENGTH_LONG).show();
                cancelButton.setEnabled(false);
                alertButton.setEnabled(true);
                cancelButton.setVisibility(View.GONE);
                UserHelper.storeMode(false,getActivity());
            }
        });
        //Click listener for the alert button
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Alert send!",Toast.LENGTH_LONG).show();
                alertButton.setEnabled(false);
                cancelButton.setEnabled(true);
                UserHelper.storeMode(true,getActivity());
                sendAlert();
                cancelButton.setVisibility(View.VISIBLE);
            }
        });
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important
        return v;
    }
    private void loadPeople() {
        StringRequest signInRequest=new StringRequest(Request.Method.POST, URL_FETCH_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response!=null){
                    aFriends.clear();
                    try {
                        JSONArray json = new JSONArray(response);
                        for(int i=0;i<json.length();i++){
                            JSONObject j=json.getJSONObject(i);
                            Friends f=new Friends(j.getString("name"),j.getString("latitude"),j.getString("longitude"));
                            Log.i("list:",j.getString("name")+j.getString("latitude")+j.getString("longitude"));
                            aFriends.add(f);
                        }
                        if(aFriends!=null){
                            setMarkers();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){


        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(signInRequest);
        Log.i("Send request","SENT for populating friends!");
    }
    private void setMarkers() {
        mGoogleMap.clear();

        for(int i=0;i<aFriends.size();i++){
            // Log.i("Size of array:",""+aFriends.size());
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(aFriends.get(i).getLatitude()),
                    Double.valueOf(aFriends.get(i).getLongitude())))).setTitle(aFriends.get(i).getUsername());
        }
    }
    private void deleteAlert() {
        StringRequest signInRequest=new StringRequest(Request.Method.POST, URL_CANCEL_ALERT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("phone",UserHelper.getPhone(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(signInRequest);

    }


    private void sendAlert() {
        StringRequest signInRequest=new StringRequest(Request.Method.POST, URL_SEND_ALERT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("phone",UserHelper.getPhone(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(signInRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap=googleMap;
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(UserHelper.getLatitude(getActivity())),
                Double.valueOf(UserHelper.getLongitude(getActivity()))), 10));
       mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style2));
        Toast.makeText(getActivity(),"Loading...",Toast.LENGTH_LONG).show();
        final Handler h = new Handler();
        final int delay = 2000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(SOSFragment.this.isVisible())
                {loadPeople();
                    h.postDelayed(this, delay);}
            }
        }, delay);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
}
