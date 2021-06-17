package com.yogesh.appoinment.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.ReportInfo;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends ArrayAdapter {
    private Context context;
    private List<ReportInfo> list = new ArrayList<>();

    public static class ReportHolder {
        TextView txtReport, txtFile, textView;
    }

    public ReportAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    public void add(@Nullable ReportInfo object) {
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public ReportInfo getItem(int position) {
        return list.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        final ReportHolder holder;
        String userType = "";
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.single_report, parent, false);

            holder = new ReportHolder();
            holder.txtReport = (TextView) row.findViewById(R.id.txtReport);
            holder.txtFile = (TextView) row.findViewById(R.id.txtFile);
            holder.textView = (TextView) row.findViewById(R.id.imgView);

            row.setTag(holder);


        } else {
            holder = (ReportHolder) row.getTag();
        }

        try {
            final ReportInfo info = getItem(position);
            holder.txtFile.setText(info.getFilename());
            holder.txtReport.setText(info.getReport());

            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebActivity.url = ServerUtility.Server_URL + "images/reports/" + info.getFilename();
                    context.startActivity(new Intent(context, WebActivity.class));
//                    MyAsyncTask async = new MyAsyncTask(info.getFilename(),ServerUtility.Server_URL + "images/reports/" + info.getFilename());
//                    async.execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        String filename="";
        String path="";

        public MyAsyncTask(String filename, String path) {
            this.filename = filename;
            this.path = path;
        }



        @Override
        protected Void doInBackground(Void... voids) {
            DownloadFiles(filename,path);
            return null;
        }
    }
    public void DownloadFiles(String filename, String path){

        try {
            URL u = new URL(path);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "data/"+filename));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }

}
