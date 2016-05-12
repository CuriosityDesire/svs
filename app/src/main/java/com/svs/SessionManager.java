package com.svs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {

    // Shared Preferences
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    public static final String PREF_NAME = "SVS";

    public static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_LNAME = "lname";
    public static final String KEY_ID = "id";
    public static final String KEY_COURSE_ID = "courseid";
    public static final String KEY_COUNT = "count";
    public static final String KEY_SEM= "sem";
    public static final String KEY_HALL= "hall";
    public static final String KEY_MOBILE= "mobile";
    public static final String KEY_EMAIL= "email";
    public static final String KEY_ADD= "add";
    public static final String KEY_DOB= "dob";
    public static final String KEY_COURSE= "course";



    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setLoginSession(String name,String lname, String id,String Courseid,String hall,String mobile,String email,String add,String dob) {

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_LNAME, lname);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_HALL, hall);
        editor.putString(KEY_COURSE_ID, Courseid);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ADD, add);
        editor.putString(KEY_DOB, dob);



        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setCounter(int cnt) {


        editor.putInt(KEY_COUNT, cnt);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    public void setSemester(String sem) {


        editor.putString(KEY_SEM,sem);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        // user email id
        user.put(KEY_ID, pref.getString(KEY_ID, "0"));
        user.put(KEY_LNAME, pref.getString(KEY_LNAME, "0"));
        user.put(KEY_ADD, pref.getString(KEY_ADD, "0"));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, "0"));
        user.put(KEY_COURSE_ID, pref.getString(KEY_COURSE_ID, "0"));
        user.put(KEY_SEM, pref.getString(KEY_SEM, "0"));
        user.put(KEY_HALL, pref.getString(KEY_HALL, "0"));
        user.put(KEY_DOB, pref.getString(KEY_DOB, "0"));
        // return user
        return user;
    }

    public HashMap<String, Integer> getCounter() {
        HashMap<String, Integer> user = new HashMap<String, Integer>();
        // MainActivity Counter
        user.put(KEY_COUNT, pref.getInt(KEY_COUNT, 0));

        return user;
    }
}
