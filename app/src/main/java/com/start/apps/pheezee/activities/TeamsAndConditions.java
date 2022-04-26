package com.start.apps.pheezee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import start.apps.pheezee.R;

public class TeamsAndConditions extends AppCompatActivity {
    PDFView mPDFView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_and_conditions);
        mPDFView = (PDFView) findViewById(R.id.pdfView);
        mPDFView.fromAsset("BugBook.pdf").load();

    }
}