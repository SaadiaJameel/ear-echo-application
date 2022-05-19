package com.example.login.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.model.CourseModel;
import com.example.login.model.SemesterModel;
import com.example.login.model.StudentModel;
import com.example.login.net.addSemService;
import com.example.login.net.courseService;
import com.example.login.net.studentService;
import com.example.login.retrofit.RetroFitService;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSemester extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //ListView superListView;

    public static long semi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_semester);

        //superListView = findViewById(R.id.superListView);

        initializecomponents();
    }

    private void initializecomponents() {

        ImageButton addCoursebtn = findViewById(R.id.imageButton2);
        EditText semNumber = findViewById(R.id.semnumber);
        //static SemesterModel semester = new SemesterModel();

//        if(semNumStatic != -1){
//            semNumber.setText(semNumStatic);
//        }

//        addCoursebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openAddCourse();
//            }
//        });

        //use retrofit service
        RetroFitService retrofit1 = new RetroFitService();

        //create instance of semester api object
        addSemService semesterapi=retrofit1.getRetrofit().create(addSemService.class);
//        ///NEW
//        courseService courseapi = retrofit1.getRetrofit().create(courseService.class);
//
//        if(flag != 0){
//            courseapi.getcourses(semi)
//                    .enqueue(new Callback<List<CourseModel>>() {
//                        @Override
//                        public void onResponse(Call<List<CourseModel>> call, Response<List<CourseModel>> response) {
//                            List<CourseModel> myheroList = response.body();
//                            String[] oneHeroes = new String[myheroList.size()];
//                            for (int i = 0; i < myheroList.size(); i++) {
//                                oneHeroes[i] = myheroList.get(i).getCoursename();
//                            }
//                            //superListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, oneHeroes));
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<CourseModel>> call, Throwable t) {
//
//                        }
//                    });
//        }
//
        //when addCourseBtn button is pressed
        addCoursebtn.setOnClickListener(view -> {

            String sem = String.valueOf(semNumber.getText());
            int semNum = Integer.parseInt(sem);

            //Semester student = new StudentModel();

            SemesterModel semester = new SemesterModel();

            semester.setSemnumber(semNum);
            semester.setFieldgroup(MainActivity.s.getFieldgroup());
            semester.setIscommited(false);



            if(TextUtils.isEmpty(sem)){
                Toast.makeText(this, "Enter Semester Number", Toast.LENGTH_SHORT).show();
            }else{

                //give api student object to save
                semesterapi.addSemNumber(semester)
                        .enqueue(new Callback<SemesterModel>() {
                            @Override
                            public void onResponse(Call<SemesterModel> call, Response<SemesterModel> response) {
                                semi = response.body().getId();
                                Toast.makeText(AddSemester.this, "Add Course", Toast.LENGTH_SHORT).show();
                                openAddCourse();
                            }

                            @Override
                            public void onFailure(Call<SemesterModel> call, Throwable t) {
                                Toast.makeText(AddSemester.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void openAddCourse(){

        Intent intent = new Intent(this, AddCourse.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){

    }

}