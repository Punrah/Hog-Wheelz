package com.hogwheelz.driverapps.activity.asynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Startup on 4/8/17.
 */

public abstract class MyAsyncTask extends AsyncTask<Void,Void,Void> {

    public Boolean isSucces=false;
    public String emsg;
    public String smsg;
    public String status;
    public ProgressDialog asyncDialog =new ProgressDialog(getContext());

    AlertDialog.Builder alert;

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setPreloading();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        setPostLoading();
        if(isSucces) {
            setSuccesPostExecute();
        }
        else
        {
            Toast.makeText(getContext(), emsg, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract Context getContext();


    public abstract void setSuccesPostExecute();

    public void setPreloading()
    {
        asyncDialog.setMessage("Please wait...");
        asyncDialog.show();
    }
    public void setPostLoading()
    {
        asyncDialog.dismiss();
    }

    public void setAlert()
    {
        alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Upload Success");
        alert.setMessage(smsg);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ((Activity)(getContext())).finish();
            }
        });
        alert.show();
    }




}
