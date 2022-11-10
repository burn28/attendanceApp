package com.example.fyp3.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Adapter.StudentAttendanceAdp;
import com.example.fyp3.Adapter.StudentClassAdp;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentAttendance extends Fragment {

    private List<String> courses;
    private RecyclerView recyclerView;
    private List<StudentClass> classList;
    private List<StudentClass> doneList;
    RecyclerView.LayoutManager linearLayoutManager;
    private StudentAttendanceAdp attendanceAdp;
    String day;
    String week;
    private TextView weekText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        week = pref.getString("week", "1");
        String dWeek = "Week "+ week;
        weekText = view.findViewById(R.id.textWeek);
        weekText.setText(dWeek);

        SharedPreferences.Editor editor = pref.edit();


        courses = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);


        classList = new ArrayList<>();
        attendanceAdp = new StudentAttendanceAdp(getContext(), classList, StudentAttendance.this);
        recyclerView.setAdapter(attendanceAdp);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
//        day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        day = "Wednesday";


        showList();

        return view;
    }

    private void showList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrolment");
        query.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    courses.clear();
                    classList.clear();
                    for (ParseObject obj : objects) {
                        courses.add(obj.getString("courseId"));
                    }
                    for (String course : courses) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("course");
                        query.whereEqualTo("courseId", course);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : objects) {
                                        if (obj.getString("day").equals(day)) {
                                            StudentClass mClass = new StudentClass();
                                            mClass.setCourseId(course);
                                            mClass.setTitle(obj.getString("title"));
                                            mClass.setDay(day);
                                            classList.add(mClass);
//                                            Toast.makeText(getContext(), course, Toast.LENGTH_SHORT).show();
                                        }

                                        attendanceAdp.notifyDataSetChanged();
                                    }
                                    SharedPreferences pref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    Gson gson = new Gson();
                                    String json = pref.getString("done_class", null);
                                    Type type = new TypeToken<ArrayList<StudentClass>>() {
                                    }.getType();

                                    doneList = gson.fromJson(json, type);


                                    if (doneList == null) {
                                        doneList = new ArrayList<>();
                                        attendanceAdp.notifyDataSetChanged();
                                    } else if (!doneList.isEmpty() && !doneList.get(0).getDay().equals(day)) {
                                        editor.remove("done_class");
                                        editor.apply();
                                        doneList = null;
                                        Toast.makeText(getContext(), "DELETE", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (int i = 0; i < classList.size(); i++) {
                                            for (int j = 0; j < doneList.size(); j++) {
                                                if (classList.get(i).getCourseId().equals(doneList.get(j).getCourseId())) {
                                                    classList.remove(i);
                                                    attendanceAdp.notifyDataSetChanged();
                                                    if (classList.size() == 0) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
//                                        Toast.makeText(getContext(), doneList.get(0).getCourseId(), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), doneList.get(0).getDay(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("QUERY2", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }

                } else {
                    Log.d("QUERY1", "Error: " + e.getMessage());
                }
            }
        });


    }

    public void recordAttendance() {
        SharedPreferences pref = getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String action = pref.getString("record", null);
        String courseId = pref.getString("courseId", null);
        String courseTitle = pref.getString("courseTitle", null);

        Gson gson = new Gson();
        StudentClass Class = new StudentClass();
        Class.setCourseId(courseId);
        Class.setTitle(courseTitle);
        Class.setDay(day);
        doneList.add(Class);
        String json = gson.toJson(doneList);
        editor.putString("done_class", json);
        editor.apply();

        if(action.equals("absent")){
            ParseObject parseObject = new ParseObject(courseId);
            parseObject.put("studentId",ParseUser.getCurrentUser().getUsername());
            parseObject.put("week", week);
            parseObject.saveInBackground();
        }
        showList();
//        for(int i=0; i<classList.size(); i++){
//            if(classList.get(i).getCourseId().equals(courseId)){
//                classList.remove(i);
//                attendanceAdp.notifyDataSetChanged();
//            }
//        }
    }
}
