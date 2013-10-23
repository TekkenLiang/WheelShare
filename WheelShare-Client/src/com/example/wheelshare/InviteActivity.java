package com.example.wheelshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;

public class InviteActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String requestJson = getIntent().getStringExtra("requestJson");
        setContentView(R.layout.activity_invite_request);
        RideRequest[] requests = null;
        try
        {
            requests = RideRequest.jsonToRequest(requestJson);
        } catch (JSONException ex)
        {
            ex.printStackTrace();
        }
        if (requests == null)
            return;

        Intent intent = getIntent();
        ((TextView) findViewById(R.id.invite_ride_date)).setText(intent.getStringExtra("date"));
        ((TextView) findViewById(R.id.invite_ride_from)).setText(intent.getStringExtra("fromCity"));
        ((TextView) findViewById(R.id.invite_ride_to)).setText(intent.getStringExtra("toCity"));
        
        TableLayout table = (TableLayout) findViewById(R.id.table_invite);

        for (final RideRequest rideRequest : requests)
        {
            TableRow row = new TableRow(this);
            TextView t = new TextView(this);
            t.setText(rideRequest.username);

            Button button = new Button(this);
            button.setText(R.string.title_activity_invite);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    new HTTPConnection("/invite", "username", "requestID").execute(Cache.username, rideRequest.id);
                    Toast.makeText(getApplicationContext(), getString(R.string.invited), Toast.LENGTH_LONG).show();
                }
            });

            row.addView(t);
            row.addView(button);
            table.addView(row);
        }

        TableRow row = new TableRow(this);
        Button button = new Button(this);
        button.setText(getString(R.string.back));
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gotoManage();
            }
        });
        row.addView(button);
        table.addView(row);


    }

    public void gotoManage()
    {
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
    }
}
