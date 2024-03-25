package com.example.c07defest;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.c07defest.databinding.FragmentRegisterBinding;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class RegisterFragment extends Fragment
{
    String query;
    Cursor cursor;
    SQLiteDatabase sqlDB;

    private FragmentRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setAllSpinner();
        setListener();
        registerStudent();


    }

    private boolean checkIfNotEmpty()
    {
        boolean name = TextUtils.isEmpty(binding.regisStudnameEdtx.getText().toString().trim());
        boolean bday = TextUtils.isEmpty(binding.regisBirthdayEdtx.getText().toString().trim());
        boolean uname = TextUtils.isEmpty(binding.regisUsernameEdtx.getText().toString().trim());
        boolean pass = TextUtils.isEmpty(binding.regisPasswordEdtx.getText().toString().trim());

        if(name || bday || uname || pass)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    private void registerStudent()
    {
        binding.registerBtn.setOnClickListener(register -> {
            if(checkIfNotEmpty())
            {
                ConnectDB();
                String studentName = binding.regisStudnameEdtx.getText().toString().trim();
                String course = String.valueOf(binding.courseSpinner.getSelectedItem());
                Date bday = formatDate();
                String status = String.valueOf(binding.statusSpinner.getSelectedItem());
                String uname = binding.regisUsernameEdtx.getText().toString().trim();
                String pass = binding.regisPasswordEdtx.getText().toString().trim();

                query = "INSERT INTO studentsTBl(studentName, course, birthday, status, username, password) " +
                        "VALUES('"+studentName+"','"+course+"','"+bday+"','"+status+"','"+uname+"','"+pass+"')";

                sqlDB.execSQL(query);


                cursor = sqlDB.rawQuery("SELECT * FROM studentsTbl WHERE studentName = ?", new String[]{studentName});
                if(cursor.moveToFirst())
                {
                    String name = cursor.getString(1);
                    Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                }

                CloseDB();
            }
            else
            {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Date formatDate()
    {
        String bday = binding.regisBirthdayEdtx.getText().toString().trim();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try
        {
            return simpleDateFormat.parse(bday);
        } catch (ParseException e)
        {
            throw new RuntimeException();
        }
    }

    private void setListener()
    {
        binding.regisBirthdayLayout.setEndIconOnClickListener(date -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {
                    binding.regisBirthdayEdtx.setText(
                            String.format("%02d", month+1) + "/" +
                            String.format("%02d", dayOfMonth) + "/" +
                            String.format("%04d", year)
                    );
                }
            }, 2024, 2, 26);
            datePickerDialog.show();
        });
    }

    private void setAllSpinner()
    {
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(getContext(), R.array.course_array, android.R.layout.simple_spinner_dropdown_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.courseSpinner.setAdapter(courseAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(getContext(), R.array.status_array, android.R.layout.simple_spinner_dropdown_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusSpinner.setAdapter(statusAdapter);
    }


    public void ConnectDB()
    {
        sqlDB = getContext().openOrCreateDatabase("students", MODE_PRIVATE, null);
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