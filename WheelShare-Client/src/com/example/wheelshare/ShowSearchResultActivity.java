package com.example.wheelshare;

import org.json.JSONException;

import static com.example.wheelshare.Cache.searchInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowSearchResultActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search_result);
        Intent intent = getIntent();
        String search_result = intent.getStringExtra(SearchRideActivity.EXTRA_RESULT);
        Ride[] results = null;
        try
        {
            if (search_result == null || search_result.length() == 0)
                results = Cache.search_results;
            else
            {
                results = Ride.jsonToRides(search_result);
                Cache.search_results = results;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.search_result_view);

        if (results == null || results.length == 0)
        {
            String message = (results == null ? getString(R.string.no_response) : getString(R.string.no_results));
            TextView text = new TextView(this);
            text.setText(message);
            layout.addView(text);
            if (results != null)
            {
                Button buttonRequest = new Button(this);
                buttonRequest.setText(R.string.request_this_ride);
                buttonRequest.setOnClickListener(new OnClickListener()
                {
                    public void onClick(View v)
                    {
                        new HTTPConnection("/requestride", "username", "startcity", "destcity", "year", "month", "date")
                        {
                            @Override
                            protected void onPostExecute(String result)
                            {
                                super.onPostExecute(result);
                                if (result.equals("1"))
                                    Toast.makeText(getApplicationContext(), getString(R.string.request_success), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();

                            }
                        }.execute(Cache.username, searchInfo.startCity, searchInfo.destCity, searchInfo.year, searchInfo.month, searchInfo.day);
                        onBackPressed();
                    }
                });
                layout.addView(buttonRequest);
            }
            Button button = new Button(this);
            button.setText(getString(R.string.back));
            button.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onBackPressed();
                }
            });

            layout.addView(button);
        } else
        {

            for (final Ride currentRide : results)
            {
                Button button = new Button(this);
                button.setText(getString(R.string.date) + currentRide.date + "\n" + getString(R.string.time) + currentRide.time);
                button.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        displayRideDetails(currentRide);
                    }
                });
                layout.addView(button);
            }
        }
        setupActionBar();

    }

    private void displayRideDetails(Ride ride)
    {
        Intent nextIntent = new Intent(this, ShowRideActivity.class);
        nextIntent.putExtra("date", ride.date);
        nextIntent.putExtra("time", ride.time);
        nextIntent.putExtra("from", ride.startCity);
        nextIntent.putExtra("to", ride.destCity);
        nextIntent.putExtra("owner", ride.owner);
        nextIntent.putExtra("id", ride.id);
        nextIntent.putExtra("price", ((Integer) (ride.price)).toString());
        nextIntent.putExtra("seats", ((Integer) (ride.seats)).toString());
        startActivity(nextIntent);
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
        getMenuInflater().inflate(R.menu.show_ride, menu);
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
}
