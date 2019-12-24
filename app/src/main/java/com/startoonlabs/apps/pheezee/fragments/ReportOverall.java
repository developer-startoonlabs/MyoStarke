package com.startoonlabs.apps.pheezee.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.SessionReportActivity;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;

import org.json.JSONArray;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.phizioemail;


public class ReportOverall extends Fragment implements MqttSyncRepository.OnReportDataResponseListner {
    private ProgressDialog report_dialog;
    MqttSyncRepository repository;
    public ReportOverall() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_overall_report, container, false);

        report_dialog = new ProgressDialog(getActivity());
        report_dialog.setMessage("Generating overall report please wait....");
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setIndeterminate(true);
        TextView tv_overall_report = view.findViewById(R.id.fragment_overall_generate_report);
        repository = new MqttSyncRepository(getActivity().getApplication());
        repository.setOnReportDataResponseListener(this);

        JSONArray array = ((SessionReportActivity)getActivity()).getSessions();
        if(array==null || array.length()<=0){
            tv_overall_report.setText("No sessions done");
        }
        tv_overall_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(array==null || array.length()<=0){
                    Toast.makeText(getActivity(), "No sessions done", Toast.LENGTH_SHORT).show();
                }else {
                    Calendar calendar = Calendar.getInstance();
                    String date = calenderToYYYMMDD(calendar);
                    getOverallReport(date);
                }
            }
        });
        return view;
    }


    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        return strDate;
    }


    private void getOverallReport(String date){
        String url = "/getreport/overall/"+patientId+"/"+phizioemail+"/" + date;
        report_dialog.setMessage("Generating overall report for all the sessions held before "+date+", please wait....");
        report_dialog.show();
        repository.getDayReport(url,patientName+"-overall");
    }

    @Override
    public void onReportDataReceived(JSONArray array, boolean response) {

    }

    @Override
    public void onDayReportReceived(File file, String message, Boolean response) {
        report_dialog.dismiss();
        if(response){
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
        else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        repository.disableReportDataListner();
        super.onDestroy();
    }
}