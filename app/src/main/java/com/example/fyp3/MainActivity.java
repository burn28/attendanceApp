package com.example.fyp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.parse.Parse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText idInput, passwordInput;
    private Button signIn, register, student, lecturer;
    String role = "";
    String id, password;
    Boolean selected_role = false;
    String sDate;
    String week;
    int currentWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Parse.initialize(new Parse.Configuration.Builder(this)
//                        .applicationId(getString(R.string.back4app_app_id))
//                        // if defined
//                        .clientKey(getString(R.string.back4app_client_key))
//                        .server(getString(R.string.back4app_server_url))
//                        .build());
        SharedPreferences pref = this.getSharedPreferences("DATE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
//        editor.remove("week");
//        editor.remove("startDate");
//        editor.apply();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            if(currentUser.get("role").equals("lecturer")){
                startActivity(new Intent(MainActivity.this, LecturerActivity.class));
                finish();
            }else if(currentUser.get("role").equals("student")){
                startActivity(new Intent(MainActivity.this, StudentActivity.class));
                finish();
            }
        }else {
            ParseUser.logOut();
        }


//        week = pref.getString("week", "1");
        sDate = "12/12/2022";
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
//        String currentDate = "21/11/2022";


        setWeek(new SimpleDateFormat("dd/MM/yyyy"), sDate, currentDate);


        student = findViewById(R.id.studentBtn);
        lecturer = findViewById(R.id.lecturerBtn);
        idInput = findViewById(R.id.edittextID);
        passwordInput = findViewById(R.id.edittextPass);
        signIn = findViewById(R.id.signinBtn);
        register = findViewById(R.id.registerBtn);


        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student.setBackgroundColor(Color.CYAN);
                lecturer.setBackgroundColor(Color.parseColor("#4378DB"));
                role = "student";
                selected_role = true;
            }
        });
        lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lecturer.setBackgroundColor(Color.CYAN);
                student.setBackgroundColor(Color.parseColor("#4378DB"));
                role = "lecturer";
                selected_role = true;
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, com.example.fyp3.register.class);
                startActivity(i);
            }
        });
    }


    public void Login() {
        id = idInput.getText().toString();
        password = passwordInput.getText().toString();
        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all the field!", Toast.LENGTH_SHORT).show();
        } else {
            if (selected_role) {
                Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                ParseUser.logInInBackground(id, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            if (user.get("role").equals(role)) {
                                if (role.equals("lecturer")) {
                                    startActivity(new Intent(MainActivity.this, LecturerActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(MainActivity.this, StudentActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Select the correct role!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(MainActivity.this, "Login Failed!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Please select your role", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setWeek(SimpleDateFormat simpleDateFormat, String startDate, String currentDate) {
        SharedPreferences pref = this.getSharedPreferences("DATE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            int diff = (int) TimeUnit.DAYS.convert(simpleDateFormat.parse(currentDate).getTime() - simpleDateFormat.parse(startDate).getTime(), TimeUnit.MILLISECONDS);
            Log.d("DIFFERENT", String.valueOf(diff));

            if(diff >6 && diff<49){
                int plus = diff/7;
//                currentWeek = Integer.parseInt(week);
//                currentWeek = currentWeek + plus;
//                week = String.valueOf(currentWeek);
//                editor.putString("week", week);
//                sDate = currentDate;
//                editor.putString("startDate", sDate);
//                editor.apply();

                currentWeek = plus +1;
                week = String.valueOf(currentWeek);
                editor.putString("week", week);
                editor.apply();

            }else{
                currentWeek = diff/7;
                week = String.valueOf(currentWeek);
                editor.putString("week", week);
                editor.apply();
            }
//            if (diff == 7 && !week.equals("7")) {
//                currentWeek = Integer.parseInt(week);
//                currentWeek++;
//                week = String.valueOf(currentWeek);
//                editor.putString("week", week);
//                sDate = currentDate;
//                editor.putString("startDate", sDate);
//                editor.apply();
//            }
//            else if(diff == 14 && week.equals("7")){
//                currentWeek = Integer.parseInt(week);
//                currentWeek++;
//                week = String.valueOf(currentWeek);
//                editor.putString("week", week);
//                sDate = currentDate;
//                editor.putString("startDate", sDate);
//                editor.apply();
//            }

//            Toast.makeText(MainActivity.this, week, Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            Log.e("DIDNT WORK", "exception", exception);
        }

    }

}