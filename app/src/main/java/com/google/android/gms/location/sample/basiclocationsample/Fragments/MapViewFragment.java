package com.google.android.gms.location.sample.basiclocationsample.Fragments;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.sample.basiclocationsample.APIHelper;
import com.google.android.gms.location.sample.basiclocationsample.Model.Friends;
import com.google.android.gms.location.sample.basiclocationsample.R;
import com.google.android.gms.location.sample.basiclocationsample.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.sample.basiclocationsample.UserHelper;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HIMANSHU on 4/17/2017.
 */

public class MapViewFragment extends Fragment implements OnMapReadyCallback, Response.Listener<String>, Response.ErrorListener,
                                    GoogleMap.CancelableCallback
{

    MapView mMapView;

    GoogleMap mGoogleMap;
    private  static final String URL_LOAD_FRIENDS="http://abhidwivedi.16mb.com/fetch.php";
    private boolean mapReady = false;
    //private GoogleApiClient mGoogleApiClient;
    //private LocationRequest mLocationRequest;
    protected LatLng mLatLng;
    public ArrayList<Friends> aFriends=new ArrayList<Friends>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        getActivity().setTitle("Explore");
        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important to refresh the map
        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mGoogleMap = googleMap;
        //mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Log.i("Getting latitude",UserHelper.getLatitude(getActivity()));
        final Handler h = new Handler();
        final int delay = 5000; //milliseconds

        //Call the loadPeople() function after every 5s to populate markers on the map
        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                if(MapViewFragment.this.isVisible())
                {loadPeople();
                h.postDelayed(this, delay);}
            }
        }, delay);


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(UserHelper.getLatitude(getActivity())),
                Double.valueOf(UserHelper.getLongitude(getActivity()))), 19));// Move camera to the current location of the user

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(UserHelper.getLatitude(getActivity())),
                Double.valueOf(UserHelper.getLongitude(getActivity())))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTitle("MEEEE!");
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.json_style));
      //  Toast.makeText(this.getActivity(),"("+UserHelper.getLatitude(getActivity())+","+UserHelper.getLongitude(getActivity())+")",Toast.LENGTH_LONG).show();
    }

    //Function that sends a Volley request to the server.
    private void loadPeople() {
        StringRequest signInRequest=new StringRequest(Request.Method.POST,URL_LOAD_FRIENDS,this,this){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("phone",UserHelper.getPhone(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(signInRequest);
        Log.i("Send request","SENT!");
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
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        aFriends.clear();
        Log.i("RECEIVED","YO!"+response);
        try {
            JSONArray json = new JSONArray(response);
            for(int i=0;i<json.length();i++){
                JSONObject j=json.getJSONObject(i);
                Friends f=new Friends(j.getString("name"),j.getString("latitude"),j.getString("longitude"));
                Log.i("list:",j.getString("name")+j.getString("latitude")+j.getString("longitude"));
                aFriends.add(f);
            }
            if(aFriends!=null){
                setMarkers();//Set markers on the map from the list retrived from the server.
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setMarkers() {
        mGoogleMap.clear();
       // mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(UserHelper.getLatitude(getActivity())),
             //   Double.valueOf(UserHelper.getLongitude(getActivity()))), 19),1000,this);
        if(MapViewFragment.this.isVisible()) {
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(UserHelper.getLatitude(getActivity())),
                    Double.valueOf(UserHelper.getLongitude(getActivity())))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).setTitle("MEEEEE!");
        }
            for(int i=0;i<aFriends.size();i++){
           // Log.i("Size of array:",""+aFriends.size());
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(aFriends.get(i).getLatitude()),
                    Double.valueOf(aFriends.get(i).getLongitude())))).setTitle(aFriends.get(i).getUsername());
        }
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }
}