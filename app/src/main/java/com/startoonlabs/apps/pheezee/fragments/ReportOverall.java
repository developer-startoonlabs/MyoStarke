package com.startoonlabs.apps.pheezee.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.utils.WriteResponseBodyToDisk;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.phizioemail;


public class ReportOverall extends Fragment {
    private ProgressDialog report_dialog;
    public ReportOverall() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_overall_report, container, false);

        report_dialog = new ProgressDialog(getActivity());
        report_dialog.setMessage("Generating day report please wait....");
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setIndeterminate(true);
        TextView tv_overall_report = view.findViewById(R.id.fragment_overall_generate_report);


        tv_overall_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                String date = calenderToYYYMMDD(calendar);
                getOverallReport(date);
            }
        });
        return view;
    }


    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        Log.i("Date sent", strDate);
        return strDate;
    }


    private void getOverallReport(String date){
        GetDataService getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<ResponseBody> fileCall = getDataService.getReport("/getreport/overall/"+patientId+"/"+phizioemail+"/" + date);
        report_dialog.setMessage("Generating overall report report, please wait....");
        report_dialog.show();
        fileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                File file = WriteResponseBodyToDisk.writeResponseBodyToDisk(response.body(), patientName+"-monthly");
                if (file != null) {
                    report_dialog.dismiss();
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".my.package.name.provider", file), "application/pdf");
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    try {
                        startActivity(target);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}