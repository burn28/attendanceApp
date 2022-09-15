package com.example.fyp3.Fragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Adapter.LecturerAttendanceAdp;
import com.example.fyp3.Model.AttendanceClass;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LecturerAttendance extends Fragment {

    public String week = "";
    public String[] weeks = {"1","2","3","4","5","6","7",
            "8","9","10","11","12","13","14","percentage"};
    public AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;

    private RecyclerView recyclerView;
    private com.example.fyp3.Adapter.LecturerAttendanceAdp attendanceAdp;
    public List<AttendanceClass> attendanceList;
    public List<AttendanceClass> absentList;
    public List<AttendanceClass> presentList;
    public List<AttendanceClass> displayList;
    public List<AttendanceClass> bufferList;
    public List<AttendanceClass> percentList;
    RecyclerView.LayoutManager linearLayoutManager;

    private TextView title;
    private RadioGroup radioGroup;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer_attendance, container, false);
        setHasOptionsMenu(true);

        title = view.findViewById(R.id.course);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        title.setText(preferences.getString("title",""));

        autoCompleteTextView = view.findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.week_list, weeks);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                week = adapterView.getItemAtPosition(i).toString();
                showList2("default");

            }
        });

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        absentList = new ArrayList<>();
        presentList = new ArrayList<>();
//        displayList = new ArrayList<>();
        attendanceList = new ArrayList<>();

        bufferList = new ArrayList<>();
        percentList = new ArrayList<>();


        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.optionAll:
                        showList2("all");
                        break;
                    case R.id.optionPresent:
                        showList2("present");
                        break;
                    case R.id.optionAbsent:
                        showList2("absent");
                        break;
                }
            }
        });


        return view;
    }

    private void showList() {

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String courseId = preferences.getString("courseId","");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Attendance");
        query.whereEqualTo("courseId", courseId);
        query.whereEqualTo("week",week);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    attendanceList.clear();

                    for(ParseObject object:objects){
                        AttendanceClass lClass = new AttendanceClass();
                        lClass.setCourseId(object.getString("courseId"));
                        lClass.setStudentId(object.getString("studentId"));
                        lClass.setStatus(object.getString("status"));
                        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                        userQuery.whereEqualTo("username", object.getString("studentId"));
                        userQuery.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null){
                                   for(ParseUser user:objects){
                                       lClass.setStudentName(user.getString("fullname"));
                                       attendanceList.add(lClass);
                                   }
                                   displayList = new ArrayList<>(attendanceList);
                                    attendanceAdp.notifyDataSetChanged();
                                }else {
                                    Log.d("SECOND", "Error: " + e.getMessage());
                                }
                            }
                        });

                    }

                }else if(objects.isEmpty()){
                    attendanceList.clear();
                    attendanceAdp.notifyDataSetChanged();
                }
                else {
                    Log.d("FIRST", "Error: " + e.getMessage());
                }
            }
        });


    }

    private void showList2(String option) {
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String courseId = preferences.getString("courseId", "");
        if(option.equals("default") && !week.equals("percentage")) {
//            absentList = new ArrayList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
            query.whereEqualTo("week", week);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && !objects.isEmpty()) {
                        absentList.clear();

                        for (ParseObject object : objects) {
                            AttendanceClass lClass = new AttendanceClass();
                            lClass.setStudentId(object.getString("studentId"));
                            lClass.setStatus("absent");
                            absentList.add(lClass);
                        }
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
                        query1.orderByAscending("studentId");
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null && !objects.isEmpty()) {
                                    attendanceList.clear();
                                    for (ParseObject object : objects) {
                                        AttendanceClass lClass = new AttendanceClass();
                                        lClass.setStudentId(object.getString("studentId"));
                                        lClass.setStudentName(object.getString("fullname"));
                                        for (AttendanceClass obj : absentList) {
                                            if (lClass.getStudentId().equals(obj.getStudentId())) {
                                                obj.setStudentName(lClass.getStudentName());
                                                lClass.setStatus("absent");
                                                break;
                                            } else {
                                                lClass.setStatus("present");
                                            }
                                        }
                                        if(lClass.getStatus().equals("present")){
                                            presentList.add(lClass);
                                        }
                                        attendanceList.add(lClass);
                                    }
                                    displayList = new ArrayList<>(attendanceList);
                                    attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
                                    recyclerView.setAdapter(attendanceAdp);
                                    attendanceAdp.notifyDataSetChanged();

                                } else {
                                    Log.d("EMPTY", "Error: " + e.getMessage());
                                }
                            }
                        });

                    } else if (objects.isEmpty()) {
                        attendanceList.clear();
                        attendanceAdp.notifyDataSetChanged();
                    } else {
                        Log.d("FIRST", "Error: " + e.getMessage());
                    }
                }
            });
            radioGroup.check(R.id.optionAll);
        }else if(option.equals("all")){
            displayList = new ArrayList<>(attendanceList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        }else if(option.equals("present")){
            displayList = new ArrayList<>(presentList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        }
        else if(option.equals("absent")){
            displayList = new ArrayList<>(absentList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        }else if(week.equals("percentage")){
//            percentList.clear();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
            query.orderByAscending("studentId");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && !objects.isEmpty()) {
                        int i = 0;
                        for(ParseObject obj:objects){
                            AttendanceClass lClass = new AttendanceClass();
                            lClass.setStudentId(obj.getString("studentId"));
                            lClass.setTotalWeek(1);
//                            if(!bufferList.isEmpty()){
//                                for(AttendanceClass student:bufferList){
//                                    if(!lClass.getStudentId().equals(student.getStudentId())){
//
//                                    }else{
//                                        lClass.setTotalWeek(student.getTotalWeek()+1);
//                                        percentList.add(lClass);
//                                        break;
//                                    }
//                                }
//                                bufferList.add(lClass);
//                            }else{
//                                bufferList.add(lClass);
//                            }
                            if(percentList.isEmpty()){
                                percentList.add(lClass);
                            }
                            else if(!lClass.getStudentId().equals(percentList.get(i).getStudentId())){
                                percentList.add(lClass);
//                                Toast.makeText(getContext(), bufferList.get(i).getStudentId(), Toast.LENGTH_SHORT).show();
                                i++;
                            }else{
                                percentList.get(i).setTotalWeek(percentList.get(i).getTotalWeek()+1);
                            }
//                            bufferList.add(lClass);
                        }
                    }else {
                        Log.d("PERCENTAGE", "Error: " + e.getMessage());
                    }

                }
            });
            for(AttendanceClass obj:percentList){
                Toast.makeText(getContext(), obj.getStudentId()+" total week "+ obj.getTotalWeek(), Toast.LENGTH_SHORT).show();
            }
//            SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
//            editor.putString("layout","percentage");
//            editor.commit();
//            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//            recyclerView.setAdapter(attendanceAdp);
//            attendanceAdp.notifyDataSetChanged();

        }


    }
}