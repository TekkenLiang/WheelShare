/*
 * Victor Kretov
 */
package com.example.wheelshare;

public class SearchInfo
{

    protected String startCity;
    protected String destCity;
    protected String year;
    protected String month;
    protected String day;

    public SearchInfo(String startCity, String destCity, String year, String month, String day)
    {
        this.startCity = startCity;
        this.destCity = destCity;
        this.year = year;
        this.month = month;
        this.day = day;
    }
}
