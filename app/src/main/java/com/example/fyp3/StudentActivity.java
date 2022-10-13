package com.example.fyp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp3.Fragment.LecturerHome;
import com.example.fyp3.Fragment.StudentAttendance;
import com.example.fyp3.Fragment.StudentHome;
import com.example.fyp3.Model.StudentClass;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class StudentActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView studentId,studentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        navigationView = findViewById(R.id.navigation);
        getStudent();
        navigationView.getMenu().findItem(R.id.home).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawer.closeDrawer(GravityCompat.START);
                switch (id)
                {
                    case R.id.home:
                        Toast.makeText(StudentActivity.this, "Home Fragment", Toast.LENGTH_SHORT).show();
                        replaceFragment(new StudentHome());
                        break;
                    case R.id.attendance:
                        Toast.makeText(StudentActivity.this, "Attendance Fragment", Toast.LENGTH_SHORT).show();
                        replaceFragment(new StudentAttendance());
                        break;
                    case R.id.table:
                        Toast.makeText(StudentActivity.this, "Table Fragment", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        replaceFragment(new StudentHome());
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
//            super.onBackPressed();
        }

    }

    public void getStudent(){
        View header = navigationView.getHeaderView(0);
        studentId = header.findViewById(R.id.studentId);
        studentName = header.findViewById(R.id.studentName);

        SharedPreferences pref = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ISA15");
        query.whereEqualTo("studentId", ParseUser.getCurrentUser().getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    String id = object.getString("studentId");
                    String name = object.getString("fullname");
                    studentId.setText(id);
                    studentName.setText(name);
                    editor.putString("studentId",id);
                    editor.putString("studentName",name);
                    editor.apply();
                }
            }
        });
    }
}