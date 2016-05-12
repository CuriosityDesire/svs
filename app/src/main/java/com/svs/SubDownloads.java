package com.svs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.adapter.DownloadAdapter;
import com.svs.com.svs.helper.DBHelper;
import com.svs.com.svs.helper.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubDownloads extends AppCompatActivity {
    ArrayList<HashMap<String, String>> unitDlist;
    ProgressBar pgrSyncUnitsD;
    private SessionManager sessionmanager;
    private HashMap<String, String> userDetails;
    private ListView listUnitDownload;
    private String url;
    private JSONArray jarrayUnits;
    String subid, subname;

    private Toolbar mToolbar;
    private String unitid, uname;
    private List<downloads> dList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubDownloadAdapter mAdapter;
    private DBHelper db;
    SQLiteDatabase sd;
    LinearLayout llSubDownloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_downloads);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Downloads");
        Intent in = getIntent();
        subid = in.getStringExtra("SID");
        unitid = in.getStringExtra("UID");
        uname = in.getStringExtra("UNAME");
        db = new DBHelper(getApplication());
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();
        unitDlist = new ArrayList<HashMap<String, String>>();

        pgrSyncUnitsD = (ProgressBar) findViewById(R.id.pgrSyncSubD);
        recyclerView = (RecyclerView) findViewById(R.id.listSubDownload);
        llSubDownloads = (LinearLayout) findViewById(R.id.llSubDownloads);
        sessionmanager = new SessionManager(SubDownloads.this);
        userDetails = new HashMap<String, String>();
        //listUnitDownload= (ListView) findViewById(R.id.listUnitsDownloads);
        userDetails = sessionmanager.getUserDetails();


        new GetHomewrokDetailsFromServer().execute();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                downloads movie = dList.get(position);
                String filename = ((TextView) view.findViewById(R.id.lblSubName)).getText().toString();
                String fileurl = ((TextView) view.findViewById(R.id.lblSubFile)).getText().toString();

                try {

                    if (filename.toLowerCase().contains(".pdf")
                            || filename.toLowerCase().contains(".docx")) {


                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

            pgrSyncUnitsD.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            url = "http://logicupsolutions.com/svsalumni/api/download_by_sub.php?subid=" + subid;
            Log.d("url", url);
            ServiceHandler sh = new ServiceHandler();
            String str_stud_remark = sh
                    .makeServiceCall(url, ServiceHandler.GET);
            try {
                JSONObject json1 = new JSONObject(str_stud_remark);
                if (json1 != null) {

                    try {
                        Log.d("subject_downloads data", json1.toString());
                        // Getting Array of Contacts
                        jarrayUnits = json1.getJSONArray("files");

                        // looping through All Contacts
                        sd.delete("subject_downloads", null, null);
                        for (int i = 0; i < jarrayUnits.length(); i++) {
                            JSONObject cc = jarrayUnits.getJSONObject(i);
                            sd.execSQL("INSERT INTO subject_downloads VALUES('"
                                    + cc.getString("subid") + "','"
                                    + cc.getString("name") + "','"
                                    + cc.getString("file") + "') ");

                            String id = cc.getString("subid");
                            String name = cc.getString("name");
                            String file = cc.getString("file");

                            downloads dn = new downloads();
                            dn.setId(id);
                            dn.setName(name);
                            dn.setFile(file);
                            dList.add(dn);
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

                pgrSyncUnitsD.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //listSubDownload.setVisibility(View.VISIBLE);
                mAdapter = new SubDownloadAdapter(dList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                //FillDataonListView();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    //******************************************


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private UnitDownloads.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final UnitDownloads.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        public RecyclerTouchListener(Context applicationContext, RecyclerView recyclerView, ClickListener doclink) {
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    //*******************************************************************************

    public class SubDownloadAdapter extends RecyclerView.Adapter<SubDownloadAdapter.MyViewHolder> {

        private List<downloads> dList;
        private String f, filename;


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView id, name, file;
            public ImageView imgDownloadFile;

            public MyViewHolder(View view) {
                super(view);
                id = (TextView) view.findViewById(R.id.lblsubNo);
                name = (TextView) view.findViewById(R.id.lblSubName);
                file = (TextView) view.findViewById(R.id.lblSubFile);
                imgDownloadFile = (ImageView) view.findViewById(R.id.imgDownloadFile);

                imgDownloadFile.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

                if (v.getId() == imgDownloadFile.getId()) {

                    //   Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

                    try {
                        f = file.getText().toString();
                        filename = name.getText().toString();

                        new DownloadFileFromURL().execute(f);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                }
            }
        }


        public SubDownloadAdapter(List<downloads> dList) {
            this.dList = dList;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_sub_download_items, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            final downloads movie = dList.get(position);
            holder.id.setText(movie.getId());
            holder.name.setText(movie.getName());
            holder.file.setText(movie.getFile());


        }


        @Override
        public int getItemCount() {
            return dList.size();
        }


        class DownloadFileFromURL extends AsyncTask<String, String, String> {

            private ProgressDialog pDialogkk;

            /**
             * Before starting background thread Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialogkk = new ProgressDialog(SubDownloads.this);
                pDialogkk.setMessage("Downloading file. Please wait...");
                pDialogkk.setIndeterminate(false);
                pDialogkk.setMax(100);
                pDialogkk.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialogkk.setCancelable(true);
                pDialogkk.show();

            }

            @Override
            protected String doInBackground(String... f_url) {
                int count;

                try {
                    URL url = new URL(f_url[0]);
                    if (url.equals("") || url.equals(null)) {
                        Snackbar.make(llSubDownloads, "File not found", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        URLConnection conection = url.openConnection();
                        conection.connect();
                        // getting file length
                        int lenghtOfFile = conection.getContentLength();

                        // input stream to read file - with 8k buffer
                        InputStream input = new BufferedInputStream(
                                url.openStream(), 8192);

                        // Output stream to write file
                        OutputStream output = new FileOutputStream("/sdcard/"
                                + filename);

                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            publishProgress(""
                                    + (int) ((total * 100) / lenghtOfFile));

                            // writing data to file
                            output.write(data, 0, count);
                        }

                        // flushing output
                        output.flush();

                        // closing streams
                        output.close();
                        input.close();
                    }

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                return null;
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                pDialogkk.setProgress(Integer.parseInt(progress[0]));
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            @Override
            protected void onPostExecute(String file_url) {
                // dismiss the dialog after the file was downloaded
                pDialogkk.dismiss();
                Snackbar.make(llSubDownloads, "File Has been Downloaded,Check in your Internal Storage", Snackbar.LENGTH_LONG).setAction("Action", null).show();


                // Displaying downloaded image into image view
                // Reading image path from sdcard
                // String imagePath = Environment.getExternalStorageDirectory()
                // .toString() + "/downloadedfile.jpg";
                // setting downloaded into image view
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //*******************************************************************************
}
