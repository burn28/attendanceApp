package com.example.fyp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private EditText idInput,passwordInput;
    private Button signIn, register, student, lecturer;
    String role = "";
    String id,password;
    Boolean selected_role = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser.logOut();



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
                lecturer.setBackgroundColor(Color.parseColor("#FF6200EE"));
                role = "student";
                selected_role = true;
            }
        });
        lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lecturer.setBackgroundColor(Color.CYAN);
                student.setBackgroundColor(Color.parseColor("#FF6200EE"));
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


    public void Login(){
        id = idInput.getText().toString();
        password = passwordInput.getText().toString();
        if(selected_role){
            ParseUser.logInInBackground(id, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        if(user.get("role").equals(role)){
                            if(role.equals("lecturer")){
                                startActivity(new Intent(MainActivity.this, LecturerActivity.class));
                            }else{
                                startActivity(new Intent(MainActivity.this, StudentActivity.class));
                            }
                        }else {
                            Toast.makeText(MainActivity.this, "Select the correct role!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(MainActivity.this, "Please select your role", Toast.LENGTH_SHORT).show();
        }

    }

}