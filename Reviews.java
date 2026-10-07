package com.college;

public class Reviews {
    String name;
    int year;
    String reviewString;
    void getdata(String n, int y, String r)
    {
        name = n;
        year = y;
        reviewString = r;
    }
    void getdata()
    {
        System.out.println("Name:"+name);
        System.out.println("Year:"+year);
        System.out.println("Review:"+reviewString);
    }
}
