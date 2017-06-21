package com.google.android.gms.location.sample.basiclocationsample.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.sample.basiclocationsample.Model.Friends;
import com.google.android.gms.location.sample.basiclocationsample.R;
import com.google.android.gms.location.sample.basiclocationsample.UserHelper;
import com.google.android.gms.location.sample.basiclocationsample.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendsFragment extends Fragment implements AdapterView.OnItemClickListener,Response.Listener<String>, Response.ErrorListener {

    private  static final String URL_LOAD_FRIENDS="http://abhidwivedi.16mb.com/fetch.php";
    protected ArrayList<String> items=new ArrayList<String>();
    View rootView;
    ArrayAdapter<String> itemsAdapter;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friend_fragment, container, false);
        rootView=view;
        getActivity().setTitle("Friends");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        items.add("Loading...");
         itemsAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,items);//Passing the array to the adapter.
        listView = (ListView) rootView.findViewById(R.id.list);
        listView.setAdapter(itemsAdapter); //Adding adapter to the list view
        final Handler h = new Handler();
        final int delay = 5000; //milliseconds

        /** Call loadPeople() to populate friends after a certain delay=5s*/
        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                Log.i("FriendsFragment","I am running");
                if(FriendsFragment.this.isVisible())
                    if(UserHelper.getPhone(getActivity())!=null)
                    {loadPeople();h.postDelayed(this, delay);}


            }
        }, delay);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

/* Send request to the server to load friends*/
    private void loadPeople() {
        StringRequest signInRequest=new StringRequest(Request.Method.POST,URL_LOAD_FRIENDS,this,this){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("phone", UserHelper.getPhone(getActivity()));
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(signInRequest);
        Log.i("Send request","SENT for populating friends!");
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    /* Parse the response string to JSON Array*/
    @Override
    public void onResponse(String response) {
        try {
            items.clear();
            Log.i("FriendsFragment",response);
            JSONArray json = new JSONArray(response);
            for(int i=0;i<json.length();i++){
                JSONObject j=json.getJSONObject(i);

                items.add(j.getString("name"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        populateList();
    }
// Notify the items Adapter that the value of the passed to it has changed.
    private void populateList() {

        itemsAdapter.notifyDataSetChanged();

    }
}
