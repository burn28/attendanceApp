package com.example.fyp3.Fragment;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Adapter.LecturerAttendanceAdp;
import com.example.fyp3.Adapter.LecturerAttendanceAdpNew;
import com.example.fyp3.Adapter.PercentageAdp;
import com.example.fyp3.ExcelUtils;
import com.example.fyp3.Model.AttendanceClass;
import com.example.fyp3.Model.AttendanceClassNew;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LecturerAttendance extends Fragment {

    public String week = "";
    public static final String[] weeks = {"1", "2", "3", "4", "5", "6", "7",
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

    private com.example.fyp3.Adapter.LecturerAttendanceAdpNew attendanceAdp2;
    public List<AttendanceClassNew> bufferList2;
    public List<AttendanceClassNew> percentList2;
    public List<AttendanceClassNew> displayList2;
    public List<AttendanceClassNew> attendanceList2;
    public List<AttendanceClassNew> absentList2;
    public List<AttendanceClassNew> presentList2;
    public List<AttendanceClassNew> noRecordList;
    public List<AttendanceClassNew> FirstList2;
    public List<AttendanceClassNew> SecList2;
    public List<AttendanceClassNew> ThirdList2;

    private TextView title;
    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;
    private TextView none;
    private ImageView exportBtn;
    String currentWeek;
    String courseId;

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    public void onResume() {
        super.onResume();

        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.week_list, weeks);
        autoCompleteTextView.setAdapter(arrayAdapter);
        if (week.equals("Overall Percentage")) {
            week = "Overall Percentage";
            showList2("percentage");
            radioGroup.setVisibility(View.GONE);
            radioGroup2.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer_attendance, container, false);
        setHasOptionsMenu(true);

        title = view.findViewById(R.id.course);
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        title.setText(preferences.getString("title", ""));

        SharedPreferences pref = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        currentWeek = pref.getString("week", "1");

        courseId = preferences.getString("courseId", "");

        autoCompleteTextView = view.findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.week_list, weeks);
        autoCompleteTextView.setAdapter(arrayAdapter);

        none = view.findViewById(R.id.none);
        exportBtn = view.findViewById(R.id.exportBtn);
        RadioButton radioButton = view.findViewById(R.id.optionAll);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                week = adapterView.getItemAtPosition(i).toString();
                if (week.equals("Overall Percentage")) {
                    radioGroup.setVisibility(View.GONE);
                    radioGroup2.setVisibility(View.VISIBLE);
                    exportBtn.setVisibility(View.VISIBLE);
                    showList2("percentage");
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                    radioGroup2.setVisibility(View.GONE);
//                    radioGroup.check(R.id.optionAll);
                    radioButton.setChecked(true);
                    showList2("default");

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

        attendanceList2 = new ArrayList<>();
        presentList2 = new ArrayList<>();
        absentList2 = new ArrayList<>();
        noRecordList = new ArrayList<>();
        FirstList2 = new ArrayList<>();
        SecList2 = new ArrayList<>();
        ThirdList2 = new ArrayList<>();
        percentList2 = new ArrayList<>();

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
                        showList2("all");
                        break;
                    case R.id.optionPresent:
                        showList2("present");
                        break;
                    case R.id.optionAbsent:
                        showList2("absent");
                        break;
                    case R.id.optionNo:
                        showList2("no record");
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
                        percentOpt2("firstOpt");
                        break;
                    case R.id.opt86:
                        percentOpt2("secOpt");
                        break;
                    case R.id.opt0:
                        percentOpt2("thirdOpt");
                        break;
                }
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("'_'yy.MM.dd'_'HH.mm");
                String fileName = "Attendance_"+courseId+sdf.format(new Date());
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.inflate(R.menu.export_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.export) {
                            int crntWeek = Integer.parseInt(currentWeek);
                            try {
                                boolean isSuccess = ExcelUtils.exportDataIntoWorkbook(getContext(), fileName, percentList2, crntWeek);
                                if (isSuccess) {
                                    Toast.makeText(getContext(), "Export Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Export FAILEDDD", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                });


            }
        });
        return view;
    }


    private void showList2(String option) {

        if (!week.equals("Overall Percentage") && Integer.parseInt(week) > Integer.parseInt(currentWeek)) {
            none.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            none.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (option.equals("default") && !week.equals("Overall Percentage")) {
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery(courseId);
                query1.orderByAscending("studentId");
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null && !objects.isEmpty()) {
                            attendanceList2.clear();
                            presentList2.clear();
                            absentList2.clear();
                            noRecordList.clear();
                            for (ParseObject object : objects) {
                                AttendanceClassNew lClass = new AttendanceClassNew();
                                lClass.setStudentId(object.getString("studentId"));
                                lClass.setStatus("week" + week, object.getString("week" + week));
                                attendanceList2.add(lClass);
//                                ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
//                                userParseQuery.whereEqualTo("username", lClass.getStudentId());
//                                userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
//                                    @Override
//                                    public void done(ParseUser object, ParseException e) {
//                                        if (e == null) {
//                                            lClass.setStudentName(object.getString("fullname"));
//
//                                            attendanceAdp2.notifyDataSetChanged();
//                                        }
//                                    }
//                                });
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
                                query.whereEqualTo("studentId", lClass.getStudentId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject objects, ParseException e) {
                                        if (objects.getString("fullname") != null) {
                                            lClass.setStudentName(objects.getString("fullname"));
                                        }
                                        attendanceAdp2.notifyDataSetChanged();
                                    }
                                });
                                if (lClass.getStatus("week" + week).equals("present")) {
                                    presentList2.add(lClass);
                                } else if (lClass.getStatus("week" + week).equals("no record")) {
                                    noRecordList.add(lClass);
                                } else {
                                    absentList2.add(lClass);
                                }
                            }
//                            presentList2.clear();
//                            absentList2.clear();
//                            noRecordList.clear();
//                            for(AttendanceClassNew obj : attendanceList2){
//                                if(obj.getStatus("week"+week).equals("present")){
//                                    presentList2.add(obj);
//                                }else if(obj.getStatus("week"+week).equals("no record")){
//                                    noRecordList.add(obj);
//                                }else{
//                                    absentList2.add(obj);
//                                }
//                                attendanceAdp2.notifyDataSetChanged();
//                            }
//                            for(AttendanceClassNew obj: attendanceList2){
//
//                            }
//                            studentList("all");
//                            displayList2 = new ArrayList<>(attendanceList2);
                            attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), attendanceList2, "week" + week);
                            recyclerView.setAdapter(attendanceAdp2);
                            attendanceAdp2.notifyDataSetChanged();
                        } else {
                            Log.d("EMPTY", "Error: " + e.getMessage());
                        }
                    }
                });

            } else if (week.equals("Overall Percentage")) {
//            Toast.makeText(getContext(), "RUNNING", Toast.LENGTH_SHORT).show();
                ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
                query.orderByAscending("studentId");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null && !objects.isEmpty()) {
                            percentList2.clear();
//                            int i = Integer.parseInt(currentWeek);
                            for (ParseObject obj : objects) {
                                AttendanceClassNew lClass = new AttendanceClassNew();
                                lClass.setStudentId(obj.getString("studentId"));
//                                lClass.setTotalWeek(1);
                                for (int j = 1; j <= Integer.parseInt(currentWeek); j++) {
                                    lClass.setStatus("week" + j, obj.getString("week" + j));
                                    if (!lClass.getStatus("week" + j).equals("present")) {
                                        lClass.setTotalWeek(lClass.getTotalWeek() + 1);
                                        lClass.setWeeks(String.valueOf(j));
                                    }
                                    ParseQuery<ParseUser> userParseQuery = ParseUser.getQuery();
                                    userParseQuery.whereEqualTo("username", lClass.getStudentId());
//                                    userParseQuery.getFirstInBackground(new GetCallback<ParseUser>() {
//                                        @Override
//                                        public void done(ParseUser object, ParseException e) {
//                                            if(e == null){
//                                                lClass.setStudentName(object.getString("fullname"));
//
//                                                percentageAdp.notifyDataSetChanged();
//                                            }else {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    });
//                                    userParseQuery.findInBackground(new FindCallback<ParseUser>() {
//                                        @Override
//                                        public void done(List<ParseUser> objects, ParseException e) {
//                                            if(e == null){
//                                                for(ParseUser user : objects){
//                                                    lClass.setStudentName(user.getString("fullname"));
//
//                                                    percentageAdp.notifyDataSetChanged();
//                                                }
//                                            }else {
//                                                Log.e("Parse", e.getMessage());
//                                            }
//                                        }
//                                    });
                                }
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Student");
                                query.whereEqualTo("studentId", lClass.getStudentId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {

                                        if (object.getString("fullname") != null) {
                                            lClass.setStudentName(object.getString("fullname"));
                                        }
                                        percentageAdp.notifyDataSetChanged();
                                    }
                                });
                                percentList2.add(lClass);
                                percentageAdp.notifyDataSetChanged();
                            }

                            for (AttendanceClassNew obj : percentList2) {
                                double percentage = (14d - obj.getTotalWeek()) * 100 / 14;
                                int result = (int) Math.ceil(percentage);
                                obj.setPercentage(result);
                            }
                            AttendanceClassNew std = percentList2.get(1);
                            Toast.makeText(getContext(), std.getTotalWeek().toString(), Toast.LENGTH_SHORT).show();
                            percentOpt2("firstOpt");
                        } else if (objects.isEmpty()) {
                            Log.d("PERCENTAGE", "Error: Empty");
                            none.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else if (e != null) {
                            Log.d("PERCENTAGE", "Error: " + e.getMessage());
                        }
                    }
                });
                radioGroup2.check(R.id.opt100);
            } else if (option.equals("all")) {
                displayList2 = new ArrayList<>(attendanceList2);
                attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), displayList2, "week" + week);
                recyclerView.setAdapter(attendanceAdp2);
                attendanceAdp2.notifyDataSetChanged();
            } else if (option.equals("present")) {
                displayList2 = new ArrayList<>(presentList2);
                attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), displayList2, "week" + week);
                recyclerView.setAdapter(attendanceAdp2);
                attendanceAdp2.notifyDataSetChanged();
            } else if (option.equals("absent")) {
                displayList2 = new ArrayList<>(absentList2);
                attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), displayList2, "week" + week);
                recyclerView.setAdapter(attendanceAdp2);
                attendanceAdp2.notifyDataSetChanged();
            } else if (option.equals("no record")) {
                displayList2 = new ArrayList<>(noRecordList);
                attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), displayList2, "week" + week);
                recyclerView.setAdapter(attendanceAdp2);
                attendanceAdp2.notifyDataSetChanged();
            }
        }
    }


    private void percentOpt2(String opt) {
        FirstList2.clear();
        SecList2.clear();
        ThirdList2.clear();
        for (AttendanceClassNew obj : percentList2) {
            if (obj.getPercentage() == 100) {
                FirstList2.add(obj);
            } else if (obj.getPercentage() < 100 && obj.getPercentage() > 85) {
                SecList2.add(obj);
            } else {
                ThirdList2.add(obj);
            }
        }
        if (opt.equals("firstOpt")) {
            percentageAdp = new PercentageAdp(getContext(), FirstList2);
        } else if (opt.equals("secOpt")) {
            percentageAdp = new PercentageAdp(getContext(), SecList2);
        } else {
            percentageAdp = new PercentageAdp(getContext(), ThirdList2);
        }
//        percentageAdp = new PercentageAdp(getContext(), displayList2);
        recyclerView.setAdapter(percentageAdp);
        percentageAdp.notifyDataSetChanged();

    }
//    private void showList(String option) {
//
//        if (!week.equals("Overall Percentage") && Integer.parseInt(week) > Integer.parseInt(currentWeek)) {
//            none.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//        } else {
//            none.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//
//            SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
//            String courseId = preferences.getString("courseId", "");
//            if (option.equals("default") && !week.equals("Overall Percentage")) {
//                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
//                query1.orderByAscending("studentId");
//                query1.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> objects, ParseException e) {
//                        if (e == null && !objects.isEmpty()) {
//                            attendanceList.clear();
//                            presentList.clear();
//                            absentList.clear();
//                            for (ParseObject object : objects) {
//                                AttendanceClass lClass = new AttendanceClass();
//                                lClass.setStudentId(object.getString("studentId"));
//                                lClass.setStudentName(object.getString("fullname"));
//                                lClass.setStatus("present");
//                                attendanceList.add(lClass);
//                            }
//                            ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
//                            query.whereEqualTo("week", week);
//                            query.findInBackground(new FindCallback<ParseObject>() {
//                                public void done(List<ParseObject> objects, ParseException e) {
//                                    if (e == null && !objects.isEmpty()) {
//                                        absentList.clear();
//                                        for (ParseObject object : objects) {
//                                            AttendanceClass lClass = new AttendanceClass();
//                                            lClass.setStudentId(object.getString("studentId"));
//                                            lClass.setStatus("absent");
//                                            absentList.add(lClass);
//                                        }
//                                        for (AttendanceClass obj : attendanceList) {
//                                            for (AttendanceClass obj2 : absentList) {
//                                                if (obj.getStudentId().equals(obj2.getStudentId())) {
//                                                    obj2.setStudentName(obj.getStudentName());
//                                                    obj.setStatus("absent");
//                                                    break;
//                                                } else {
//                                                    obj.setStatus("present");
//                                                }
//                                            }
//                                            if (obj.getStatus().equals("present")) {
//                                                presentList.add(obj);
//                                            }
//                                            attendanceAdp.notifyDataSetChanged();
//                                        }
//                                    } else if (objects.isEmpty()) {
//                                        presentList.addAll(attendanceList);
//                                    } else {
//                                        Log.d("EMPTY", "Error: " + e.getMessage());
//                                    }
//                                }
//                            });
//                            displayList = new ArrayList<>(attendanceList);
//                            attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//                            recyclerView.setAdapter(attendanceAdp);
//                            attendanceAdp.notifyDataSetChanged();
//                        } else {
//                            Log.d("EMPTY", "Error: " + e.getMessage());
//                        }
//                    }
//                });
//                radioGroup.check(R.id.optionAll);
//            } else if (week.equals("Overall Percentage")) {
////            Toast.makeText(getContext(), "RUNNING", Toast.LENGTH_SHORT).show();
//                ParseQuery<ParseObject> query = ParseQuery.getQuery(courseId);
//                query.orderByAscending("studentId");
//                query.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> objects, ParseException e) {
//                        if (e == null && !objects.isEmpty()) {
//                            percentList.clear();
//                            int i = 0;
//                            for (ParseObject obj : objects) {
//                                AttendanceClass lClass = new AttendanceClass();
//                                lClass.setStudentId(obj.getString("studentId"));
//                                lClass.setTotalWeek(1);
//                                lClass.setWeek(obj.getString("week"));
//                                if (percentList.isEmpty()) {
//                                    percentList.add(lClass);
//                                } else if (!lClass.getStudentId().equals(percentList.get(i).getStudentId())) {
//                                    percentList.add(lClass);
////                                Toast.makeText(getContext(), bufferList.get(i).getStudentId(), Toast.LENGTH_SHORT).show();
//                                    i++;
//                                } else {
//                                    percentList.get(i).setTotalWeek(percentList.get(i).getTotalWeek() + 1);
//                                    percentList.get(i).setWeek(obj.getString("week"));
//                                }
//                            }
//                            for (AttendanceClass obj : percentList) {
//                                double percentage = (14d - obj.getTotalWeek()) * 100 / 14;
//                                int result = (int) Math.ceil(percentage);
//                                obj.setPercentage(result);
//                            }
//                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("ISA15");
//                            query1.orderByAscending("studentId");
//                            query1.findInBackground(new FindCallback<ParseObject>() {
//                                @Override
//                                public void done(List<ParseObject> objects, ParseException e) {
//                                    if (e == null && !objects.isEmpty()) {
//                                        bufferList.clear();
//                                        for (ParseObject obj : objects) {
//                                            AttendanceClass student = new AttendanceClass();
//                                            student.setStudentId(obj.getString("studentId"));
//                                            student.setStudentName(obj.getString("fullname"));
//                                            for (AttendanceClass percent : percentList) {
//                                                if (student.getStudentId().equals(percent.getStudentId())) {
//                                                    student.setPercentage(percent.getPercentage());
//                                                    student.setTotalWeek(percent.getTotalWeek());
//                                                    student.setWeeks(percent.getWeek());
//                                                    break;
//                                                }
//                                            }
//                                            bufferList.add(student);
//                                        }
////                                        percentOpt("firstOpt");
////                                    Toast.makeText(getContext(), "ELOK JE", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }else if(objects.isEmpty()){
//                            Log.d("PERCENTAGE", "Error: Empty");
//                            none.setVisibility(View.VISIBLE);
//                            recyclerView.setVisibility(View.GONE);
//                        }
//                        else if(e != null) {
//                            Log.d("PERCENTAGE", "Error: " + e.getMessage());
//                        }
//
//                    }
//                });
////            displayList = new ArrayList<>(bufferList);
////            percentageAdp = new PercentageAdp(getContext(), displayList);
////            recyclerView.setAdapter(percentageAdp);
////            percentageAdp.notifyDataSetChanged();
//                radioGroup2.check(R.id.opt100);
//
//            } else if (option.equals("all")) {
//                displayList = new ArrayList<>(attendanceList);
//                attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//                recyclerView.setAdapter(attendanceAdp);
//                attendanceAdp.notifyDataSetChanged();
//            } else if (option.equals("present")) {
//                displayList = new ArrayList<>(presentList);
//                attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//                recyclerView.setAdapter(attendanceAdp);
//                attendanceAdp.notifyDataSetChanged();
//            } else if (option.equals("absent")) {
//                displayList = new ArrayList<>(absentList);
//                attendanceAdp = new LecturerAttendanceAdp(getContext(), displayList);
//                recyclerView.setAdapter(attendanceAdp);
//                attendanceAdp.notifyDataSetChanged();
//            }
//        }
//
//
//    }

//    private void studentList(String opt){
//        presentList2.clear();
//        absentList2.clear();
//        noRecordList.clear();
//        for(AttendanceClassNew obj : attendanceList2){
//            if(obj.getStatus("week"+week).equals("present")){
//                presentList2.add(obj);
//            }else if(obj.getStatus("week"+week).equals("no record")){
//                noRecordList.add(obj);
//            }else{
//                absentList2.add(obj);
//            }
//        }
//        if(opt.equals("all")){
//            attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), attendanceList2, "week"+week);
//        }else if(opt.equals("present")){
//            attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), presentList2, "week"+week);
//            attendanceAdp2.notifyDataSetChanged();
//        }else if(opt.equals("absent")){
//            attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), absentList2, "week"+week);
//            attendanceAdp2.notifyDataSetChanged();
//
//        }else if(opt.equals("no record")){
//            attendanceAdp2 = new LecturerAttendanceAdpNew(getContext(), noRecordList, "week"+week);
//            attendanceAdp2.notifyDataSetChanged();
//        }
//        recyclerView.setAdapter(attendanceAdp2);
//        attendanceAdp2.notifyDataSetChanged();
//    }

//    private void percentOpt(String opt) {
//        FirstList.clear();
//        SecList.clear();
//        ThirdList.clear();
//        for (AttendanceClass obj : bufferList) {
//            if (obj.getPercentage() == 100) {
//                FirstList.add(obj);
//            } else if (obj.getPercentage() < 100 && obj.getPercentage() > 85) {
//                SecList.add(obj);
//            } else {
//                ThirdList.add(obj);
//            }
//        }
//        if (opt.equals("firstOpt")) {
//            displayList = new ArrayList<>(FirstList);
//        } else if (opt.equals("secOpt")) {
//            displayList = new ArrayList<>(SecList);
//        } else {
//            displayList = new ArrayList<>(ThirdList);
//        }
//        percentageAdp = new PercentageAdp(getContext(), displayList);
//        recyclerView.setAdapter(percentageAdp);
//        percentageAdp.notifyDataSetChanged();
//
//    }

}