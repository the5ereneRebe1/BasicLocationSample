package com.google.android.gms.location.sample.basiclocationsample;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.sample.basiclocationsample.Fragments.FriendsFragment;
import com.google.android.gms.location.sample.basiclocationsample.Fragments.MapViewFragment;
import com.google.android.gms.location.sample.basiclocationsample.Fragments.SOSFragment;
import com.google.android.gms.location.sample.basiclocationsample.Fragments.TestFragment;
import com.google.android.gms.location.sample.basiclocationsample.Services.TestJobService;


public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FragmentTransaction fragmentTransaction;
    protected FragmentManager fragmentManager;
    protected TextView mDrawerName;
    protected TextView mDrawerPhone;
    protected String notification_intent;

    public static void launchActivity(AppCompatActivity currentActivity) {
        Intent intent = new Intent(currentActivity, HomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        currentActivity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String menuFragment = getIntent().getStringExtra("KEY_SOS");
            setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        /**FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v=navigationView.inflateHeaderView(R.layout.nav_header_home_screen);
        mDrawerName=(TextView) v.findViewById(R.id.tvUsername);
        mDrawerName.setText(UserHelper.getUsername(this));
        mDrawerPhone=(TextView) v.findViewById(R.id.tvPhonenumber);
        mDrawerPhone.setText(UserHelper.getPhone(this));
        if (menuFragment != null) {

            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment.equals("SOS")) {
                SOSFragment sosFragment=new SOSFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.add(R.id.content_frame,sosFragment);
                fragmentTransaction.commit();
            }
        } else {
            // Activity was not launched with a menuFragment selected -- continue as if this activity was opened from a launcher (for example)
            FriendsFragment friendsFragment=new FriendsFragment();
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.add(R.id.content_frame,friendsFragment);
            fragmentTransaction.commit();
        }

        scheduleJob();

    }

   /* public static void scheduleJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = createJob(dispatcher);
        dispatcher.schedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){
        Job job = dispatcher.newJobBuilder()
                // persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                // Call this service when the criteria are met.
                .setService(TestJobService.class)
                // unique id of the task
                .setTag("MeetJob1")
                .setReplaceCurrent(true)

                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 30 - 60 seconds from now.
                .setTrigger(Trigger.executionWindow(5, 10))

                //Run this job only when the network is avaiable.
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        return job;
    }
*/
    private void scheduleJob() {
        //Set the factors to help the JobScheduler schedule job
        ComponentName mServiceComponent = new ComponentName(this, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(5,mServiceComponent);
        //builder.setMinimumLatency(5 * 1000); // wait at least
        //builder.setOverrideDeadline(50 * 1000); // maximum delay
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);// require unmetered network
        builder.setBackoffCriteria(5000,JobInfo.BACKOFF_POLICY_LINEAR);
        builder.setRequiresDeviceIdle(false); // we dont care if device should be idle
        builder.setRequiresCharging(false);// we don't care if the device is charging or not
        //builder.setPersisted(true);
        builder.setPeriodic(10*1000);

        JobScheduler jobScheduler = (JobScheduler)getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            if(UserHelper.getLatitude(this)==null){
                Toast.makeText(this,"Location not enabled",Toast.LENGTH_LONG).show();
            }
            else {
                MapViewFragment mapFragment = new MapViewFragment();
                fragmentManager.beginTransaction().replace(R.id.content_frame, mapFragment).commit();
            }
                // Handle the camera action
        } else if (id == R.id.nav_test) {
            TestFragment testFragment=new TestFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,testFragment).commit();
        } else if (id == R.id.nav_friends) {
            FriendsFragment friendsFragment=new FriendsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,friendsFragment).commit();
       // } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            SOSFragment sosFragment=new SOSFragment();
            fragmentManager.beginTransaction().replace(R.id.content_frame,sosFragment).commit();

        } /*else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
