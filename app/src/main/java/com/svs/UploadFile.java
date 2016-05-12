package com.svs;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;


import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.com.svs.helper.NetConnectivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class UploadFile extends AppCompatActivity {
    private Toolbar mToolbar;
    Button btnUploadSubFile;
    private static final String TAG = MainActivity.class.getSimpleName();
    EditText txtFilePath;
    private File selectedFile;
    private static final int REQUEST_PICK_FILE = 1;
    private Button btnCapturePicture, btnRecordVideo;
    private Uri uri;
    public final static String EXTRA_FILE_PATH = "file_path";
    private ProgressBar progressBar;
    long totalSize = 0;
    TextView txtPercentage;
    private String filepath = null;
    private String ll;
    Cursor returnCursor;
    private int nameIndex;
    ImageView imgUpload;
    private int serverResponseCode = 0;
    private String encodedString;
    private HttpResponse response;
    private String responseServer;
    private ProgressDialog progressDialog;
    LinearLayout llUpload;
    private String subID;
    private String unitid;
    public static final int progress_bar_type = 0;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private boolean isAnimActive;
    private final Random random = new Random();
    private String act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upload Files");
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        txtFilePath = (EditText) findViewById(R.id.txtFilePath);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        imgUpload = (ImageView) findViewById(R.id.imgUpload);
        llUpload = (LinearLayout) findViewById(R.id.llUpload);
        Intent in = getIntent();
        subID = in.getStringExtra("SID");
        unitid = in.getStringExtra("UID");
        act = in.getStringExtra("ACTIVITY");
        /**
         * Capture image button click event
         */
        imgUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                //imgUpload.setImageResource(R.drawable.uploadicon);
                captureImage();


            }
        });


    }


    //********************************************************

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                progressDialog = new ProgressDialog(UploadFile.this);
                progressDialog.setMessage("Uploading Please wait..");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.show();
                return progressDialog;
            default:
                return null;
        }
    }

    //********************************************************

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            uri = Uri.parse(String.valueOf(Environment.getExternalStorageDirectory())); // a directory
            intent.setDataAndType(uri, "*/*");
            startActivityForResult(intent, REQUEST_PICK_FILE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {

                Uri selectedFileUri = data.getData();
                filepath = FilePath.getPath(this, selectedFileUri);


                Log.i(TAG, "Selected File Path:" + filepath);

                if (NetConnectivity.isOnline(getApplicationContext())) {

                    if (filepath.toLowerCase().contains(".pdf") || filepath.toLowerCase().contains(".docx") || filepath.toLowerCase().contains(".doc")) {
                        new UploadFileToServer().execute();
                    } else {

                        Snackbar.make(llUpload, "Only (pdf,docx) are allowed", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    //UploadFileBahar();
                } else {

                    Snackbar.make(llUpload, "Please Check Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }


            } else
                Toast.makeText(UploadFile.this, "Back from pick with cancel status", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//*****************************************************

    class UploadFileToServer extends AsyncTask<String, String, String> {

        private ProgressDialog pDialogkk;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialogkk = new ProgressDialog(UploadFile.this);
            pDialogkk.setMessage("Uploading file. Please wait...");
            pDialogkk.setCancelable(true);
            pDialogkk.show();

        }

        @Override
        protected String doInBackground(String... f_url) {
            int serverResponseCode = 0;


            File selectedFile = new File(filepath);


            String[] parts = filepath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {
                //dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    }
                });

            } else {

                try {
                    URL url = new URL(Config.FILE_UPLOAD_URL);
                    final MultipartUtility http = new MultipartUtility(url);
                    http.addFormField("subid", subID);
                   if(act.equals("subject"))
                   {
                       http.addFormField("unitid", "0");
                   }
                    else
                   {
                       http.addFormField("unitid", unitid);
                   }
                    http.addFilePart("file", selectedFile);
                    final byte[] bytes = http.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialogkk.dismiss();

            Snackbar.make(llUpload, "File Has been Uploaded", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            // String imagePath = Environment.getExternalStorageDirectory()
            // .toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
        }

    }


    class UploadFileToServerOld extends AsyncTask<String, String, String> {

        @SuppressWarnings("deprecation")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {


            int serverResponseCode = 0;

            HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 5120 * 5120;
            File selectedFile = new File(filepath);


            String[] parts = filepath.split("/");
            final String fileName = parts[parts.length - 1];

            if (!selectedFile.isFile()) {
                //dialog.dismiss();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                    }
                });

            } else {

                try {
                    URL url = new URL(Config.FILE_UPLOAD_URL);
                    final MultipartUtility http = new MultipartUtility(url);
                    http.addFormField("subid", subID);
                    http.addFormField("unitid", "0");
                    http.addFilePart("file", selectedFile);
                    final byte[] bytes = http.finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
             /* else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(selectedFile);
                    URL url = new URL(Config.FILE_UPLOAD_URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);//Allow Inputs
                    connection.setDoOutput(true);//Allow Outputs
                    connection.setUseCaches(false);//Don't use a cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("subid", subID);
                    connection.setRequestProperty("unitid", "0");
                    connection.setRequestProperty("file", filepath);


                    //creating new dataoutputstream
                    dataOutputStream = new DataOutputStream(connection.getOutputStream());

                    //writing bytes to data outputstream
                    dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);


                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                            + filepath + "\"" + lineEnd);

                    dataOutputStream.writeBytes(lineEnd);


                    //returns no. of bytes present in fileInputStream
                    bytesAvailable = fileInputStream.available();
                    //selecting the buffer size as minimum of available bytes or 1 MB
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    //setting the buffer as byte array of size of bufferSize
                    buffer = new byte[bufferSize];

                    //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                    while (bytesRead > 0) {
                        //write the bytes read from inputstream
                        dataOutputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    dataOutputStream.writeBytes(lineEnd);
                    dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();

                    Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                    DataInputStream inStream;
                    try {
                        inStream = new DataInputStream(connection.getInputStream());
                        String str;
                        while ((str = inStream.readLine()) != null) {
                            Log.e("joshtag", "SOF Server Response" + str);
                        }
                        inStream.close();
                    }
                    catch (IOException ioex) {
                        Log.e("joshtag", "SOF error: " + ioex.getMessage(), ioex);
                    }

                    //close the streams //
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    //response code of 200 indicates the server status OK
                    if (serverResponseCode == 200) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlert("Uploaded Successfully");
                                // tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                            }
                        });
                    }

                    //closing the input and output streams


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadFile.this, "File Not Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(UploadFile.this, "URL error!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UploadFile.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                }
                //dialog.dismiss();

            }

            */


            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            Toast.makeText(UploadFile.this, subID, Toast.LENGTH_LONG).show();
        }

    }


    //************************************************

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadFile.this);
            builder.setMessage(message).setTitle("Response from Server")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //*******************************************************************************************************************


    public void UploadFileBahar() {
        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 5120 * 5120;
        File selectedFile = new File(filepath);


        String[] parts = filepath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            //dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //tvFileName.setText("Source File Doesn't Exist: " + selectedFilePath);
                }
            });

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(Config.FILE_UPLOAD_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("file", filepath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + filepath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // tvFileName.setText("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadFile.this, "File Not Found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(UploadFile.this, "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UploadFile.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            //dialog.dismiss();

        }
    }

}
