package com.svs;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.svs.com.svs.helper.ConnectionDetector;
import com.svs.com.svs.helper.DBHelper;
import com.svs.com.svs.helper.NetConnectivity;
import com.svs.fragments.StudentProfileFragment;
import com.svs.fragments.HomeFragment;
import com.svs.fragments.MessagesFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    TextView lblSignUp, lblDisplayname, labelHome;
    String name;
    private SessionManager session;
    HashMap<String, String> userDetails;
    HashMap<String, Integer> userCounter;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    DBHelper db;
    SQLiteDatabase sd;
    TextView lblLoginUsername;

    GridView subjectgrid;
    ConnectionDetector cd;
    DrawerLayout drawerLayout;
    ArrayList<Item> gridArray = new ArrayList<Item>();

    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            // showDialogbox();
            // showDialog();
            session = new SessionManager(getApplicationContext());
            userDetails = new HashMap<String, String>();
            userCounter = new HashMap<String, Integer>();
            userDetails = session.getUserDetails();
            userCounter= session.getCounter();
            count++;
            session.setCounter(count);
            int putcount = userCounter.get(SessionManager.KEY_COUNT);
            if (putcount == 0) {
                Intent i = new Intent(MainActivity.this, NewsPopupActivity.class);
                startActivity(i);
            }


            cd = new ConnectionDetector(getApplicationContext());
            drawerLayout = (DrawerLayout) findViewById(R.id
                    .drawer_layout);
            // Check if Internet present

            // getSupportActionBar().hide();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            lblLoginUsername = (TextView) findViewById(R.id.lblLoginUsername);
            db = new DBHelper(getApplicationContext());
            sd = db.getReadableDatabase();
            sd = db.getWritableDatabase();


            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener(this);

            // display the first navigation drawer view on app launch
            displayView(0);
            // session manager

            try {
                String namelogin = userDetails.get(SessionManager.KEY_NAME);
                lblLoginUsername.setText("welcome " + namelogin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private void showDialogbox() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getApplicationContext());
        builder.setView(R.layout.activity_news_popup);
        builder.show();
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        session.editor.clear();
        session.editor.commit();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Intent i = new Intent(MainActivity.this,Settings.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new StudentProfileFragment();
                title = getString(R.string.title_profile);
                break;
            case 2:
                if (NetConnectivity.isOnline(getApplicationContext())) {
                    logoutUser();
                } else {

                    Snackbar.make(drawerLayout, "Please Check Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    //*********************************
    void showDialog() {
        try {
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = MyDialogFragment.newInstance();
            newFragment.show(ft, "dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class MyDialogFragment extends DialogFragment {

        static MyDialogFragment newInstance() {
            MyDialogFragment f = new MyDialogFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_news_popup, container, false);
            return v;
        }

    }
    //*********************************

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }
}
