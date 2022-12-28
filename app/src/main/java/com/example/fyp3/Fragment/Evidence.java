package com.example.fyp3.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Evidence extends Fragment {

    TextView reasonText;
    TextView weekText;
    TextView download;
    String courseId;
    String studentId;
    String week;
    String extension;
    Button delBtn;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evidence, container, false);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);


        download = view.findViewById(R.id.textView2);
        reasonText = view.findViewById(R.id.textView3);
        delBtn = view.findViewById(R.id.delBtn);

        SharedPreferences pref = getContext().getSharedPreferences("PROOF", Context.MODE_PRIVATE);
        week = pref.getString("week", null);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        courseId = preferences.getString("courseId", "");
        studentId = preferences.getString("studentId", "");

        View includeLayout = view.findViewById(R.id.included);
        weekText = includeLayout.findViewById(R.id.textWeek);
        weekText.setText("week "+week);

        download.setVisibility(View.GONE);
        getReason();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                    Log.e("STORAGE", "Storage not available or read only");
                }else{
                    getFile();
                }
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tickAttend();
            }
        });

        return view;
    }

    public void getReason(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
        query.whereEqualTo("studentId", studentId);
        query.whereEqualTo("week", week);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                String reason = object.getString("reason");
                if(reason!=null){
                    download.setVisibility(View.GONE);
                    reasonText.setText(reason);
                }else{
                    download.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }



    public void getFile(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
        query.whereEqualTo("studentId", studentId);
        query.whereEqualTo("week", week);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    ParseFile file = (ParseFile) object.get("evidence");
                    if(file == null){
                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Log.e("GETFILE","DONE");
                            saveFile(data,studentId+"_"+"week"+week);
                        }
                    });

                     extension = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                    Log.e("URL", extension);
                }
            }
        });
    }

    public void saveFile(byte[] data, String filename){
        Log.e("SAVEFILE","masuk");
        File direct = new File(Environment.getExternalStorageDirectory()+"/Download/Student Attendance");
        if(!direct.exists()) {
            if(direct.mkdir()){
                //directory is created;
                Log.e("FILE","MKDIR");
            }else{
                try {
                    //create dir
                    Files.createDirectory(direct.toPath());
                } catch (IOException e) {
                    Log.e("FILE","EXCEPTION");
                    e.printStackTrace();Log.e("FILE","RETURN");
                    return;
                }
            }
        }
        File file = new File(direct, filename+"."+extension);
        try{
            //save file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            Toast.makeText(getContext(), "File saved successfully", Toast.LENGTH_SHORT).show();
            Log.e("FILE","COMPLETE");
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("File could not be saved.", e);
        }
    }

    public void tickAttend(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
        query.whereEqualTo("studentId", studentId);
        query.whereEqualTo("week", week);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                object.deleteInBackground();
                Toast.makeText(getContext(), "Change record successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });

    }



//    public void getImage(){
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
//        query.whereEqualTo("studentId", studentId);
//        query.whereEqualTo("week", week);
//
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if(e == null){
//                    ParseFile evidenceFile = (ParseFile)object.get("evidence");
//                    evidenceFile.getDataInBackground(new GetDataCallback() {
//                        @Override
//                        public void done(byte[] data, ParseException e) {
//                            if(e == null){
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(90);
//                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                imageView.setImageBitmap(Bitmap.createBitmap(bmp, 0,0, imageView.getWidth(), imageView.getHeight(), matrix,true));
//                            }else {
//                                Log.e("PIC","else 2");
//                            }
//                        }
//                    });
//                }else{
//                    Log.e("PIC","else 1");
//                }
//            }
//        });
//                query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null) {
//                    for (ParseObject obj : objects) {
//                        ParseFile applicantResume = (ParseFile)obj.get("evidence");
//                    }
//                } else {
//
//                }
//            }
//        });
//    }
}