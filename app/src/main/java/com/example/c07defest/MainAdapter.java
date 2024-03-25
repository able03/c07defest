package com.example.c07defest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>
{
    private Context context;
    private List<StudentAdapterModel> students;

    private String query;
    private Cursor cursor;
    private SQLiteDatabase sqlDB;

    public MainAdapter(Context context)
    {
        this.context = context;
    }
    public void setStudents(List<StudentAdapterModel> students)
    {
        this.students = students;
        notifyDataSetChanged();
    }

    public void setFilteredStudents(List<StudentAdapterModel> students)
    {
        this.students = students;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position)
    {
        int id = students.get(position).getId();
        String name = students.get(position).getName();

        holder.id.setText(String.valueOf(id));
        holder.name.setText(name);

        holder.edit.setOnClickListener(editItem -> {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra("name", name);
            context.startActivity(intent);
        });

        holder.delete.setOnClickListener(deleteItem -> {
            ConnectDB();
            sqlDB.delete("studentsTbl", "studentName = ?", new String[]{name});
            CloseDB();

            students.remove(position);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount()
    {
        return students.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView id, name;
        ImageView edit, delete;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            id = itemView.findViewById(R.id.idTxt);
            name = itemView.findViewById(R.id.nameTxt);
            edit = itemView.findViewById(R.id.editImg);
            delete = itemView.findViewById(R.id.deleteImg);

        }
    }

    public void ConnectDB()
    {
        sqlDB = context.openOrCreateDatabase("students", context.MODE_PRIVATE, null);
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
