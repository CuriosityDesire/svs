package com.svs;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Handler;
import android.widget.Toast;

import com.svs.com.svs.helper.DBHelper;

import java.util.HashMap;
import java.util.logging.LogRecord;

public class SplashScreen extends Activity {

    HashMap<String, Integer> userCounter;
    private static int SPLASH_TIME_OUT = 3000;
    private SessionManager session;
    HashMap<String, String> userDetails;
    DBHelper db;
    SQLiteDatabase sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        session = new SessionManager(getApplicationContext());
        userDetails = new HashMap<String, String>();
        userCounter = new HashMap<String, Integer>();
        userDetails = session.getUserDetails();

        db = new DBHelper(getApplication());
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (session.isLoggedIn()) {
                    // User is already logged in. Take him to main activity

                    try {
                        String qry = "select * from semester";
                        Cursor c = sd.rawQuery(qry, null);

                    if (c.getCount() <= 0) {
                        c.close();

                        Intent intent = new Intent(SplashScreen.this, Settings.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {

                        while (c.moveToNext()) {
                            String sem = c.getString(0);
                           // Toast.makeText(getApplication(), sem, Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                    c.close();
                    sd.close();
                    db.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            else

            {
                // User is already logged in. Take him to main activity
                Intent intent = new Intent(SplashScreen.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        }
    }

    ,SPLASH_TIME_OUT);

}
}
