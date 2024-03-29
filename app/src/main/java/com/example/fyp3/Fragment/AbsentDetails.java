package com.example.fyp3.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Adapter.AbsentWeekAdp;
import com.example.fyp3.Adapter.LecturerAttendanceAdp;
import com.example.fyp3.Model.StudentClass;
import com.example.fyp3.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AbsentDetails extends Fragment {

    String studentName, studentId, courseId, courseTitle;
    int percentage;
    TextView studentIdText, studentNameText, percentageText, courseText;
    private CardView cardView;
    public List<String> weekList;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager linearLayoutManager;
    private com.example.fyp3.Adapter.AbsentWeekAdp absentWeekAdp;
    String currentWeek;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_absent_details, container, false);


        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        studentId = pref.getString("studentId", null);
        studentName = pref.getString("studentName", null);
        percentage = pref.getInt("percentage", -1);
        courseId = pref.getString("courseId", "");
        courseTitle = pref.getString("title", "");
        courseText = view.findViewById(R.id.course);
        courseText.setText(courseTitle);

        SharedPreferences pref2 = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        currentWeek = pref2.getString("week", "1");

        View includeLayout = view.findViewById(R.id.included);
        studentIdText = includeLayout.findViewById(R.id.studentId);
        studentNameText = includeLayout.findViewById(R.id.studentName);
        percentageText = includeLayout.findViewById(R.id.percentage);
        cardView = includeLayout.findViewById(R.id.cardview);
        includeLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.anim_fall_down));
//        cardView.startAnimation(AnimationUtils.loadAnimation(this.getContext(),R.anim.anim_fall_down));

        studentIdText.setText(studentId);
        studentNameText.setText(studentName);
        percentageText.setText(percentage + "%");

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        weekList = new ArrayList<>();
        absentWeekAdp = new AbsentWeekAdp(getContext(), weekList);
        recyclerView.setAdapter(absentWeekAdp);

        showList();
        return view;
    }

    public void showList() {
        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        if (this.getActivity().getClass().getSimpleName().equals("StudentActivity")) {
            weekList.clear();
            Gson gson = new Gson();
            String jsonText = pref.getString("weeks", null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            weekList = gson.fromJson(jsonText, type);
            absentWeekAdp = new AbsentWeekAdp(getContext(), weekList);
            absentWeekAdp.notifyDataSetChanged();
            recyclerView.setAdapter(absentWeekAdp);
        } else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
            query.whereEqualTo("studentId", studentId);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        weekList.clear();
                        for (ParseObject obj : objects) {
                            for (int i = 1; i <= Integer.parseInt(currentWeek); i++) {
                                String week = obj.getString("week" + i);
                                if (!week.equals("present")) {
                                    weekList.add(String.valueOf(i));
                                    absentWeekAdp.notifyDataSetChanged();
                                }
                            }

                        }
                    } else {

                    }
                }
            });
            recyclerView.setAdapter(absentWeekAdp);
//            weekList.clear();
//            Gson gson = new Gson();
//            String jsonText = pref.getString("weeks", null);
//            Type type = new TypeToken<ArrayList<String>>() {
//            }.getType();
//            weekList = gson.fromJson(jsonText, type);
//            absentWeekAdp = new AbsentWeekAdp(getContext(), weekList);
//            absentWeekAdp.notifyDataSetChanged();
//            recyclerView.setAdapter(absentWeekAdp);
        }
    }


}