package com.example.sai.pheezeeapp.DFU.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.sai.pheezeeapp.R;


public class ZipInfoFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zip_info, null);
        return new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.dfu_file_info).setPositiveButton(R.string.ok, null).create();
    }
}