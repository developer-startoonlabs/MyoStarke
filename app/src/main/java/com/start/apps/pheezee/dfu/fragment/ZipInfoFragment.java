package com.start.apps.pheezee.dfu.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import start.apps.pheezee.R;


public class ZipInfoFragment extends DialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zip_info, null);
        return new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.dfu_file_info).setPositiveButton(R.string.ok, null).create();
    }
}