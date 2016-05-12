package com.svs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class DocDetailsActivity extends AppCompatActivity {

    String mainfile, docfile, imgfile;
    private WebView webView;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webDocFileView);

        Intent i = getIntent();
        docfile = i.getStringExtra("DOCLINK");

        String link = "https://docs.google.com/viewer?url=" + docfile;
    }
}
