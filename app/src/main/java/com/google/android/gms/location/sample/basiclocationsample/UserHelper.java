package com.google.android.gms.location.sample.basiclocationsample;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Creator: vbarad
 * Date: 2016-08-10
 * Project: veggkart
 */
public class UserHelper {
  private static final String PREFS_FILE = "meet-prefs";
  private static final String KEY_USERNAME = "username";
  private static final String KEY_PHONE_NO = "phone-no";
  private static final String KEY_LATITUDE="latitude";
  private static final String KEY_LONGITUDE="longitude";
  private static final String KEY_ALERTS="sos_alerts";
    private static final String ALERT_MODE="alert_status";

  public static void storeUsername(String username, Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_USERNAME, username);
    editor.apply();
  }
  public static void storeAlert(String alert, Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_ALERTS, alert);
    editor.apply();
  }
    public static void storeMode(boolean alert, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALERT_MODE, alert);
        editor.apply();
    }
  public static void storePhone(String phone, Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_PHONE_NO, phone);
    editor.apply();
  }
  public static void storeLatitude(String latitude, Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    Log.i("Storing latitude",latitude);
    editor.putString(KEY_LATITUDE, latitude);
    editor.apply();
  }
  public static void storeLongitude(String longitude, Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_LONGITUDE, longitude);
    editor.apply();
  }

  public static String getUsername(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    String username = preferences.getString(KEY_USERNAME, null);
    return username;
  }
  public static String getAlert(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    String username = preferences.getString(KEY_ALERTS, null);
    return username;
  }
    public static boolean getMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        boolean bool = preferences.getBoolean(ALERT_MODE, false);
        return bool;
    }


  public static String getPhone(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    String userId = preferences.getString(KEY_PHONE_NO, null);
    return userId;
  }
  public static String getLatitude(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    String userId = preferences.getString(KEY_LATITUDE,null);
    return userId;
  }
  public static String getLongitude(Context context) {
    SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    String userId = preferences.getString(KEY_LONGITUDE,null);
    return userId;
  }

  public static void clearUserDetails(Context context) {
    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove(KEY_ALERTS);
    editor.apply();
  }


}
