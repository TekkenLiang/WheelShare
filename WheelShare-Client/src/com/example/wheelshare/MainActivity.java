package com.example.wheelshare;

import static com.example.wheelshare.Cache.SENDER_ID;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        final String regId = GCMRegistrar.getRegistrationId(this);

        if (regId.equals(""))
        {
            GCMRegistrar.register(this, SENDER_ID);
        }

        Cache.regId = GCMRegistrar.getRegistrationId(this);

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void login_request(View view)
    {
        EditText editUsername = (EditText) findViewById(R.id.edit_username);
        EditText editPassword = (EditText) findViewById(R.id.edit_password);
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        if (username.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.username_missing), Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.password_missing), Toast.LENGTH_LONG).show();
            return;
        }

        HTTPConnection con = new HTTPConnection("/login", "username", "password", "deviceID");
        con.execute(username, password, Cache.regId);

        String result = null;

        try
        {
            result = con.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        }

        if (result == null || result.length() == 0)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.no_response), Toast.LENGTH_LONG).show();
        } else if (result.equals("1"))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.username_not_exist), Toast.LENGTH_LONG).show();
        } else if (result.equals("2"))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.password_not_correct), Toast.LENGTH_LONG).show();
        } else if (result.equals("3"))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.log_in_sucessfully), Toast.LENGTH_LONG).show();
            onLoginSuccess(username);
        } else
        {
            Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
        }

    }

    public void register_request(View view)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        try
        {
            startActivity(intent);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void onLoginSuccess(String username)
    {
        Intent newIntent = new Intent(this, WelcomeActivity.class);
        Cache.username = username;
        startActivity(newIntent);
    }
}
