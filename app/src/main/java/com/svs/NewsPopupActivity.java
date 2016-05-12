package com.svs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.svs.com.svs.helper.DBHelper;

import java.util.HashMap;

public class NewsPopupActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private WebView wvNews;
    ProgressBar pgrNews;
    ImageView imgClose;
    HashMap<String, Integer> userCounter;
    private SessionManager session;
    HashMap<String, String> userDetails;
    DBHelper db;
    SQLiteDatabase sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_news_popup);
            session = new SessionManager(getApplicationContext());
            userDetails = new HashMap<String, String>();
            userCounter = new HashMap<String, Integer>();
            userDetails = session.getUserDetails();
            wvNews = (WebView) findViewById(R.id.wvNews);
            db = new DBHelper(getApplication());
            sd = db.getWritableDatabase();
            sd = db.getReadableDatabase();
            // pgrNews=(ProgressBar)findViewById(R.id.pgrNews);
            imgClose = (ImageView) findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String qry = "select * from semester";
                        Cursor c = sd.rawQuery(qry, null);
                        if (c.getCount() <= 0) {
                            c.close();

                            Intent intent = new Intent(NewsPopupActivity.this, Settings.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            while (c.moveToNext()) {
                                String sem = c.getString(0);
                               // Toast.makeText(getApplication(), sem, Toast.LENGTH_LONG).show();
                            }

                            Intent intent = new Intent(NewsPopupActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }

                        c.close();
                        sd.close();
                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //pgrNews.setVisibility(View.GONE);
            wvNews.setVisibility(View.GONE);
            startWebView("http://logicupsolutions.com/svsalumni/api/getmessage.php");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void startWebView(String url) {

        // Create new webview Client to show progress dialog
        // When opening a url or click on link

        wvNews.setWebViewClient(new WebViewClient() {


            // If you will not use this method url links are opeen in new brower
            // not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            // Show loader on url load
            public void onLoadResource(WebView view, String url) {

            }

            public void onPageFinished(WebView view, String url) {
                try {
                    // pgrNews.setVisibility(View.GONE);

                    wvNews.setVisibility(View.VISIBLE);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        // Javascript inabled on webview
        wvNews.getSettings().setJavaScriptEnabled(true);

        wvNews.reload();
        wvNews.loadUrl(url);

    }

}
