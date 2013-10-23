package com.example.wheelshare;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class PostRideActivity extends FragmentActivity
{
	
	private String fromCity, toCity, date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ride);
        TextView textview_time = (TextView) findViewById(R.id.textview_time_post);
        textview_time.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showTimePicker(v);
            }
        });

        TextView textview_date = (TextView) findViewById(R.id.textview_date_post);
        textview_date.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDatePicker(v);
            }
        });
        setupActionBar();

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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_ride, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void post_request(View view)
    {
        EditText editFromCity = (EditText) findViewById(R.id.edittext_from_post);
        EditText editDestCity = (EditText) findViewById(R.id.edittext_dest_post);
        EditText editPrice = (EditText) findViewById(R.id.edittext_price_post);
        EditText editSeat = (EditText) findViewById(R.id.edittext_seat_post);

        String fromCity = editFromCity.getText().toString();
        String destCity = editDestCity.getText().toString();
        String price = editPrice.getText().toString();
        String seats = editSeat.getText().toString();

        TextView textMonthDateYear = (TextView) findViewById(R.id.textview_date_post);
        TextView textTime = (TextView) findViewById(R.id.textview_time_post);
        String[] monthDateYear = textMonthDateYear.getText().toString().split("/");
        if (monthDateYear[0].length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.date_missing), Toast.LENGTH_LONG).show();
            return;
        }
        String month = monthDateYear[0];
        String date = monthDateYear[1];
        String year = monthDateYear[2];
        String[] hourMinute = textTime.getText().toString().split(":");
        if (hourMinute[0].length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.ride_time_missing), Toast.LENGTH_LONG).show();
            return;
        }
        String hour = hourMinute[0];
        String minute = hourMinute[1];


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
        if (price.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.price_missing), Toast.LENGTH_LONG).show();
            return;
        }
        if (seats.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.seats_number_missing), Toast.LENGTH_LONG).show();
            return;
        }

        this.fromCity = fromCity.trim().toLowerCase();
        this.toCity = destCity.trim().toLowerCase();
        this.date = month+"/"+date+"/"+year;

        new HTTPConnection("/postride", "owner", "startcity", "destcity", "year", "month", "date", "hour", "minute", "price", "availSeatNum")
        {
            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                if (result == null)
                    Toast.makeText(getApplicationContext(), getString(R.string.no_response), Toast.LENGTH_LONG).show();
                else if (result.equals("2"))
                	Toast.makeText(getApplicationContext(), getString(R.string.time_incorrect), Toast.LENGTH_LONG).show();
                else if (result.equals("1"))
                {
                    onBackPressed();
                } else
                    showRequests(result);

                Toast.makeText(getApplicationContext(), getString(R.string.ride_been_posted), Toast.LENGTH_LONG).show();
            }
        }.execute(Cache.username, fromCity.trim().toLowerCase(), destCity.trim().toLowerCase(), year, month, date, hour, minute, price, seats);

    }

    public void reset(View view)
    {
        ((EditText) findViewById(R.id.edittext_from_post)).setText("");
        ((EditText) findViewById(R.id.edittext_dest_post)).setText("");
        ((EditText) findViewById(R.id.edittext_price_post)).setText("");
        ((EditText) findViewById(R.id.edittext_seat_post)).setText("");

        ((TextView) findViewById(R.id.textview_date_post)).setText("");
        ((TextView) findViewById(R.id.textview_time_post)).setText("");
    }

    public void showRequests(String result)
    {
        Intent newIntent = new Intent(this, InviteActivity.class);
        newIntent.putExtra("requestJson", result);
        newIntent.putExtra("toCity", toCity);
        newIntent.putExtra("fromCity", fromCity);
        newIntent.putExtra("date", date);
        startActivity(newIntent);
    }
}
