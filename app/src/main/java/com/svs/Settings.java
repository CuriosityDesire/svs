package com.svs;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.svs.com.svs.helper.DBHelper;

import java.util.HashMap;

public class Settings extends AppCompatActivity {
    private Toolbar mToolbar;
    Button btnOk;
    Spinner spnSemester;
    private String semesterText;
    private SessionManager session;
    HashMap<String, String> userDetails;
    DBHelper db;
    SQLiteDatabase sd;
    TextView lblStudName, lblCourseName, lblSemester, lblCourseNameValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");
        session = new SessionManager(getApplicationContext());
        userDetails = new HashMap<String, String>();
        userDetails = session.getUserDetails();
        btnOk = (Button) findViewById(R.id.btnOk);
        spnSemester = (Spinner) findViewById(R.id.spnSemester);
        lblStudName = (TextView) findViewById(R.id.lblStudentNameValue);
        lblSemester = (TextView) findViewById(R.id.lblSemesterNameValue);
        lblCourseNameValue = (TextView) findViewById(R.id.lblCourseNameValue);

        db = new DBHelper(getApplication());
        sd = db.getReadableDatabase();
        sd = db.getWritableDatabase();
        lblStudName.setText(userDetails.get(SessionManager.KEY_NAME) + " " + userDetails.get(SessionManager.KEY_LNAME));
        lblCourseNameValue.setText(userDetails.get(SessionManager.KEY_COURSE_ID));
        //lblCourseName.setText(userDetails.get(SessionManager.KEY_NAME));

        lblSemester.setText("Sem " + userDetails.get(SessionManager.KEY_SEM));
        spnSemester.setSelection(Integer.parseInt(userDetails.get(SessionManager.KEY_SEM)) - 1);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.setSemester(semesterText);

                insertSemester();
                Intent i = new Intent(Settings.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        spnSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                semesterText = spnSemester.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void insertSemester() {

        try {
            sd.delete("semester", null, null);
            sd.execSQL("INSERT INTO semester VALUES('" + semesterText + "') ");

            sd.close();
            db.close();
        } catch (SQLException e) {
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
}
