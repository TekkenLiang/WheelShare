package com.example.wheelshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity
{

    String toUser;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_contact);

        toUser = getIntent().getStringExtra("to");
        ((TextView) findViewById(R.id.contact_username)).setText(toUser);


    }

    public void contact(View view)
    {
        new HTTPConnection("/contact", "to", "from", "message")
        {
            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
            }
        }.execute(toUser, Cache.username, ((TextView) findViewById(R.id.contact_message)).getText().toString());
        Toast.makeText(getApplicationContext(), getString(R.string.message_sent), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void cancel(View view)
    {
    	Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
    }
}
