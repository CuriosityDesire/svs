package com.svs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.com.svs.helper.NetConnectivity;
import com.svs.com.svs.helper.ServiceHandler;

import org.json.JSONArray;

import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    TextView lblSignIn;
    EditText txtUsernameRegister, txtEmailRegister, txtHallTicketRegister, txtMobileRegister;

    Button btnReg;

    LinearLayout registerlayout;
    private String regUsernameValue;
    private String regEmailValue;
    private String regHallValue;
    private String regMobileValue;
    Spinner spnCourses;
    private ProgressDialog pDialog;
    JSONArray operators = null;
    private String verificationTextGet, name, email;
    ServiceHandler sh;
    Boolean flag = false;
    int courseselectedid;
    private static final Pattern EMAIL_PATTERN = Pattern
            .compile("[a-zA-Z0-9+._%-+]{1,100}" + "@"
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,10}" + "(" + "."
                    + "[a-zA-Z0-9][a-zA-Z0-9-]{0,20}" + ")+");
    private static final Pattern USERNAME_PATTERN = Pattern
            .compile("[a-zA-Z0-9]{1,250}");
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("[a-zA-Z0-9+_.]{4,16}");
    private static final String REQUIRED_MSG = "required";
    private static final String VALID_EMAIL_MSG = "Enter Valid Email";
    private static final String VALID_PASSWORD_MSG = "Enter Valid Password";
    private String coourseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            // getSupportActionBar().hide();

            txtUsernameRegister = (EditText) findViewById(R.id.txtUsernameRegister);
            txtEmailRegister = (EditText) findViewById(R.id.txtEmailRegister);
            txtHallTicketRegister = (EditText) findViewById(R.id.txtHallTicketRegister);
            txtMobileRegister = (EditText) findViewById(R.id.txtMobileRegister);
            lblSignIn = (TextView) findViewById(R.id.lblSignIn);
            btnReg = (Button) findViewById(R.id.btnRegister);
            spnCourses = (Spinner) findViewById(R.id.spnCourse);
            registerlayout = (LinearLayout) findViewById(R.id.registerlayout);
            spnCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    coourseText = spnCourses.getSelectedItem().toString();
                    courseselectedid = spnCourses.getSelectedItemPosition() + 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    regUsernameValue = txtUsernameRegister.getText().toString();
                    regEmailValue = txtEmailRegister.getText().toString();
                    regHallValue = txtHallTicketRegister.getText().toString();
                    regMobileValue = txtMobileRegister.getText().toString();


                    if (regUsernameValue.length() == 0) {
                        // txtUsernameRegister.setError(REQUIRED_MSG);

                        Toast.makeText(RegisterActivity.this, "Username is required", Toast.LENGTH_LONG).show();
                    }

                    if (regEmailValue.length() == 0) {
                        //txtEmailRegister.setError(REQUIRED_MSG);
                        Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_LONG).show();

                    }
                    if (regHallValue.length() == 0) {
                        //txtHallTicketRegister.setError(REQUIRED_MSG);
                        Toast.makeText(RegisterActivity.this, "HallTicket No is required", Toast.LENGTH_LONG).show();

                    }
                    if (regMobileValue.length() == 0) {
                        //txtMobileRegister.setError(REQUIRED_MSG);
                        Toast.makeText(RegisterActivity.this, "Mobile is required", Toast.LENGTH_LONG).show();

                    }


                    //****************************


                    if (!regUsernameValue.equals("") && !regEmailValue.equals("")
                            && !regHallValue.equals("")
                            && !regMobileValue.equals("")) {

                        if (!CheckEmail(regEmailValue)) {
                            //  txtEmailRegister.setError(REQUIRED_MSG);
                            Toast.makeText(RegisterActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                        } else if (!CheckPassword(regHallValue)) {

                            Toast.makeText(RegisterActivity.this, "Invalid HallTicket No", Toast.LENGTH_LONG).show();
                        } else if (regMobileValue.length() <= 9) {

                            //txtMobileRegister.setError(REQUIRED_MSG);
                            Toast.makeText(RegisterActivity.this, "Invalid Mobile No", Toast.LENGTH_LONG).show();
                        } else {

                            if (NetConnectivity.isOnline(getApplicationContext())) {
                                txtUsernameRegister.setText("");
                                txtEmailRegister.setText("");
                                txtHallTicketRegister.setText("");
                                txtMobileRegister.setText("");

                                new SendingRegistrationDetails().execute();
                            } else {

                                String s = "Please Check Internet Connection";
                                Snackbar snack = Snackbar.make(registerlayout, s, Snackbar.LENGTH_LONG);
                                View view = snack.getView();
                                view.setBackgroundColor(Color.YELLOW);
                                snack.show();
                                //Snackbar.make(registerlayout, "Please Check Internet Connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }

                        }

                    }

                    //****************************


                }
            });
            lblSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(RegisterActivity.this, Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean CheckEmail(String email) {

        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean CheckPassword(String password) {

        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean CheckUsername(String username) {

        return USERNAME_PATTERN.matcher(username).matches();
    }


    // Sending Data
    private class SendingRegistrationDetails extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            pDialog = new ProgressDialog(RegisterActivity.this);
            pDialog.setMessage("Registering...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            // deleteCategoryData();
            // deleteProductData();

            try {

                String add = regHallValue + "&fname=" + regUsernameValue + "&mno=" + regMobileValue + "&email=" + regEmailValue + "&course=" + courseselectedid;

                add = add.replace(" ", "%20");
                //add = add.replace("&", "%26");
                add = add.replace("'", "%27");
                add = add.replace("(", "%28");
                add = add.replace(")", "%29");
                add = add.replace("-", "%2D");
                add = add.replace(".", "%2E");
                add = add.replace(":", "%3A");
                add = add.replace(";", "%3B");
                add = add.replace("?", "%3F");
                add = add.replace("@", "%40");
                add = add.replace("_", "%5F");

                String RegisUrl = "http://logicupsolutions.com/svsalumni/api/signup.php?hno="
                        + add;

                sh = new ServiceHandler();

                String jsonStrSendverifyCode = sh.makeServiceCall(RegisUrl,
                        ServiceHandler.GET);

                if (jsonStrSendverifyCode == null) {
                    flag = false;
                } else {
                    flag = true;
                }
                Log.d("User: ", "> " + jsonStrSendverifyCode);


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
                Toast.makeText(getApplicationContext(),
                        "Registered Sucessfully", Toast.LENGTH_LONG).show();
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Intent i = new Intent(RegisterActivity.this, Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        }

    }
}
