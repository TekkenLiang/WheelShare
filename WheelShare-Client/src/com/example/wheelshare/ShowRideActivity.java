package com.example.wheelshare;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShowRideActivity extends Activity
{

    private String rideId;
    boolean isInvited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ride);
        Intent intent = getIntent();
        isInvited = intent.getBooleanExtra("isInvited", false);

        //String date = intent.getStringExtra("date");
        //TextView textview_date = (TextView) findViewById(R.id.show_ride_date);
        //textview_date.setText(date);
        rideId = intent.getStringExtra("id");

        ((TextView) findViewById(R.id.show_ride_date)).setText(intent.getStringExtra("date"));
        ((TextView) findViewById(R.id.show_ride_time)).setText(intent.getStringExtra("time"));
        ((TextView) findViewById(R.id.show_ride_from)).setText(intent.getStringExtra("from"));
        ((TextView) findViewById(R.id.show_ride_to)).setText(intent.getStringExtra("to"));
        ((TextView) findViewById(R.id.show_ride_owner)).setText(intent.getStringExtra("owner"));
        ((TextView) findViewById(R.id.show_ride_price)).setText(intent.getStringExtra("price"));
        ((TextView) findViewById(R.id.show_ride_seats_available)).setText(intent.getStringExtra("seats"));

        // Show the Up button in the action bar.
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

    public void join_request(View view)
    {
        new HTTPConnection("/join", "rideID", "participant", "isInvited")
        {
            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                if (result.equals("1"))
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.join_sent), Toast.LENGTH_LONG).show();
                    gotoManage();
                    return;
                } else if (result.equals("2"))
                    Toast.makeText(getApplicationContext(), getString(R.string.seats_full), Toast.LENGTH_LONG).show();
                else if (result.equals("3"))
                    Toast.makeText(getApplicationContext(), getString(R.string.already_requested), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }.execute(rideId, Cache.username, isInvited?"1":"0");

    }

    public void gotoManage()
    {
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
    }
}
