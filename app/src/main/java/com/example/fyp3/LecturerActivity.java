package com.example.fyp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Fragment.LecturerHome;

public class LecturerActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LecturerHome())
                .commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else if (getSupportFragmentManager().getBackStackEntryAt(count - 1).getName().equals("details")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LecturerAttendance())
                    .addToBackStack("attendance")
                    .commit();
//            Toast.makeText(this, "details", Toast.LENGTH_SHORT).show();
        } else {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
//            Toast.makeText(this, "pop", Toast.LENGTH_SHORT).show();
        }
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//        } else {
//            getSupportFragmentManager().popBackStack();
//        }
    }
}