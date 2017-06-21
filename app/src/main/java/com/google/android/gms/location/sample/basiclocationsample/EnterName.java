package com.google.android.gms.location.sample.basiclocationsample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HIMANSHU on 4/12/2017.
 */

public class EnterName extends AppCompatActivity implements Response.Listener<String>,Response.ErrorListener{
    private Button bProceedSign;
    private AppCompatEditText etUsername;
    private ProgressDialog progressDialog;
    private String phone;
    private String username;
    private String secret;
    private final String URL_ADD_USER="http://abhidwivedi.16mb.com/meet_me_dalo.php";
    public static void launchActivity(AppCompatActivity currentActivity) {
        Intent intent = new Intent(currentActivity, EnterName.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_name);
        Intent i=getIntent();
        phone=i.getStringExtra("phone");
        secret=i.getStringExtra("secret");
        bProceedSign=(Button)findViewById(R.id.buttonUsername);
        etUsername=(AppCompatEditText) findViewById(R.id.editText_signIn_username);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {
        UserHelper.storePhone(phone,this);
        UserHelper.storeUsername(username,this);
        HomeScreen.launchActivity(this);
    }

    public void checkAndUpdate(View view) {
        /** TODO: 1.Check entered Username
         * TODO: 2. Send the user creds to the server along with the secret
         * TODO: 3.
         */
        username = etUsername.getText().toString().trim();
        boolean isInputValid = true;

        /** if (!username.matches("^[a-zA-Z0-9]+' '")) {
         isInputValid = false;
         this.etUsername.setError("Invalid username");
         }**/
        //TODO: Remove this after test
        isInputValid=true;
        if (isInputValid) {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setTitle("Meet");
            this.progressDialog.setMessage("Creating User...");
            this.progressDialog.show();

        }
        //Adding the user to the server as a new user
        try {

            StringRequest signInRequest=new StringRequest(Request.Method.POST,URL_ADD_USER,this,this){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params=new HashMap<>();
                    params.put("phone",phone);
                    params.put("name",username);
                    params.put("secret",secret);
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(signInRequest);

        }catch (Exception e){}
    }
}
