package com.svs.fragments;

/**
 * Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.Item;
import com.svs.R;
import com.svs.SessionManager;
import com.svs.Units;
import com.svs.adapter.LazyAdapter;
import com.svs.com.svs.helper.ConnectionDetector;
import com.svs.com.svs.helper.DBHelper;
import com.svs.com.svs.helper.NetConnectivity;
import com.svs.com.svs.helper.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {


    GridView subjectgrid;
    ConnectionDetector cd;
    DrawerLayout drawerLayout;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    SessionManager session;
    HashMap<String, String> userdetails;
    ArrayList<String> ilist;
    DBHelper db;
    SQLiteDatabase sd;

    private Item item;
    Bitmap about, contact, ourteam, upcoming, courses, login;

    LazyAdapter adapter;

    ArrayList<HashMap<String, String>> menuList = new ArrayList<HashMap<String, String>>();

    ArrayList<String> lstmenuid = new ArrayList<String>();
    ArrayList<String> lsttitle = new ArrayList<String>();
    ArrayList<String> lstscreentype = new ArrayList<String>();
    ArrayList<String> lstwebfile = new ArrayList<String>();

    // XML node keys
    // static final String KEY_SONG = "song"; // parent node
    public static final String KEY_IMAGE = "subImage";
    public static final String KEY_ID = "subjectId";
    public static final String KEY_CODE = "subjectCode";
    public static final String KEY_NAME = "subjectName";
    public static final String KEY_COURSEID = "courseId";
    public static final String KEY_DESC = "description";
    public static final String KEY_REGULAION = "regulation";
    ImageView imgNoInternet;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        subjectgrid = (GridView) rootView.findViewById(R.id.subjectsgird);
        imgNoInternet = (ImageView) rootView.findViewById(R.id.imgNoInternet);

        session = new SessionManager(getActivity());
        userdetails = new HashMap<String, String>();
        userdetails = session.getUserDetails();
        db = new DBHelper(getActivity());
        sd = db.getWritableDatabase();
        sd = db.getReadableDatabase();


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {

            subjectgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String subname = ((TextView) view.findViewById(R.id.item_text)).getText().toString();
                        String subid = ((TextView) view.findViewById(R.id.item_id)).getText().toString();

                       // Snackbar.make(getView(), "You Clicked " + subname, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        Intent i = new Intent(getActivity(), Units.class);
                        i.putExtra("ID", subid);
                        i.putExtra("NAME", subname);
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            if (NetConnectivity.isOnline(getActivity())) {
                new GetMenuDetailsFromServer().execute();
            } else {

                // imgNoInternet.setVisibility(View.VISIBLE);
                subjectgrid.setVisibility(View.GONE);
                Snackbar.make(getView(), "Please Check Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetMenuDetailsFromServer extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String qry = "select * from semester";
            Cursor cc = sd.rawQuery(qry, null);
            while (cc.moveToNext()) {
                String sem = cc.getString(0);
                //Toast.makeText(getActivity(), sem, Toast.LENGTH_LONG).show();
            }
            ServiceHandler sh = new ServiceHandler();
            String URL;
            //String URL = "http://logicupsolutions.com/svsalumni/api/get_sem.php?course="+userdetails.get(SessionManager.KEY_COURSE_ID)+"&sem="+userdetails.get(SessionManager.KEY_SEM);
            if (userdetails.get(SessionManager.KEY_SEM) == "0") {
                URL = "http://logicupsolutions.com/svsalumni/api/get_subjects.php?course=2&sem=1";
            } else {
                URL = "http://logicupsolutions.com/svsalumni/api/get_subjects.php?course=2&sem=" + userdetails.get(SessionManager.KEY_SEM);
            }

            String response = sh.makeServiceCall(URL, ServiceHandler.GET);
            Log.d("data : ", response);
            menuList.clear();
            lstmenuid.clear();
            lstscreentype.clear();
            lsttitle.clear();
            lstwebfile.clear();

            try {
                // response = convertToJsonFormat(response);
                JSONObject obj = new JSONObject(response);
                JSONArray arr = obj.getJSONArray("subjects");

                for (int i = 0; i < arr.length(); i++) {
                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Element e = (Element) nl.item(i);
                    JSONObject c = arr.getJSONObject(i);

                    lstmenuid.add(c.getString(KEY_ID));
                    lsttitle.add(c.getString(KEY_NAME));

                    // adding each child node to HashMap key => value
                    map.put(KEY_ID, c.getString(KEY_ID));
                    map.put(KEY_NAME, c.getString(KEY_NAME));
                    if (c.getString(KEY_IMAGE).contains(" ")) {
                        String thumb = c.getString(KEY_IMAGE).replace(" ",
                                "%20");

                        map.put(KEY_IMAGE, thumb);
                    } else {

                        map.put(KEY_IMAGE, c.getString(KEY_IMAGE));
                    }

                    // here insert into table

                    // adding HashList to ArrayList
                    menuList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                pDialog.cancel();
                pDialog.dismiss();

                // Getting adapter by passing xml data ArrayList
                adapter = new LazyAdapter(getActivity(), menuList);
                subjectgrid.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
