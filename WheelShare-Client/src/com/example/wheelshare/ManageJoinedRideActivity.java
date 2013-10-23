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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManageJoinedRideActivity extends Activity
{

    Ride ride = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_joined_ride);
        // Show the Up button in the action bar.
        Intent intent = getIntent();
        String rideID = intent.getStringExtra("rideID");
        HTTPConnection con = new HTTPConnection("/getridebyid", "rideID");
        con.execute(rideID);
        String result = null;
        Participant thisUser = null;
        try
        {
            result = con.get(5, TimeUnit.SECONDS);
            ride = Ride.jsonToRides(result)[0];
            thisUser = ride.getParticipant(Cache.username);
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
        if (ride == null || thisUser == null)
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
        else
        {
            ((TextView) findViewById(R.id.manage_ride_date)).setText(ride.date);
            ((TextView) findViewById(R.id.manage_ride_time)).setText(ride.time);
            ((TextView) findViewById(R.id.manage_ride_from)).setText(ride.startCity);
            ((TextView) findViewById(R.id.manage_ride_to)).setText(ride.destCity);
            ((TextView) findViewById(R.id.manage_ride_price)).setText("" + ride.price);
            ((TextView) findViewById(R.id.manage_ride_seats_available)).setText("" + ride.seats);

            String str = "";
            if (thisUser.isAccepted)
            {
                str = getString(R.string.accepted);
                ((Button) findViewById(R.id.button_pay)).setEnabled(true);
            } else if (thisUser.isPaid)
                str = getString(R.string.paid);
            else if (thisUser.isRejected || ride.seats == 0)
                str = getString(R.string.rejected);
            else
                str = getString(R.string.pending);

            ((TextView) findViewById(R.id.textview_manage_status)).setText(str);
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

    public void pay(View view)
    {
        //((TextView)findViewById(R.id.manage_ride_from) );
        Intent intent = new Intent(this, Payment.class);
        TextView editText = (TextView) findViewById(R.id.manage_ride_price);
        String price = editText.getText().toString();
        intent.putExtra("price", price);
        intent.putExtra("username", Cache.username);
        intent.putExtra("rideID", ride.id);
        startActivity(intent);
    }

    public void contact(View view)
    {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("to", ride.owner);
        startActivity(intent);
    }
}
