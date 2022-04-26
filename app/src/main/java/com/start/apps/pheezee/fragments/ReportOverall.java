package com.start.apps.pheezee.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.SessionReportActivity;
import com.start.apps.pheezee.pojos.GetReportDataResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.start.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.start.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;


public class ReportOverall extends Fragment implements MqttSyncRepository.OnReportDataResponseListner {
    private ProgressDialog report_dialog;
    MqttSyncRepository repository;
    public ReportOverall() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String[] record = {""};

        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_overall_report, container, false);
        Spinner overall_spinner = (Spinner)view.findViewById(R.id.spinner_bodypart);

        report_dialog = new ProgressDialog(getActivity());
        report_dialog.setMessage("Generating overall report please wait....");
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setIndeterminate(true);
        TextView tv_overall_report = view.findViewById(R.id.fragment_overall_generate_report);
        repository = new MqttSyncRepository(getActivity().getApplication());
        repository.setOnReportDataResponseListener(this);

        GetReportDataResponse array = ((SessionReportActivity)getActivity()).getSessions();
        if(array==null){
            tv_overall_report.setText("No sessions done");
        }

        // Added by Haaris 30/6/2020
        overall_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //use postion value

                switch (position)

                {

                    case 0:
                        record[0] = "elbow";
                        break;
                    case 1:
                        record[0] = "knee";
                        break;
                    case 2:
                        record[0] = "ankle";
                        break;
                    case 3:
                        record[0] = "hip";
                        break;
                    case 4:
                        record[0] = "wrist";
                        break;
                    case 5:
                        record[0] = "shoulder";
                        break;
                    case 6:
                        record[0] = "forearm";
                        break;
                    case 7:
                        record[0] = "spine";
                        break;
                    case 8:
                        record[0] = "abdomen";
                        break;
                }

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        tv_overall_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(array==null ){
                    Toast.makeText(getActivity(), "No sessions done", Toast.LENGTH_SHORT).show();
                }else {
                    Calendar calendar = Calendar.getInstance();
                    String date = calenderToYYYMMDD(calendar);
                    getOverallReport(date,record[0]);
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


    private void getOverallReport(String date,String bodypart){
        String url = "/getreport/overall/"+patientId+"/"+phizioemail+"/" + date+"/" + bodypart;
        report_dialog.setMessage("Generating overall report for all the sessions held before "+date+", please wait....");
        report_dialog.show();
        repository.getDayReport(url,patientName+"-overall");
    }

    @Override
    public void onReportDataReceived(GetReportDataResponse array, boolean response) {

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