package com.example.fyp3.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Adapter.LecturerClassAdp;
import com.example.fyp3.MainActivity;
import com.example.fyp3.Model.LecturerClass;
import com.example.fyp3.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class LecturerHome extends Fragment {

    private RecyclerView recyclerView;
    private com.example.fyp3.Adapter.LecturerClassAdp classAdapter;
    private List<LecturerClass> classList;
    RecyclerView.LayoutManager linearLayoutManager;
    private TextView weekText;
    private ImageView logoutBtn;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecturer_home, container, false);
        setHasOptionsMenu(true);

        SharedPreferences pref = getActivity().getSharedPreferences("DATE", Context.MODE_PRIVATE);
        String week = "Week " + pref.getString("week", "1");
        weekText = view.findViewById(R.id.textWeek);
        weekText.setText(week);

        recyclerView = view.findViewById(R.id.recycler);
        linearLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(linearLayoutManager);

        classList = new ArrayList<>();
        classAdapter = new LecturerClassAdp(getContext(), classList);
        recyclerView.setAdapter(classAdapter);

        showList();
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!classList.isEmpty()){
                    searchList(newText);
                }
                return true;
            }
        });

        logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });


        return view;
    }

    private void searchList(String s) {
        List<LecturerClass> filteredList = new ArrayList<>();
        for(LecturerClass Class : classList){
            if(Class.getCourseId().toLowerCase().contains(s.toLowerCase()) ||
                    Class.getTitle().toLowerCase().contains(s.toLowerCase())){
                filteredList.add(Class);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(getContext(), "No data found!", Toast.LENGTH_SHORT).show();
            classAdapter.setFilteredList(filteredList);
        }else{
            classAdapter.setFilteredList(filteredList);
        }
    }

    private void showList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("course");
        query.whereEqualTo("lecturerId", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    classList.clear();

                    for (ParseObject object : objects) {
                        LecturerClass lClass = new LecturerClass();
                        lClass.setCourseId(object.getString("courseId"));
                        lClass.setTitle(object.getString("title"));
                        lClass.setDay(object.getString("day"));
                        lClass.setStartTime(object.getString("startTime"));
                        lClass.setEndTime(object.getString("endTime"));
                        lClass.setLocation(object.getString("location"));
                        lClass.setType(object.getString("type"));
                        classList.add(lClass);
                    }
                    classAdapter.notifyDataSetChanged();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}