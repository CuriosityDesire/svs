package com.svs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.com.svs.helper.DBHelper;
import com.svs.com.svs.helper.NetConnectivity;
import com.svs.com.svs.helper.ServiceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    TextView lblSignUp;
    Button btnLogin;
    EditText txtHallTicketNo, txtMobile;
    private DBHelper db;
    String hall, mobile;
    LinearLayout loginlayout;
    private ProgressDialog pDialog;
    TextView lblforgotpass, lblSingup;
    private static String url;

    // JSON Node names
    private static final String TAG_STUDENT = "student";
    private static final String TAG_ID = "studentId";
    private static final String TAG_HALL = "hallTicketNo";
    private static final String TAG_NAME = "firstName";
    private static final String TAG_LASTNAME = "lastName";
    private static final String TAG_MOBILE = "mobile1";
    private static final String TAG_COURSEID = "courseId";
    // students JSONArray
    // Hashmap for ListView
    private SessionManager session;
    HashMap<String, String> userDetails;
    private String id, name, hallticket;
    private String dbEmail, dbName, dbId;
    ProgressBar pgrLogin;
    private String courseid;
    private String phone;
    private String lname,add,email;
    private String dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
        db = new DBHelper(getApplicationContext());

        loginlayout=(LinearLayout)findViewById(R.id.loginlayout);
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Check if user is already logged in or not

        lblforgotpass = (TextView) findViewById(R.id.lblforgotpass);
        pgrLogin = (ProgressBar) findViewById(R.id.pgrLogin);
        lblSignUp = (TextView) findViewById(R.id.lblSignUp);
        txtHallTicketNo = (EditText) findViewById(R.id.txtHallTicket);
        txtMobile = (EditText) findViewById(R.id.txtMobile);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hall = txtHallTicketNo.getText().toString();
                mobile = txtMobile.getText().toString();

                if (hall.equals(null) || hall.equals("")) {

                    Toast.makeText(getApplicationContext(),
                            "Please Enter HallTicket No", Toast.LENGTH_LONG).show();

                } else if (mobile.equals(null) || mobile.equals("")) {

                    Toast.makeText(getApplicationContext(),
                            "Please Enter Mobile No", Toast.LENGTH_LONG).show();
                } else {
                    if (NetConnectivity.isOnline(getApplicationContext())) {
                        new GetUserMasterDetailsJosn().execute();
                    } else {

                        Snackbar.make(loginlayout, "Please Check Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }


                }


            }
        });

        lblSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Login.this, RegisterActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });

    }
    private class GetUserMasterDetailsJosn extends
            AsyncTask<Void, Void, Void> {
        JSONArray userJson = null;
        boolean flag = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

/*            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Verifying...");
            pDialog.setCancelable(false);
            pDialog.show();*/

            txtHallTicketNo.setVisibility(View.GONE);
            txtMobile.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            lblforgotpass.setVisibility(View.GONE);
            lblSignUp.setVisibility(View.GONE);
            pgrLogin.setVisibility(View.VISIBLE);
           // phone=txtMobile.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            // deleteCategoryData();
            // deleteProductData();

            try {

                ServiceHandler
                        sh = new ServiceHandler();

                String jsonStr = sh.makeServiceCall(
                        "http://logicupsolutions.com/svsalumni/api/login.php?hno=" + hall + "&mno=" + mobile,
                        sh.GET);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        userJson = jsonObj.getJSONArray("student");

                        // looping through All Contacts
                        for (int i = 0; i < userJson.length(); i++) {
                            JSONObject c = userJson.getJSONObject(i);

                            id = c.getString("studentId");
                            name = c.getString("firstName");
                            lname = c.getString("lastName");
                            dob = c.getString("dob");
                            add = c.getString("address1");
                            hallticket = ""
                                    + c.getString("hallTicketNo");
                            courseid = c.getString("courseName");
                            email = c.getString("email1");
                            phone = c.getString("mobile1");
                            flag = true;
                            // session.ClearDetails();
                            //String ses =userDetails.get(SessionManager.KEY_STDID);
                            //session.createLoginSession(name, id);
                            // Create login session
                            session.setLogin(true);
                            session.setLoginSession(name,lname,id,courseid,hallticket,phone,email,add,dob);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            if (flag == true) {


                txtHallTicketNo.setText("");
                txtMobile.setText("");
                db.addUser(dbId, dbName, dbEmail);

                // Launch main activity
                Intent intent = new Intent(Login.this,
                        MainActivity.class);
                startActivity(intent);
                finish();

            } else {

                txtHallTicketNo.setText("");
                txtMobile.setText("");
                txtHallTicketNo.setVisibility(View.VISIBLE);
                txtMobile.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                lblforgotpass.setVisibility(View.VISIBLE);
                lblSignUp.setVisibility(View.VISIBLE);
                pgrLogin.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_LONG).show();

            }

            pgrLogin.setVisibility(View.GONE);
        }
    }


}
