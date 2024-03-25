package com.example.c07defest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.c07defest.databinding.ActivityEditBinding;


public class EditActivity extends AppCompatActivity
{

    private ActivityEditBinding binding;
    String query;
    Cursor cursor;
    SQLiteDatabase sqlDB;
    static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);
        setAllSpinner();
        setListener();
        setEditData();
        setUpdate();
    }

    private void setListener()
    {
        binding.editBirthdayLayout.setEndIconOnClickListener(icon -> { DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                binding.editBirthdayEdtx.setText(
                        String.format("%02d", month+1) + "/" +
                                String.format("%02d", dayOfMonth) + "/" +
                                String.format("%04d", year)
                );
            }
        }, 2024, 2, 26);
            datePickerDialog.show();
        });

        binding.goBackBtn.setOnClickListener(goBackBtn -> {
            finish();
        });
    }

    private void setUpdate()
    {
        binding.updateBtn.setOnClickListener(update -> {
            if(checkIfNotEmpty())
            {
                String studname = binding.editStudnameEdtx.getText().toString().trim();
                String course = String.valueOf(binding.editCourseSpinner.getSelectedItem());
                String bday = binding.editBirthdayEdtx.getText().toString().trim();
                String status = String.valueOf(binding.editStatusSpinner.getSelectedItem());
                String username = binding.editUsernameEdtx.getText().toString().trim();
                String password = binding.editPasswordEdtx.getText().toString().trim();

                ConnectDB();
                cursor = sqlDB.rawQuery("UPDATE studentsTbl SET studentName = ?, course = ?, birthday = ?, status = ?, username = ?, password = ? WHERE studentName = ?",
                        new String[]{studname, course, bday, status, username, password, name});
                cursor.moveToFirst();
                if(cursor.getCount() == 0)
                {

                    Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }

                CloseDB();
            }
            else
            {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkIfNotEmpty()
    {
        boolean studname = TextUtils.isEmpty(binding.editStudnameEdtx.getText().toString().trim());
        boolean bday = TextUtils.isEmpty(binding.editBirthdayEdtx.getText().toString().trim());
        boolean uname = TextUtils.isEmpty(binding.editUsernameEdtx.getText().toString().trim());
        boolean pass = TextUtils.isEmpty(binding.editPasswordEdtx.getText().toString().trim());

        if(studname || bday || uname || pass)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    private void setEditData()
    {
        ConnectDB();
         name = getIntent().getStringExtra("name");
        cursor = sqlDB.rawQuery("SELECT * FROM studentsTbl WHERE studentName = ?", new String[]{name});
        if(cursor.moveToNext())
        {
            String name1 = cursor.getString(1);
            int course1 = checkCourseItem(cursor.getString(2));
            String bday1 = cursor.getString(3);
            int status1 = checkStatusItem(cursor.getString(4));
            String uname1 = cursor.getString(5);
            String pass1 = cursor.getString(6);

            binding.editStudnameEdtx.setText(name1);
            binding.editCourseSpinner.setSelection(course1);
            binding.editBirthdayEdtx.setText(bday1);
            binding.editStatusSpinner.setSelection(status1);
            binding.editUsernameEdtx.setText(uname1);
            binding.editPasswordEdtx.setText(pass1);
        }
        else
        {
            Toast.makeText(this, "no data found", Toast.LENGTH_SHORT).show();
        }
        CloseDB();
    }

    private int checkCourseItem(String course)
    {
        switch(course)
        {
            case "BSIT": return 0;
            case "BSCPE": return 1;
            case "BSA": return 2;
            default: return  0;
        }
    }

    private int checkStatusItem(String status)
    {
        switch(status)
        {
            case "Regular": return 0;
            case "Irregular": return 1;
            default: return  0;
        }
    }

    private void setAllSpinner()
    {
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(this, R.array.course_array, android.R.layout.simple_spinner_dropdown_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editCourseSpinner.setAdapter(courseAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_dropdown_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.editStatusSpinner.setAdapter(statusAdapter);
    }

    @Override
    public void finish()
    {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }

    public void ConnectDB()
    {
        sqlDB = this.openOrCreateDatabase("students", MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS studentsTbl(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentName VARCHAR(50), " +
                "course VARCHAR(50), " +
                "birthday DATE, " +
                "status VARCHAR(50)," +
                "username VARCHAR(50)," +
                "password VARCHAR(50))";
        sqlDB.execSQL(query);
    }

    public void CloseDB()
    {
        sqlDB.close();
    }
}