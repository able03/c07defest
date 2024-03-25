package com.example.c07defest;

public class StudentAdapterModel
{
    private int id;
    private String name;

    public StudentAdapterModel(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
