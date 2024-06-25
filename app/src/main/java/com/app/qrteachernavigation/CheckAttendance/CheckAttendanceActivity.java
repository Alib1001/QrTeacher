package com.app.qrteachernavigation.CheckAttendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.qrteachernavigation.API.ApiService;
import com.app.qrteachernavigation.API.RetrofitClient;
import com.app.qrteachernavigation.Adapters.StudentsListAdapter;
import com.app.qrteachernavigation.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.app.qrteachernavigation.models.Student;

public class CheckAttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerViewStudents;
    private StudentsListAdapter studentsListAdapter;
    private List<Student> studentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_attendance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        long timetableId = getIntent().getLongExtra("timetableId", 0);

        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        studentsListAdapter = new StudentsListAdapter(studentList);
        recyclerViewStudents.setAdapter(studentsListAdapter);

        getStudentsByTimetableId(timetableId);
    }

    private void getStudentsByTimetableId(long timetableId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<List<Integer>> callGetStudents = apiService.getStudentsFromTimetable(timetableId);
        callGetStudents.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    List<Integer> studentIds = response.body();
                    if (studentIds != null) {
                        for (Integer studentId : studentIds) {
                            getStudentById(Long.valueOf(studentId));
                        }
                    }
                } else {
                    Toast.makeText(CheckAttendanceActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(CheckAttendanceActivity.this, "Error loading students: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStudentById(Long studentId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance(this).create(ApiService.class);
        Call<Student> callGetStudent = apiService.getStudentById(studentId);
        callGetStudent.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    Student student = response.body();
                    if (student != null) {
                        studentList.add(student);
                        studentsListAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(CheckAttendanceActivity.this, "Failed to load student", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Toast.makeText(CheckAttendanceActivity.this, "Error loading student: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

