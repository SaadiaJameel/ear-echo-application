package com.example.login.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.model.StudentModel;
import com.example.login.net.studentService;
import com.example.login.retrofit.RetroFitService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    static StudentModel s = new StudentModel();
    String uname, pswd;
    TextView username;
    TextView password;

    MaterialButton signin;
    MaterialButton signupbtn;

    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            openSemesterAll();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);

        signin = (MaterialButton) findViewById(R.id.signinbtn);
        signupbtn = (MaterialButton) findViewById(R.id.signupbtn);
        mAuth= FirebaseAuth.getInstance();


        //use retrofit service
        RetroFitService retrofit = new RetroFitService();

        //create instance of employee api object
        studentService studentapi=retrofit.getRetrofit().create(studentService.class);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uname = username.getText().toString();
                pswd = password.getText().toString();

                // Check if email and password are empty or not
                if(TextUtils.isEmpty(uname)){
                    Toast.makeText(MainActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(pswd)){
                    Toast.makeText(MainActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(uname, pswd)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Login successful.",
                                            Toast.LENGTH_SHORT).show();
                                    openNavView();
                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                studentapi.getStudent(uname)
//                        .enqueue(new Callback<StudentModel>() {
//                            @Override
//                            public void onResponse(Call<StudentModel> call, Response<StudentModel> response) {
//
//                                if(!response.isSuccessful()){
//                                    Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
//                                }else {
//                                    s=(StudentModel)response.body();
//
//                                    if(s.getPassword().equals(pswd)){
//                                        Toast.makeText(MainActivity.this, "Login Succesfull!", Toast.LENGTH_SHORT).show();
//
//                                        openSemesterAll();
//                                    }else{
//                                        Toast.makeText(MainActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<StudentModel> call, Throwable t) {
//                                Toast.makeText(MainActivity.this, "No response", Toast.LENGTH_SHORT).show();
//                            }
//                        });
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
    }

    public void openActivity2(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }

    public void openSemesterAll(){
        Intent intent = new Intent(this, SemesterAllStudent.class);
        startActivity(intent);
    }

    public void openNavView(){
        Intent intent = new Intent(this, NavigationView.class);
        startActivity(intent);
    }

    public void openSettings(){
        Intent intent = new Intent(this, SemesterAllStudent.class);
        startActivity(intent);
    }

    public StudentModel getStudent(){
        return s;
    }
}