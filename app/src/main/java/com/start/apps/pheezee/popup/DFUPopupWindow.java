package com.start.apps.pheezee.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import start.apps.pheezee.R;
import com.start.apps.pheezee.dfu.DfuService;
import com.start.apps.pheezee.dfu.fragment.UploadCancelFragment;

public class DFUPopupWindow {

    public String TAG  = "DeviceInfoActivity";
    private Context context;
    private PopupWindow dfu_popup=null;
    View layout;
    boolean popup_created=false;
    boolean dfu_sucessful=false;

    private UploadCancelFragment.CancelFragmentListener mListener;

    public interface CancelFragmentListener {
        void onCancelUpload();
    }

    public DFUPopupWindow(Context context){
        this.context = context;
    }



    public void showWindow(){



        Configuration config = ((Activity)context).getResources().getConfiguration();

        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.dfu_popupwindow, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.dfu_popupwindow, null);
        }

        FrameLayout layout_MainMenu = (FrameLayout) layout.findViewById(R.id.dfu_frame);

        dfu_popup = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        dfu_popup.setWindowLayoutMode(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        dfu_popup.setOutsideTouchable(true);
        dfu_popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        popup_created = true;

        ImageView go_back = layout.findViewById(R.id.go_back);

        // On click on Continue
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dfuStatusDialog();


            }
        });

        dfu_popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // If not successful
                if(dfu_sucessful==false) {
                    showUploadCancelDialog();
                }
            }
        });



    }

    private void showToast(String nothing_selected) {
        Toast.makeText(context, nothing_selected, Toast.LENGTH_SHORT).show();
    }

    public void connecting()
    {
        // Checking if popup window is created
        if(dfu_popup==null) return;

        TextView dfu_text = layout.findViewById(R.id.ota_status_text);
        ImageView ota_status = layout.findViewById(R.id.ota_status);

        ota_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dfu_connecting));
        dfu_text.setText("Connecting to Pheezee");

    }

    public void updating(int progress_percentage)
    {
        // Checking if popup window is created
        if(dfu_popup==null) return;

        TextView dfu_text = layout.findViewById(R.id.ota_status_text);
        ProgressBar dfu_progress = layout.findViewById(R.id.ota_update_progress);
        TextView dfu_progress_text = layout.findViewById(R.id.dfu_progress_text);
        ImageView ota_status = layout.findViewById(R.id.ota_status);

        ota_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dfu_updating));

        dfu_progress.setVisibility(View.VISIBLE);
        dfu_progress_text.setVisibility(View.VISIBLE);


        dfu_progress.setProgress(progress_percentage);
        dfu_progress.setEnabled(false);
        dfu_text.setText("Pheezee updating");

        dfu_progress_text.setText(String.valueOf(progress_percentage)+"% Completed");

    }

    public void successfull(){

        // Checking if popup window is created
        if(dfu_popup==null) return;

        TextView dfu_text = layout.findViewById(R.id.ota_status_text);
        ImageView ota_status = layout.findViewById(R.id.ota_status);
        ProgressBar ota_loading = layout.findViewById(R.id.ota_loading);

        ota_loading.setVisibility(View.INVISIBLE);
        ota_status.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dfu_successful));
        dfu_text.setText("Successfully updated");
        dfu_sucessful=true;

    }

    public void dismiss()
    {
        // Checking if popup window is created
        if(dfu_popup==null) return;

        dfu_popup.dismiss();

    }

    private void showUploadCancelDialog() {

        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
        manager.sendBroadcast(pauseAction);

        try {
            mListener = (UploadCancelFragment.CancelFragmentListener) context;
        } catch (final ClassCastException e) {
            Log.d(TAG, "The parent Activity must implement CancelFragmentListener interface");
        }

        mListener.onCancelUpload();

    }

    public void dfuStatusDialog(){

        // Custom notification added by Haaris
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.notification_dialog_box);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView notification_title = dialog.findViewById(R.id.notification_box_title);
        TextView notification_message = dialog.findViewById(R.id.notification_box_message);

        Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
        Button notification_ButtonCancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

        Notification_Button_ok.setText("Yes");
        notification_ButtonCancel.setText("No");

        // Setting up the notification dialog
        notification_title.setText(R.string.dfu_confirmation_dialog_title);
        notification_message.setText(R.string.dfu_upload_dialog_cancel_message);

        // On click on Continue
        Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dfu_popup.dismiss();
                dialog.dismiss();

            }
        });
        // On click on Cancel
        notification_ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);

        // End


    }



}


