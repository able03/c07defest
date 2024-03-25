package com.example.c07defest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.c07defest.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity
{
    private ActivityLoginBinding binding;
    private String query;
    private Cursor cursor;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        checkAdmin();
        login();
    }

    private void login()
    {
       binding.loginBtn.setOnClickListener(login -> {
           if(checkIfNotEmpty())
           {
               ConnectDB();
               String username = binding.loginUsernameEdtx.getText().toString().trim();
               String password = binding.loginPasswordEdtx.getText().toString().trim();

               cursor = sqlDB.rawQuery("SELECT * FROM adminTbl WHERE username = ? AND password = ?", new String[]{username, password});
               if(cursor.moveToNext())
               {
                   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                   startActivity(intent);
               }
               else
               {
                   Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show();
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
        boolean uname = TextUtils.isEmpty(binding.loginUsernameEdtx.getText().toString());
        boolean pass = TextUtils.isEmpty(binding.loginPasswordEdtx.getText().toString());

        if(uname || pass)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    private void checkAdmin()
    {
        ConnectDB();
        String username = "admin";
        String password = "admin1234";
        cursor = sqlDB.rawQuery("SELECT * FROM adminTbl WHERE username = ? AND password = ?",
                new String[]{username, password});
        cursor.moveToFirst();
        if(cursor.getCount() == 0)
        {
            query = "INSERT INTO adminTbl(username, password) VALUES('"+username+"', '"+password+"')";
            sqlDB.execSQL(query);
        }


        CloseDB();
    }

    public void ConnectDB()
    {
        sqlDB = this.openOrCreateDatabase("students", MODE_PRIVATE, null);
        query = "CREATE TABLE IF NOT EXISTS adminTbl(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username VARCHAR(50), " +
                "password VARCHAR(50))";
        sqlDB.execSQL(query);
    }

    public void CloseDB()
    {
        sqlDB.close();
    }
}