package com.svs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.com.svs.helper.DBHelper;
import com.svs.com.svs.helper.JSONParser;
import com.svs.com.svs.helper.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Units extends AppCompatActivity {
    ProgressBar pgrSyncUnits;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;
    private ListView listview, listSubDownload;
    private String url;
    private JSONArray jarrayUnits;
    private String unitname;
    DBHelper db;
    ArrayList<HashMap<String, String>> unitlist = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> subdownlist;
    SQLiteDatabase sd;
    ProgressBar pgrShowHomework;
    TextView lblShowTextSync, lblBackHomework;
    String subid, subname;

    private Toolbar mToolbar;
    int cnt = 0;
    private String unitid;
    private String sid,sname,sfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent in = getIntent();
        subid = in.getStringExtra("ID");
        subname = in.getStringExtra("NAME");
        subdownlist = new ArrayList<HashMap<String, String>>();
        setTitle(subname);
        pgrSyncUnits = (ProgressBar) findViewById(R.id.pgrSyncUnits);
        sessionmanager = new SessionManager(Units.this);
        userDetails = new HashMap<String, String>();
        listview = (ListView) findViewById(R.id.listUnits);


        userDetails = sessionmanager.getUserDetails();
        db = new DBHelper(getApplication());
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();

        new GetHomewrokDetailsFromServer().execute();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String unitname = ((TextView) view.findViewById(R.id.lblUnitName)).getText().toString();
                String unitid = ((TextView) view.findViewById(R.id.lblUnitId)).getText().toString();

                Intent i = new Intent(Units.this, UnitDownloads.class);
                i.putExtra("UID", unitid);
                i.putExtra("UNAME", unitname);
                i.putExtra("SID", subid);
                startActivity(i);
            }
        });
    }

    public void FillDataonListView() {


        Cursor c = sd.rawQuery("select * from Units where subid=" + '"' + subid + '"', null);

        unitlist.clear();
        subdownlist.clear();
        while (c.moveToNext()) {

            unitid = c.getString(0);
            unitname = c.getString(2);
            cnt++;
            HashMap<String, String> map = new HashMap<String, String>();

            map.put("ID", unitid);
            map.put("NAME", unitname);
            map.put("No", "Chapter " + cnt);
            unitlist.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(Units.this,
                unitlist, R.layout.list_unit_item, new String[]{
                "ID","NAME", "No"},
                new int[]{R.id.lblUnitId,R.id.lblUnitName, R.id.lblUnitNo});

        listview.setAdapter(adapter);



    }



    // ***************************************

    class GetHomewrokDetailsFromServer extends AsyncTask<String, Void, String> {

        private String url1;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // pDialog = new ProgressDialog(FragmentHomework.this);
            // pDialog.setMessage("Please wait...");
            // pDialog.show();
            // pDialog.setCancelable(false);

            pgrSyncUnits.setVisibility(View.VISIBLE);
            listview.setVisibility(View.GONE);
//            listSubDownload.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            JSONParser jParser = new JSONParser();

            // getting JSON string from URL=
            // url = AllKeys.WEBSITE + "json_data.aspx?DeptId=" + deptid
            // + "&type=homework";

            url = "http://logicupsolutions.com/svsalumni/api/get_units.php?subject=" + subid;
            url1 = "http://logicupsolutions.com/svsalumni/api/download_by_sub.php?subid=" + subid;
            Log.d("url", url);
            // url = AllKeys.WEBSITE+ "json_data.aspx?DeptId=" + "3" +
            // "&type=homework";
            // JSONObject json = jParser.getJSONFromUrl(url);
            ServiceHandler sh = new ServiceHandler();
            String str_stud_remark = sh
                    .makeServiceCall(url, ServiceHandler.GET);
            String str_stud_remark1 = sh
                    .makeServiceCall(url1, ServiceHandler.GET);
            try {
                JSONObject json = new JSONObject(str_stud_remark);
                if (json != null) {

                    try {
                        Log.d("Units Json data", json.toString());
                        // Getting Array of Contacts
                        jarrayUnits = json.getJSONArray("units");

                        // looping through All Contacts
                        sd.delete("Units", null, null);
                        for (int i = 0; i < jarrayUnits.length(); i++) {
                            JSONObject c = jarrayUnits.getJSONObject(i);

                            sd.execSQL("INSERT INTO Units VALUES('"
                                    + c.getString("unitId") + "','"
                                    + c.getString("subjectId") + "','"
                                    + c.getString("unitTitle") + "') ");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("units json error", e.getMessage());
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("SimpleDateFormat")

        @Override
        protected void onPostExecute(String result) {
            // pDialog.dismiss();
            // pDialog.cancel();
            try {

                pgrSyncUnits.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
                //listSubDownload.setVisibility(View.VISIBLE);

                FillDataonListView();
               // FillDataonListView1();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_subject_file, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_sub_file:

                Intent i = new Intent(Units.this, UploadFile.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("SID", subid);
                i.putExtra("UID", unitid);
                i.putExtra("ACTIVITY", "subject");
                startActivity(i);
                return true;
            case R.id.action_download_sub_file:

                Intent ii = new Intent(Units.this, SubDownloads.class);
                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ii.putExtra("SID", subid);
                ii.putExtra("UID", unitid);
                startActivity(ii);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
