package com.example.fyp3.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.Model.TableClass;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class StudentTimetable extends Fragment {

    private List<String> courseList;
    private List<TableClass> tableClassList;
    private TextView Monday1, Monday2, Monday3, Monday4, Monday5, Monday6, Monday7, Monday8, Monday9,
            Tuesday1, Tuesday2, Tuesday3, Tuesday4, Tuesday5, Tuesday6, Tuesday7, Tuesday8, Tuesday9,
            Wednesday1, Wednesday2, Wednesday3, Wednesday4, Wednesday5, Wednesday6, Wednesday7, Wednesday8, Wednesday9,
            Thursday1, Thursday2, Thursday3, Thursday4, Thursday5, Thursday6, Thursday7, Thursday8, Thursday9,
            Friday1, Friday2, Friday3, Friday4, Friday5, Friday6, Friday7, Friday8, Friday9;

    private List<TextView> mondayList;
    private List<TextView> tuesdayList;
    private List<TextView> wednesdayList;
    private List<TextView> thursdayList;
    private List<TextView> fridayList;
    private HashMap<String,String> colorMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_timetable, container, false);

        courseList = new ArrayList<>();
        tableClassList = new ArrayList<>();
        mondayList = new ArrayList<>();
        tuesdayList = new ArrayList<>();
        wednesdayList = new ArrayList<>();
        thursdayList = new ArrayList<>();
        fridayList = new ArrayList<>();
        colorMap = new HashMap<>();

        Monday1 = view.findViewById(R.id.Monday1);
        Monday2 = view.findViewById(R.id.Monday2);
        Monday3 = view.findViewById(R.id.Monday3);
        Monday4 = view.findViewById(R.id.Monday4);
        Monday5 = view.findViewById(R.id.Monday5);
        Monday6 = view.findViewById(R.id.Monday6);
        Monday7 = view.findViewById(R.id.Monday7);
        Monday8 = view.findViewById(R.id.Monday8);
        Monday9 = view.findViewById(R.id.Monday9);


        mondayList.add(Monday1);
        mondayList.add(Monday2);
        mondayList.add(Monday3);
        mondayList.add(Monday4);
        mondayList.add(Monday5);
        mondayList.add(Monday6);
        mondayList.add(Monday7);
        mondayList.add(Monday8);
        mondayList.add(Monday9);


        Tuesday1 = view.findViewById(R.id.Tuesday1);
        Tuesday2 = view.findViewById(R.id.Tuesday2);
        Tuesday3 = view.findViewById(R.id.Tuesday3);
        Tuesday4 = view.findViewById(R.id.Tuesday4);
        Tuesday5 = view.findViewById(R.id.Tuesday5);
        Tuesday6 = view.findViewById(R.id.Tuesday6);
        Tuesday7 = view.findViewById(R.id.Tuesday7);
        Tuesday8 = view.findViewById(R.id.Tuesday8);
        Tuesday9 = view.findViewById(R.id.Tuesday9);


        tuesdayList.add(Tuesday1);
        tuesdayList.add(Tuesday2);
        tuesdayList.add(Tuesday3);
        tuesdayList.add(Tuesday4);
        tuesdayList.add(Tuesday5);
        tuesdayList.add(Tuesday6);
        tuesdayList.add(Tuesday7);
        tuesdayList.add(Tuesday8);
        tuesdayList.add(Tuesday9);


        Wednesday1 = view.findViewById(R.id.Wednesday1);
        Wednesday2 = view.findViewById(R.id.Wednesday2);
        Wednesday3 = view.findViewById(R.id.Wednesday3);
        Wednesday4 = view.findViewById(R.id.Wednesday4);
        Wednesday5 = view.findViewById(R.id.Wednesday5);
        Wednesday6 = view.findViewById(R.id.Wednesday6);
        Wednesday7 = view.findViewById(R.id.Wednesday7);
        Wednesday8 = view.findViewById(R.id.Wednesday8);
        Wednesday9 = view.findViewById(R.id.Wednesday9);


        wednesdayList.add(Wednesday1);
        wednesdayList.add(Wednesday2);
        wednesdayList.add(Wednesday3);
        wednesdayList.add(Wednesday4);
        wednesdayList.add(Wednesday5);
        wednesdayList.add(Wednesday6);
        wednesdayList.add(Wednesday7);
        wednesdayList.add(Wednesday8);
        wednesdayList.add(Wednesday9);


        Thursday1 = view.findViewById(R.id.Thursday1);
        Thursday2 = view.findViewById(R.id.Thursday2);
        Thursday3 = view.findViewById(R.id.Thursday3);
        Thursday4 = view.findViewById(R.id.Thursday4);
        Thursday5 = view.findViewById(R.id.Thursday5);
        Thursday6 = view.findViewById(R.id.Thursday6);
        Thursday7 = view.findViewById(R.id.Thursday7);
        Thursday8 = view.findViewById(R.id.Thursday8);
        Thursday9 = view.findViewById(R.id.Thursday9);


        thursdayList.add(Thursday1);
        thursdayList.add(Thursday2);
        thursdayList.add(Thursday3);
        thursdayList.add(Thursday4);
        thursdayList.add(Thursday5);
        thursdayList.add(Thursday6);
        thursdayList.add(Thursday7);
        thursdayList.add(Thursday8);
        thursdayList.add(Thursday9);


        Friday1 = view.findViewById(R.id.Friday1);
        Friday2 = view.findViewById(R.id.Friday2);
        Friday3 = view.findViewById(R.id.Friday3);
        Friday4 = view.findViewById(R.id.Friday4);
        Friday5 = view.findViewById(R.id.Friday5);
        Friday6 = view.findViewById(R.id.Friday6);
        Friday7 = view.findViewById(R.id.Friday7);
        Friday8 = view.findViewById(R.id.Friday8);
        Friday9 = view.findViewById(R.id.Friday9);


        fridayList.add(Friday1);
        fridayList.add(Friday2);
        fridayList.add(Friday3);
        fridayList.add(Friday4);
        fridayList.add(Friday5);
        fridayList.add(Friday6);
        fridayList.add(Friday7);
        fridayList.add(Friday8);
        fridayList.add(Friday9);

        colorMap.put("SKJ4272","#9033CC");
        colorMap.put("QQQ2111","#4378DB");
        colorMap.put("SKJ4213","#d13a2e");
        colorMap.put("UTU3012","#F2BF40");
        colorMap.put("SKJ4183","#21de80");
        colorMap.put("SKM3013","#DA9725");

        getEnrollment();
        return view;
    }

    public void getEnrollment() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrolment");
        query.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    tableClassList.clear();
                    for (ParseObject obj : objects) {
                        TableClass tClass = new TableClass();
                        tClass.setCourseId(obj.getString("courseId"));
                        tableClassList.add(tClass);
                    }
                    for (TableClass course : tableClassList) {
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("course");
                        query2.whereEqualTo("courseId", course.getCourseId());
                        query2.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : objects) {
                                        course.setTitle(obj.getString("title"));
                                        course.setDay(obj.getString("day"));
                                        course.setStartTime(obj.getString("startTime"));
                                        course.setEndTime(obj.getString("endTime"));
                                        Log.d("TABLE", course.getCourseId());
                                        int diff = 0;
                                        int index = 0;
                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                                            Date date1 = format.parse(course.getEndTime());
                                            Date date2 = format.parse(course.getStartTime());
                                            Log.d("END", "D:"+date1.getTime());
                                            long mills = date1.getTime() - date2.getTime();
                                            diff = (int) (mills / (1000 * 60 * 60));
                                            Date date3 = format.parse(course.getStartTime());
                                            Date date4 = format.parse("08:00");
                                            long mills2 = date3.getTime() - date4.getTime();
                                            index = (int) (mills2 / (1000 * 60 * 60));
//                                            Toast.makeText(getContext(), diff, Toast.LENGTH_SHORT).show();

                                        } catch (Exception error) {
                                            error.printStackTrace();
                                        }
                                        //monday class
                                        if (course.getDay().equals("Monday")) {
                                            Log.d("TABLE I", course.getCourseId());
                                            Log.d("DIFF", "D:"+diff);
                                            for(int i=0; i<diff;i++){
                                                mondayList.get(index).setText(course.getCourseId());
                                                mondayList.get(index).setBackgroundColor(Color.parseColor(colorMap.get(course.getCourseId())));
                                                index++;
                                                Log.d("index", "index"+index);
                                            }


                                        }
                                        //tuesday clas
                                        if (course.getDay().equals("Tuesday")) {
                                            Log.d("TABLE I", course.getCourseId());
                                            Log.d("DIFF", "D:"+diff);
                                            for(int i=0; i<diff;i++){
                                                tuesdayList.get(index).setText(course.getCourseId());
                                                tuesdayList.get(index).setBackgroundColor(Color.parseColor(colorMap.get(course.getCourseId())));
                                                index++;
                                                Log.d("index", "index"+index);
                                            }

                                        }
                                        //wednesday class
                                        if (course.getDay().equals("Wednesday")) {
                                            Log.d("TABLE I", course.getCourseId());
                                            Log.d("DIFF", "D:"+diff);
                                            for(int i=0; i<diff;i++){
                                                wednesdayList.get(index).setText(course.getCourseId());
                                                wednesdayList.get(index).setBackgroundColor(Color.parseColor(colorMap.get(course.getCourseId())));
                                                index++;
                                                Log.d("index", "index"+index);
                                            }

                                        }
                                        //thursday class
                                        if (course.getDay().equals("Thursday")) {
                                            Log.d("TABLE I", course.getCourseId());
                                            Log.d("DIFF", "D:"+diff);
                                            for(int i=0; i<diff;i++){
                                                thursdayList.get(index).setText(course.getCourseId());
                                                thursdayList.get(index).setBackgroundColor(Color.parseColor(colorMap.get(course.getCourseId())));
                                                index++;
                                                Log.d("index", "index"+index);
                                            }

                                        }
                                        //friday class
                                        if (course.getDay().equals("Friday")) {
                                            Log.d("TABLE I", course.getCourseId());
                                            Log.d("DIFF", "D:"+diff);
                                            for(int i=0; i<diff;i++){
                                                fridayList.get(index).setText(course.getCourseId());
                                                fridayList.get(index).setBackgroundColor(Color.parseColor(colorMap.get(course.getCourseId())));
                                                index++;
                                                Log.d("index", "index"+index);
                                            }

                                        }


                                    }
                                } else {
                                    Log.d("INNER", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }
//                    for (TableClass tClass : tableClassList) {
//                        String day = tClass.getDay();
//                        Log.e("TABLE",day);
//                        Toast.makeText(getContext(), day, Toast.LENGTH_SHORT).show();
////                        if (tClass.getDay().equals("Monday")) {
//                            int diff = 0;
//                            try {
//                                SimpleDateFormat format = new SimpleDateFormat("hh:mm");
//                                Date date1 = format.parse(tClass.getEndTime());
//                                Date date2 = format.parse("07:00");
//                                long mills = date1.getTime() - date2.getTime();
//                                diff = (int) (mills / (1000 * 60 * 60));
//                                Toast.makeText(getContext(), diff, Toast.LENGTH_SHORT).show();
//                            } catch (Exception error) {
//                                error.printStackTrace();
//                            }
//                            if (tClass.getDay().equals("Monday")) {
//                                Toast.makeText(getContext(), tClass.getTitle(), Toast.LENGTH_SHORT).show();
//                                mondayList.get(diff).setText(tClass.getCourseId());
//                            }
////                            for(int i=1; i<=diff; i++){
////
////                            }
////                        }
//                    }

                } else {
                    Log.d("Table", "Error: " + e.getMessage());
                }
            }
        });


    }

}