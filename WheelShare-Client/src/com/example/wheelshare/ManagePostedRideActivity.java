package com.example.wheelshare;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ManagePostedRideActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_posted_ride);
        // Show the Up button in the action bar.
        Intent intent = getIntent();
        String rideID = intent.getStringExtra("rideID");
        HTTPConnection con = new HTTPConnection("/getridebyid", "rideID");
        con.execute(rideID);
        String result = null;
        Ride ride = null;
        try
        {
            result = con.get(5, TimeUnit.SECONDS);
            ride = Ride.jsonToRides(result)[0];
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (ride == null)
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        else
        {
            ((TextView) findViewById(R.id.manage_ride_date)).setText(ride.date);
            ((TextView) findViewById(R.id.manage_ride_time)).setText(ride.time);
            ((TextView) findViewById(R.id.manage_ride_from)).setText(ride.startCity);
            ((TextView) findViewById(R.id.manage_ride_to)).setText(ride.destCity);
            ((TextView) findViewById(R.id.manage_ride_price)).setText("" + ride.price);
            ((TextView) findViewById(R.id.manage_ride_seats_available)).setText("" + ride.seats);
        }
        TableLayout table = (TableLayout) findViewById(R.id.table_manage_requests);
        for (final Participant participant : ride.participants)
        {
            TableRow row = new TableRow(this);

            TextView textview = new TextView(this);
            textview.setText(participant.username);
            row.addView(textview);
            final Ride ride2 = ride;
            if (!participant.isAccepted && !participant.isRejected && ride.seats > 0)
            {

                Button button_accept = new Button(this);
                button_accept.setText("Accept");
                button_accept.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        new HTTPConnection("/confirmride", "rideID", "result", "rsu_username")
                                .execute(ride2.id, "1", participant.username);
                        gotoManage();
                    }
                });
                Button button_decline = new Button(this);
                button_decline.setText("Decline");
                button_decline.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        new HTTPConnection("/confirmride", "rideID", "result", "rsu_username")
                                .execute(ride2.id, "0", participant.username);
                        gotoManage();
                    }
                });


                row.addView(button_accept);
                row.addView(button_decline);
            } else
            {

                String message = "";
                if (participant.isAccepted)
                    message = getString(R.string.accepted) + ", " + (participant.isPaid ? "" : getString(R.string.not)+" ") + getString(R.string.paid);
                else if (participant.isRejected || ride.seats == 0)
                    message = getString(R.string.rejected);

                TextView t = new TextView(this);
                t.setText(message);
                row.addView(t);
            }

            table.addView(row);
        }

        setupActionBar();
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
        getMenuInflater().inflate(R.menu.manage_posted_ride, menu);
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

    public void gotoManage()
    {
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
    }
}
