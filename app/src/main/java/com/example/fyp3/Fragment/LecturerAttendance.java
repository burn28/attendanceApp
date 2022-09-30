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
import com.example.fyp3.Adapter.PercentageAdp;
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
    public String[] weeks = {"1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14", "Overall Percentage"};
    public AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;

    private RecyclerView recyclerView;
    private com.example.fyp3.Adapter.LecturerAttendanceAdp attendanceAdp;
    private com.example.fyp3.Adapter.PercentageAdp percentageAdp;
    public List<AttendanceClass> attendanceList;
    public List<AttendanceClass> absentList;
    public List<AttendanceClass> presentList;
    public List<AttendanceClass> displayList;
    public List<AttendanceClass> bufferList;
    public List<AttendanceClass> percentList;
    public List<AttendanceClass> FirstList;
    public List<AttendanceClass> SecList;
    public List<AttendanceClass> ThirdList;
    RecyclerView.LayoutManager linearLayoutManager;

    private TextView title;
    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer_attendance, container, false);
        setHasOptionsMenu(true);

        title = view.findViewById(R.id.course);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        title.setText(preferences.getString("title", ""));

        autoCompleteTextView = view.findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.week_list, weeks);
        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                week = adapterView.getItemAtPosition(i).toString();
                if (week.equals("Overall Percentage")) {
                    radioGroup.setVisibility(View.GONE);
                    radioGroup2.setVisibility(View.VISIBLE);
                    showList("percentage");
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                    radioGroup2.setVisibility(View.GONE);
                    showList("default");

                }


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
        FirstList = new ArrayList<>();
        SecList = new ArrayList<>();
        ThirdList = new ArrayList<>();


        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.optionAll:
                        showList("all");
                        break;
                    case R.id.optionPresent:
                        showList("present");
                        break;
                    case R.id.optionAbsent:
                        showList("absent");
                        break;
                }
            }
        });
        radioGroup2 = view.findViewById(R.id.radioGroup2);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.opt100:
                        percentOpt("firstOpt");
                        break;
                    case R.id.opt86:
                        percentOpt("secOpt");
                        break;
                    case R.id.opt0:
                        percentOpt("thirdOpt");
                        break;
                }
            }
        });


        return view;
    }

    private void showList(String option) {
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String courseId = preferences.getString("courseId", "");
        if (option.equals("default") && !week.equals("Overall Percentage")) {
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
            query1.orderByAscending("studentId");
            query1.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && !objects.isEmpty()) {
                        attendanceList.clear();
                        presentList.clear();
                        for (ParseObject object : objects) {
                            AttendanceClass lClass = new AttendanceClass();
                            lClass.setStudentId(object.getString("studentId"));
                            lClass.setStudentName(object.getString("fullname"));
                            lClass.setStatus("present");
                            attendanceList.add(lClass);
                        }
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
                                    for (AttendanceClass obj : attendanceList) {
                                        for (AttendanceClass obj2 : absentList) {
                                            if (obj.getStudentId().equals(obj2.getStudentId())) {
                                                obj2.setStudentName(obj.getStudentName());
                                                obj.setStatus("absent");
                                                break;
                                            } else {
                                                obj.setStatus("present");
                                            }
                                        }
                                        if(obj.getStatus().equals("present")){
                                            presentList.add(obj);
                                        }
                                        attendanceAdp.notifyDataSetChanged();
                                    }
                                } else if (objects.isEmpty()) {
                                    presentList.addAll(attendanceList);
                                } else {
                                    Log.d("EMPTY", "Error: " + e.getMessage());
                                }
                            }
                        });
                        displayList = new ArrayList<>(attendanceList);
                        attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
                        recyclerView.setAdapter(attendanceAdp);
                        attendanceAdp.notifyDataSetChanged();
                    } else {
                        Log.d("EMPTY", "Error: " + e.getMessage());
                    }
                }
            });
            radioGroup.check(R.id.optionAll);
        }else if (week.equals("Overall Percentage")) {
//            Toast.makeText(getContext(), "RUNNING", Toast.LENGTH_SHORT).show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
            query.orderByAscending("studentId");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && !objects.isEmpty()) {
                        percentList.clear();
                        int i = 0;
                        for (ParseObject obj : objects) {
                            AttendanceClass lClass = new AttendanceClass();
                            lClass.setStudentId(obj.getString("studentId"));
                            lClass.setTotalWeek(1);
                            if (percentList.isEmpty()) {
                                percentList.add(lClass);
                            } else if (!lClass.getStudentId().equals(percentList.get(i).getStudentId())) {
                                percentList.add(lClass);
//                                Toast.makeText(getContext(), bufferList.get(i).getStudentId(), Toast.LENGTH_SHORT).show();
                                i++;
                            } else {
                                percentList.get(i).setTotalWeek(percentList.get(i).getTotalWeek() + 1);
                            }
                        }
                        for (AttendanceClass obj : percentList) {
                            double percentage = (14d - obj.getTotalWeek()) * 100 / 14;
                            int result = (int) Math.ceil(percentage);
                            obj.setPercentage(result);
                        }
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
                        query1.orderByAscending("studentId");
                        query1.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null && !objects.isEmpty()) {
                                    bufferList.clear();
                                    for (ParseObject obj : objects) {
                                        AttendanceClass student = new AttendanceClass();
                                        student.setStudentId(obj.getString("studentId"));
                                        student.setStudentName(obj.getString("fullname"));
                                        for (AttendanceClass percent : percentList) {
                                            if (student.getStudentId().equals(percent.getStudentId())) {
                                                student.setPercentage(percent.getPercentage());
                                                student.setTotalWeek(percent.getTotalWeek());
                                                break;
                                            }
                                        }
                                        bufferList.add(student);
                                    }
                                    percentOpt("firstOpt");
//                                    Toast.makeText(getContext(), "ELOK JE", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d("PERCENTAGE", "Error: " + e.getMessage());
                    }

                }
            });
//            displayList = new ArrayList<>(bufferList);
//            percentageAdp = new PercentageAdp(getContext(), displayList);
//            recyclerView.setAdapter(percentageAdp);
//            percentageAdp.notifyDataSetChanged();
            radioGroup2.check(R.id.opt100);

        } else if (option.equals("all")) {
            displayList = new ArrayList<>(attendanceList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        } else if (option.equals("present")) {
            displayList = new ArrayList<>(presentList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        } else if (option.equals("absent")) {
            displayList = new ArrayList<>(absentList);
            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
            recyclerView.setAdapter(attendanceAdp);
            attendanceAdp.notifyDataSetChanged();
        }


    }

    private void percentOpt(String opt) {
        FirstList.clear();
        SecList.clear();
        ThirdList.clear();
        for (AttendanceClass obj : bufferList) {
            if (obj.getPercentage() == 100) {
                FirstList.add(obj);
            } else if (obj.getPercentage() < 100 && obj.getPercentage() > 85) {
                SecList.add(obj);
            } else {
                ThirdList.add(obj);
            }
        }
        if (opt.equals("firstOpt")) {
            displayList = new ArrayList<>(FirstList);
        } else if (opt.equals("secOpt")) {
            displayList = new ArrayList<>(SecList);
        } else {
            displayList = new ArrayList<>(ThirdList);
        }
        percentageAdp = new PercentageAdp(getContext(), displayList);
        recyclerView.setAdapter(percentageAdp);
        percentageAdp.notifyDataSetChanged();

    }

//    private void showList2(String option) {
//        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//        String courseId = preferences.getString("courseId", "");
//        if (option.equals("default") && !week.equals("Overall Percentage")) {
////            absentList = new ArrayList<>();
//            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
//            query.whereEqualTo("week", week);
//            query.findInBackground(new FindCallback<ParseObject>() {
//                public void done(List<ParseObject> objects, ParseException e) {
//                    if (e == null && !objects.isEmpty()) {
//                        absentList.clear();
//                        for (ParseObject object : objects) {
//                            AttendanceClass lClass = new AttendanceClass();
//                            lClass.setStudentId(object.getString("studentId"));
//                            lClass.setStatus("absent");
//                            absentList.add(lClass);
//                        }
//                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
//                        query1.orderByAscending("studentId");
//                        query1.findInBackground(new FindCallback<ParseObject>() {
//                            @Override
//                            public void done(List<ParseObject> objects, ParseException e) {
//                                if (e == null && !objects.isEmpty()) {
//                                    attendanceList.clear();
//                                    for (ParseObject object : objects) {
//                                        AttendanceClass lClass = new AttendanceClass();
//                                        lClass.setStudentId(object.getString("studentId"));
//                                        lClass.setStudentName(object.getString("fullname"));
//                                        for (AttendanceClass obj : absentList) {
//                                            if (lClass.getStudentId().equals(obj.getStudentId())) {
//                                                obj.setStudentName(lClass.getStudentName());
//                                                lClass.setStatus("absent");
//                                                break;
//                                            } else {
//                                                lClass.setStatus("present");
//                                            }
//                                        }
//                                        if (lClass.getStatus().equals("present")) {
//                                            presentList.add(lClass);
//                                        }
//                                        attendanceList.add(lClass);
//                                    }
//                                    displayList = new ArrayList<>(attendanceList);
//                                    attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//                                    recyclerView.setAdapter(attendanceAdp);
//                                    attendanceAdp.notifyDataSetChanged();
//
//                                } else {
//                                    Log.d("EMPTY", "Error: " + e.getMessage());
//                                }
//                            }
//                        });
//
//                    } else if (objects.isEmpty()) {
//                        attendanceList.clear();
//                        attendanceAdp.notifyDataSetChanged();
//                    } else {
//                        Log.d("FIRST", "Error: " + e.getMessage());
//                    }
//                }
//            });
//            radioGroup.check(R.id.optionAll);
//        } else if (week.equals("Overall Percentage")) {
////            Toast.makeText(getContext(), "RUNNING", Toast.LENGTH_SHORT).show();
//            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
//            query.orderByAscending("studentId");
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//                    if (e == null && !objects.isEmpty()) {
//                        percentList.clear();
//                        int i = 0;
//                        for (ParseObject obj : objects) {
//                            AttendanceClass lClass = new AttendanceClass();
//                            lClass.setStudentId(obj.getString("studentId"));
//                            lClass.setTotalWeek(1);
//                            if (percentList.isEmpty()) {
//                                percentList.add(lClass);
//                            } else if (!lClass.getStudentId().equals(percentList.get(i).getStudentId())) {
//                                percentList.add(lClass);
////                                Toast.makeText(getContext(), bufferList.get(i).getStudentId(), Toast.LENGTH_SHORT).show();
//                                i++;
//                            } else {
//                                percentList.get(i).setTotalWeek(percentList.get(i).getTotalWeek() + 1);
//                            }
//                        }
//                        for (AttendanceClass obj : percentList) {
//                            double percentage = (14d - obj.getTotalWeek()) * 100 / 14;
//                            int result = (int) Math.ceil(percentage);
//                            obj.setPercentage(result);
//                        }
//                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
//                        query1.orderByAscending("studentId");
//                        query1.findInBackground(new FindCallback<ParseObject>() {
//                            @Override
//                            public void done(List<ParseObject> objects, ParseException e) {
//                                if (e == null && !objects.isEmpty()) {
//                                    bufferList.clear();
//                                    for (ParseObject obj : objects) {
//                                        AttendanceClass student = new AttendanceClass();
//                                        student.setStudentId(obj.getString("studentId"));
//                                        student.setStudentName(obj.getString("fullname"));
//                                        for (AttendanceClass percent : percentList) {
//                                            if (student.getStudentId().equals(percent.getStudentId())) {
//                                                student.setPercentage(percent.getPercentage());
//                                                student.setTotalWeek(percent.getTotalWeek());
//                                                break;
//                                            }
//                                        }
//                                        bufferList.add(student);
//                                    }
//                                    percentOpt("firstOpt");
////                                    Toast.makeText(getContext(), "ELOK JE", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else {
//                        Log.d("PERCENTAGE", "Error: " + e.getMessage());
//                    }
//                }
//            });
////            displayList = new ArrayList<>(bufferList);
////            percentageAdp = new PercentageAdp(getContext(), displayList);
////            recyclerView.setAdapter(percentageAdp);
////            percentageAdp.notifyDataSetChanged();
//            radioGroup2.check(R.id.opt100);
//        } else if (option.equals("all")) {
//            displayList = new ArrayList<>(attendanceList);
//            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//            recyclerView.setAdapter(attendanceAdp);
//            attendanceAdp.notifyDataSetChanged();
//        } else if (option.equals("present")) {
//            displayList = new ArrayList<>(presentList);
//            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//            recyclerView.setAdapter(attendanceAdp);
//            attendanceAdp.notifyDataSetChanged();
//        } else if (option.equals("absent")) {
//            displayList = new ArrayList<>(absentList);
//            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//            recyclerView.setAdapter(attendanceAdp);
//            attendanceAdp.notifyDataSetChanged();
//        }
//    }
}