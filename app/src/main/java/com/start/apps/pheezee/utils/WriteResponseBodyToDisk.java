package com.start.apps.pheezee.utils;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.airbnb.lottie.animation.content.Content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.ResponseBody;

public class WriteResponseBodyToDisk {


        public static File writeResponseBodyToDisk (ResponseBody body, String name){
            File reportPdf = null, file = null;
            try {
                // todo change the file location/name according to your needs
                reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Pheezee/files", "reports");
                if (!reportPdf.exists())
                    reportPdf.mkdirs();

                file = new File(reportPdf, name + ".pdf");
                if (!file.exists())
                    file.createNewFile();

                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    byte[] fileReader = new byte[4096];

                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;

                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(file);

                    while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        outputStream.write(fileReader, 0, read);

                        fileSizeDownloaded += read;
                    }

                    outputStream.flush();

                    return file;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        }

        public static File GetFileFromDisk (String name){
            File reportPdf = null, file = null;

            // todo change the file location/name according to your needs
            reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Pheezee/files", "reports");
            if (!reportPdf.exists())
                reportPdf.mkdirs();

            file = new File(reportPdf, name + ".pdf");


            return file;


        }


        public static boolean checkFileInDisk (String name){
            File reportPdf = null, file = null;

            // todo change the file location/name according to your needs
            reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Pheezee/files", "reports");
            if (!reportPdf.exists())
                reportPdf.mkdirs();

            file = new File(reportPdf, name + ".pdf");
            if (!file.exists()) {
                return false;
            } else {
                return true;
            }
        }

    }

     /*   File reportPdf, file=null;
        try {

            // todo change the file location/name according to your needs
           reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "Pheezee/files","reports");


            if(!reportPdf.exists())
                reportPdf.mkdirs();

            file = new File(reportPdf,name+".pdf");
            if(!file.exists())
                file.createNewFile();

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                }

                outputStream.flush();

                return file;
            }finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File GetFileFromDisk( String name) {
        File reportPdf=null, file=null;

        // todo change the file location/name according to your needs
        {
            reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "Pheezee/files", "reports");
        }
        if (!reportPdf.exists())
            reportPdf.mkdirs();

        file = new File(reportPdf, name + ".pdf");


        return file;


    }



    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean checkFileInDisk(String name) {
        File reportPdf=null, file=null;

        // todo change the file location/name according to your needs
        reportPdf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"Pheezee/files","reports");
        if(!reportPdf.exists())
            reportPdf.mkdirs();

        file = new File(reportPdf,name+".pdf");
        if(!file.exists())
        {
            return false;
        }else
        {
            return true;
        }
    }
}*/
