package com.start.apps.pheezee.adapters;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import start.apps.pheezee.R;
import com.start.apps.pheezee.classes.SessionListClass;
import com.start.apps.pheezee.pojos.GetReportDataResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.WriteResponseBodyToDisk;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.start.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.start.apps.pheezee.activities.SessionReportActivity.patientName;
import static com.start.apps.pheezee.activities.SessionReportActivity.phizioemail;


public class OverallReportListArrayAdapter extends ArrayAdapter<SessionListClass> implements MqttSyncRepository.OnReportDataResponseListner {

    private TextView tv_exercise, tv_exercise_no,tv_download_date;
    private Button view_button;
    private ImageView share_icon;
    private ImageView bodypart_img;


    private Context context;
    public ArrayList<SessionListClass> mSessionArrayList;
    MqttSyncRepository repository;
    ProgressDialog report_dialog;

    public OverallReportListArrayAdapter(Context context, ArrayList<SessionListClass> mSessionArrayList, Application application){
        super(context, R.layout.overallreport_listview_model, mSessionArrayList);
        this.mSessionArrayList=mSessionArrayList;
        repository = new MqttSyncRepository(application);
        repository.setOnReportDataResponseListener(this);
        this.context = context;
    }


    public void updateList(ArrayList<SessionListClass> mSessionArrayList){
        this.mSessionArrayList.clear();
        this.mSessionArrayList.addAll(mSessionArrayList);
        this.notifyDataSetChanged();
    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.overallreport_listview_model, null);
//
//            holder = new ViewHolder();
//            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
//            holder.name.setChecked(true);
//            convertView.setTag(holder);
//
//            holder.name.setOnClickListener( new View.OnClickListener() {
//                public void onClick(View v) {
//                    CheckBox cb = (CheckBox) v ;
//                    SessionListClass session_list_element = (SessionListClass) cb.getTag();
//                    session_list_element.setSelected(cb.isChecked());
//                }
//            });
        }
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }


        SessionListClass selected_item = mSessionArrayList.get(position);

        tv_exercise_no = convertView.findViewById(R.id.tv_exercise_no);
        tv_exercise = convertView.findViewById(R.id.tv_exercise);
        view_button = convertView.findViewById(R.id.view_button);
        share_icon = convertView.findViewById(R.id.share_icon);
        tv_download_date = convertView.findViewById(R.id.tv_download_date);
        bodypart_img = convertView.findViewById(R.id.image_exercise);

        String feedback_image = mSessionArrayList.get(position).getBodypart().toLowerCase()+"_part_new";
        int res = context.getResources().getIdentifier(feedback_image, "drawable",context.getPackageName());

        if(res !=0) {
            bodypart_img.setImageResource(res);
        }

//        // Exercise
        tv_exercise.setText(mSessionArrayList.get(position).getBodypart());
        tv_exercise_no.setText(mSessionArrayList.get(position).getSession_time()+" sessions");


        // Downloaded date - Using muscle name data as a substitute
        if(mSessionArrayList.get(position).getMuscle_name() != null) {
            tv_download_date.setText("Last Viewed on "+mSessionArrayList.get(position).getMuscle_name());
            tv_download_date.setTextColor(context.getResources().getColor(R.color.pitch_black));
        }else
        {
            tv_download_date.setText("View report by downloading");
            tv_download_date.setTextColor(context.getResources().getColor(R.color.pitch_black));

        }

        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(context)) {
                    Calendar calendar = Calendar.getInstance();
                    String date = calenderToYYYMMDD(calendar);
                    getOverallReport(date, mSessionArrayList.get(position).getBodypart(),mSessionArrayList.get(position).getDownload_status());
                }
                else {
                    NetworkOperations.networkError(context);
                }



            }
        });

        share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(context)) {
                    Calendar calendar = Calendar.getInstance();
                    String date = calenderToYYYMMDD(calendar);
                    getOverallReportshare(date, mSessionArrayList.get(position).getBodypart(),context,mSessionArrayList.get(position).getDownload_status());
                }
                else {
                    NetworkOperations.networkError(context);
                }



            }
        });



        return convertView;

    }

    private String calenderToYYYMMDD(Calendar date){
        Date date_cal = date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date_cal);
        return strDate;
    }

    /**
     * Retrofit call to get the report pdf from the server
     * @param date
     */
    private void getOverallReport(String date,String bodypart, Boolean download_status){

        if(WriteResponseBodyToDisk.checkFileInDisk(patientName+ bodypart.toLowerCase()+"-overall") && !download_status)
        {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", WriteResponseBodyToDisk.GetFileFromDisk(patientName+ bodypart.toLowerCase()+"-overall")), "application/pdf");
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
            return;
        }

        String url = "/getreport/overall/"+patientId+"/"+phizioemail+"/" + date+"/" + bodypart.toLowerCase();
        report_dialog = new ProgressDialog(context);
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setMessage("Generating overall report held before "+date+", please wait..");
        report_dialog.show();
        repository.getDayReport(url,patientName+ bodypart.toLowerCase()+"-overall");
    }
    /**
     * Retrofit call to get the report pdf from the server
     * @param date
     */
    private void getOverallReportshare(String date,String bodypart,Context context, Boolean download_status){

        if(WriteResponseBodyToDisk.checkFileInDisk(patientName+ bodypart.toLowerCase()+"-overall") && !download_status)
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName() + ".my.package.name.provider", WriteResponseBodyToDisk.GetFileFromDisk(patientName+ bodypart.toLowerCase()+"-overall"));
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(shareIntent);
            return;
        }

        String url = "/getreport/overall/"+patientId+"/"+phizioemail+"/" + date+"/" + bodypart.toLowerCase();
        report_dialog = new ProgressDialog(context);
        report_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        report_dialog.setMessage("Sharing overall report held before "+date+", please wait..");
        report_dialog.show();
        repository.getDayReportshare(url,patientName+"-overall",context,report_dialog);
    }

    public void sendToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void sendShortToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReportDataReceived(GetReportDataResponse array, boolean response) {

    }

    @Override
    public void onDayReportReceived(File file, String message, Boolean response) {
        report_dialog.dismiss();
        if(response){
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".my.package.name.provider", file), "application/pdf");
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                context.startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
        else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }



}
