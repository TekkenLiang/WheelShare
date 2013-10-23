package com.example.wheelshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ReceiveMessageActivity extends Activity
{

    String from;

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.avtivity_receive_message);

        from = getIntent().getStringExtra("from");
        String message = getIntent().getStringExtra("message");
        ((TextView) findViewById(R.id.contact_username2)).setText(from);
        ((EditText) findViewById(R.id.contact_message2)).setText(message);
        ((EditText) findViewById(R.id.contact_message2)).setEnabled(false);

    }

    public void reply(View view) throws Throwable
    {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("to", from);
        startActivity(intent);
        this.finish();
        this.finalize();
    }

    public void cancel(View view) throws Throwable
    {
    	Intent intent = new Intent(this, ManageActivity.class);
        startActivity(intent);
        this.finalize();
    }
}
