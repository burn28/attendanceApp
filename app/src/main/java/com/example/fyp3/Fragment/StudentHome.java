package com.example.fyp3.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Adapter.LecturerClassAdp;
import com.example.fyp3.Adapter.StudentClassAdp;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentHome extends Fragment {

    private RecyclerView recyclerView;
    private StudentClassAdp courseAdp;
    private List<StudentClass> courseList;
    private TextView weekText;
    RecyclerView.LayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        String week = "Week "+pref.getString("week", "1");
        weekText = view.findViewById(R.id.textWeek);
        weekText.setText(week);

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        courseList = new ArrayList<>();
        courseAdp = new StudentClassAdp(getContext(), courseList);
        recyclerView.setAdapter(courseAdp);

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
                    courseList.clear();
                    for (ParseObject object : objects) {
                        StudentClass studentCourse = new StudentClass();
                        studentCourse.setCourseId(object.getString("courseId"));
                        courseList.add(studentCourse);
                    }
                    for (StudentClass course : courseList) {
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("course");
                        query2.whereEqualTo("courseId", course.getCourseId());
                        query2.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject obj : objects) {
                                        course.setTitle(obj.getString("title"));
                                    }
                                    courseAdp.notifyDataSetChanged();
                                } else {
                                    Log.d("INNER", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }
                    for (StudentClass course : courseList) {
                        ParseQuery<ParseObject> query3 = ParseQuery.getQuery(course.getCourseId());
                        query3.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
                        query3.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (!objects.isEmpty()) {
                                        for (ParseObject obj : objects) {
                                            course.setWeeks(obj.getString("week"));
                                            course.setTotalWeek(course.getTotalWeek() + 1);
                                        }
                                        double percentage = (14d - course.getTotalWeek()) * 100 / 14;
                                        int result = (int) Math.ceil(percentage);
                                        course.setPercentage(result);
                                    }

                                    courseAdp.notifyDataSetChanged();
                                } else {
                                    Log.d("QUERY3", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }

                } else {
                    Log.d("OUTER", "Error: " + e.getMessage());
                }

            }
        });
    }
}
