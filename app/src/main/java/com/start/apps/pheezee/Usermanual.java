package com.start.apps.pheezee;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;

import start.apps.pheezee.R;

public class Usermanual extends AppCompatActivity {
    PDFView mPDFView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Screen Short
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_usermanual);
        mPDFView = (PDFView) findViewById(R.id.pdfView);
        mPDFView.fromAsset("BugBook.pdf").load();
    }
}