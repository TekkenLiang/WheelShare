package com.example.wheelshare;

import org.json.JSONException;

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
import android.widget.TableLayout;
import android.widget.TextView;

public class ManageActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        // Show the Up button in the action bar.
        setupActionBar();

        Intent postIntent = new Intent(this, ManagePostedRideActivity.class);
        Intent joinIntent = new Intent(this, ManageJoinedRideActivity.class);
        start("/mypostedrides", R.id.table_manage_posted_rides, postIntent);
        start("/myjoinedrides", R.id.table_manage_joined_rides, joinIntent);

    }

    private void start(String url, final int tableID, final Intent activityObj)
    {
        HTTPConnection con = new HTTPConnection(url, "username")
        {
            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                show_results(result, tableID, activityObj);
            }
        };
        con.execute(Cache.username);
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
        getMenuInflater().inflate(R.menu.manage, menu);
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

    public void show_results(String string, int tableID, final Intent activityObj)
    {
        Ride[] results = null;
        try
        {
            results = Ride.jsonToRides(string);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        TableLayout table = (TableLayout) findViewById(tableID);

        if (results == null || results.length == 0)
        {
            String message = (results == null ? getString(R.string.no_response) : getString(R.string.no_results));
            TextView text = new TextView(this);
            text.setText(message);

            table.addView(text);
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
                        displayRideDetails(currentRide, activityObj);
                    }
                });
                table.addView(button);
            }
        }
    }

    private void displayRideDetails(Ride ride, final Intent activityObj)
    {
        activityObj.putExtra("rideID", ride.id);
        startActivity(activityObj);
    }
}
