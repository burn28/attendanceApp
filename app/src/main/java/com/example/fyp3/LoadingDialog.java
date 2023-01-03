package com.example.fyp3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

public class LoadingDialog {

    Activity activity;
    AlertDialog dialog;
    Context context;

    public LoadingDialog(Context context){
        this.context = context;
    }

    public void startLoadingDialog(LayoutInflater inflater){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflater.inflate(R.layout.custom_loading,null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
