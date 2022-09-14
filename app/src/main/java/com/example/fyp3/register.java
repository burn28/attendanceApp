package com.example.fyp3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class register extends AppCompatActivity {

    private TextView signin;
    private EditText idInput,fullnameInput,emailInput,passwordInput,password2Input;
    private Button register, student, lecturer;
    String role = "";
    String id,fullname,email,password,password2;
    Boolean selected_role = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        student = findViewById(R.id.studentBtn);
        lecturer = findViewById(R.id.lecturerBtn);
        idInput = findViewById(R.id.edittextID);
        fullnameInput = findViewById(R.id.edittextFullname);
        emailInput = findViewById(R.id.edittextEmail);
        passwordInput = findViewById(R.id.edittextPass);
        password2Input = findViewById(R.id.edittextPass2);
        register = findViewById(R.id.registerBtn);
        signin = findViewById(R.id.signinText);

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
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

    }

    public void Register(){
        //Saving your First data object on Back4App
        id = idInput.getText().toString();
        fullname = fullnameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        password2 = password2Input.getText().toString();

        ParseUser user = new ParseUser();
        if(!id.matches("")&&!fullname.matches("")&&!email.matches("")&&!password.matches("")&&!password2.matches("")
        && password.equals(password2) && selected_role){

            user.setUsername(id);
            user.setEmail(email);
            user.setPassword(password);
            user.put("fullname", fullname);
            user.put("role", role);


            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        ParseUser.logOut();
                        showAlert("Account Created Successfully!", "Please verify your email before Login", false);
//                        Toast.makeText(register.this, "Sign Up Successfully!  Please verify your email.", Toast.LENGTH_SHORT).show();
                    }else {

                        showAlert("Error Account Creation failed", "Account could not be created" + " :" + e.getMessage(), true);
//                        Toast.makeText(register.this, "Sign Up Failed!  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Please fill all the field!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAlert(String title, String message, boolean error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(register.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    // don't forget to change the line below with the names of your Activities
                    if (!error) {
                        Intent intent = new Intent(register.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}