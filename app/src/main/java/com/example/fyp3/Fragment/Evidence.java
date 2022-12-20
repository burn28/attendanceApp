package com.example.fyp3.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class Evidence extends Fragment {

    ImageView imageView;
    TextView weekText;
    String courseId;
    String studentId;
    String week;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_evidence, container, false);

        imageView = view.findViewById(R.id.imageView);

        SharedPreferences pref = getContext().getSharedPreferences("PROOF", Context.MODE_PRIVATE);
        week = pref.getString("week", null);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        courseId = preferences.getString("courseId", "");
        studentId = preferences.getString("studentId", "");

        View includeLayout = view.findViewById(R.id.included);
        weekText = includeLayout.findViewById(R.id.textWeek);
        weekText.setText("week "+week);
        getImage();


        return view;
    }

    public void getImage(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
        query.whereEqualTo("studentId", studentId);
        query.whereEqualTo("week", week);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    ParseFile evidenceFile = (ParseFile)object.get("evidence");
                    evidenceFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null){
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageView.setImageBitmap(Bitmap.createBitmap(bmp, 0,0, imageView.getWidth(), imageView.getHeight(), matrix,true));
                            }else {
                                Log.e("PIC","else 2");
                            }
                        }
                    });
                }else{
                    Log.e("PIC","else 1");
                }
            }
        });
        //        query.findInBackground(new FindCallback<ParseObject>() {
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
    }
}