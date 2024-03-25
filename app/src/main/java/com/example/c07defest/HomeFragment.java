package com.example.c07defest;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.c07defest.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
{
    private FragmentHomeBinding binding;
    private String query;
    private Cursor cursor;
    private SQLiteDatabase sqlDB;
    private MainAdapter mainAdapter;

    private List<StudentAdapterModel> students;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
       View view = binding.getRoot();
       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        populateRecyclerView();
        searchListener();
    }

    private void searchListener()
    {
        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                filteredText(newText);
                return true;
            }
        });
    }

    private void filteredText(String newText)
    {
        List<StudentAdapterModel> filteredStudents = new ArrayList<>();
        for(StudentAdapterModel student : students)
        {
            if(student.getName().toLowerCase().contains(newText.toLowerCase()))
            {
                filteredStudents.add(student);
            }
        }
        mainAdapter.setFilteredStudents(filteredStudents);
    }



    private List<StudentAdapterModel> getAllData()
    {
        students = new ArrayList<>();
        ConnectDB();
        cursor = sqlDB.rawQuery("SELECT * FROM studentsTbl", null);
        while(cursor.moveToNext())
        {
            int id = Integer.parseInt(cursor.getString(0));
            String name = cursor.getString(1);
            students.add(new StudentAdapterModel(id, name));
        }
        CloseDB();

        return students;
    }

    private void populateRecyclerView()
    {
        mainAdapter = new MainAdapter(getContext());
        List<StudentAdapterModel> students = getAllData();

        mainAdapter.setStudents(students);
        binding.homeRV.setAdapter(mainAdapter);
        binding.homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
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