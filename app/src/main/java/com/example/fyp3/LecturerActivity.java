package com.example.fyp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.fyp3.Fragment.LecturerAttendance;
import com.example.fyp3.Fragment.LecturerHome;

import java.io.File;

public class LecturerActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LecturerHome())
                .commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.isExternalStorageManager())
            {
                File internal = new File(Environment.getExternalStorageDirectory() + "/Download/Student Attendance");
            }
            else
            {
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(permissionIntent);
            }
        }

    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.d("STACK", String.valueOf(count));

        if (count == 0) {
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}