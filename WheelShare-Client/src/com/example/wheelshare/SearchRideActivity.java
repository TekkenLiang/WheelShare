package com.example.wheelshare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchRideActivity extends FragmentActivity
{

    public static String EXTRA_RESULT = "com.example.wheelshare.SEARCH_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ride);
        // Show the Up button in the action bar.
        setupActionBar();

        TextView textview_date = (TextView) findViewById(R.id.textview_date_search);
        textview_date.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePicker(v);
            }
        });

    }

    public void searchRequest(View view)
    {
        //setContentView(R.layout.activity_show_search_result);

        EditText editFromCity = (EditText) findViewById(R.id.edittext_from_search);
        EditText editDestCity = (EditText) findViewById(R.id.edittext_dest_search);
        String fromCity = editFromCity.getText().toString();
        String destCity = editDestCity.getText().toString();
        TextView textMonthDateYear = (TextView) findViewById(R.id.textview_date_search);
        //TextView textTime = (TextView) findViewById(R.id.textview_time_search);
        String[] monthDateYear = textMonthDateYear.getText().toString().split("/");
        if (monthDateYear == null || monthDateYear[0].length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.date_missing), Toast.LENGTH_LONG).show();
            return;
        }

        String month = monthDateYear[0];
        String date = monthDateYear[1];
        String year = monthDateYear[2];

        if (fromCity.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.start_city_missing), Toast.LENGTH_LONG).show();
            return;
        }
        if (destCity.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.dest_city_missing), Toast.LENGTH_LONG).show();
            return;
        }

        HTTPConnection con = new HTTPConnection("/searchride", "username", "startcity", "destcity", "year", "month", "date")
        {
            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                show_results(result);
            }
        };
        con.execute(Cache.username, fromCity.trim().toLowerCase(), destCity.trim().toLowerCase(), year, month, date);
        Cache.searchInfo = new SearchInfo(fromCity.trim().toLowerCase(), destCity.trim().toLowerCase(), year, month, date);
    }

    public void show_results(String string)
    {
        Intent intent = new Intent(this, ShowSearchResultActivity.class);
        intent.putExtra(EXTRA_RESULT, string);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    private void setupActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showTimePicker(View view)
    {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setTargetTextView((TextView) view);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePicker(View view)
    {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetTextView((TextView) view);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void resetSearch(View view)
    {
        EditText editFromCity = (EditText) findViewById(R.id.edittext_from_search);
        EditText editDestCity = (EditText) findViewById(R.id.edittext_dest_search);
        TextView textMonthDateYear = (TextView) findViewById(R.id.textview_date_search);
        //TextView textTime = (TextView) findViewById(R.id.textview_time_search);
        editFromCity.setText(null);
        editDestCity.setText(null);
        textMonthDateYear.setText(null);
        //textTime.setText(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_ride, menu);
        return true;
    }
}
