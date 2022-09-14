package com.example.fyp3.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    RecyclerView.LayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

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
                if(e == null){
                    courseList.clear();
                    for(ParseObject object:objects){
                        StudentClass studentCourse = new StudentClass();
                        studentCourse.setCourseId(object.getString("courseId"));
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("course");
                        query2.whereEqualTo("courseId",studentCourse.getCourseId());
                        query2.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if(e == null){
                                    studentCourse.setTitle(object.getString("title"));
                                    courseList.add(studentCourse);
                                    courseAdp.notifyDataSetChanged();
                                }else {
                                    Log.d("INNER", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }

                }else {
                    Log.d("OUTER", "Error: " + e.getMessage());
                }

            }
        });
    }
}