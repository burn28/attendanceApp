package com.example.fyp3.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp3.Adapter.StudentAttendanceAdp;
import com.example.fyp3.Adapter.StudentClassAdp;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    RecyclerView.LayoutManager linearLayoutManager;
    private StudentAttendanceAdp attendanceAdp;
    String day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        courses = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        classList = new ArrayList<>();
        attendanceAdp = new StudentAttendanceAdp(getContext(), classList);
        recyclerView.setAdapter(attendanceAdp);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

        showList();
//        StudentClass mClass = new StudentClass();
//        mClass.setCourseId("TRY2");
//        classList.add(mClass);
//        attendanceAdp.notifyDataSetChanged();
        return view;
    }

    private void showList(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrolment");
        query.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e ==  null){
                    courses.clear();
                    classList.clear();
                    for(ParseObject obj:objects){
                        courses.add(obj.getString("courseId"));
                    }
                    for(String course:courses){
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("course");
                        query.whereEqualTo("courseId", course);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null){
                                    for(ParseObject obj:objects){
                                        if(obj.getString("day").equals(day)){
                                            StudentClass mClass = new StudentClass();
                                            mClass.setCourseId(course);
                                            mClass.setTitle(obj.getString("title"));
                                            classList.add(mClass);
                                        }
                                        attendanceAdp.notifyDataSetChanged();
                                    }
                                }else{
                                    Log.d("QUERY2", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }

                }else{
                    Log.d("QUERY1", "Error: " + e.getMessage());
                }
            }
        });
    }
}
