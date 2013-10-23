package com.example.wheelshare;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class RegistrationActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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
        getMenuInflater().inflate(R.menu.registration, menu);
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

    public void signup_request(View view)
    {
        EditText editUsername = (EditText) findViewById(R.id.edit_username);
        EditText editPassword = (EditText) findViewById(R.id.edit_password);
        EditText editFirstname = (EditText) findViewById(R.id.edit_firstname);
        EditText editLastname = (EditText) findViewById(R.id.edit_lastname);
        EditText editLicence = (EditText) findViewById(R.id.edit_licence);
        EditText editConfirmPassword = (EditText) findViewById(R.id.edit_confirmpassword);
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();
        String licence = editLicence.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        //TODO
        if (username.length() == 0)
            Toast.makeText(getApplicationContext(), getString(R.string.username_missing), Toast.LENGTH_LONG).show();
        else if (password.length() == 0)
            Toast.makeText(getApplicationContext(), getString(R.string.password_missing), Toast.LENGTH_LONG).show();
        else if (firstname.length() == 0)
            Toast.makeText(getApplicationContext(), getString(R.string.firstname_missing), Toast.LENGTH_LONG).show();
        else if (lastname.length() == 0)
            Toast.makeText(getApplicationContext(), getString(R.string.lastname_missing), Toast.LENGTH_LONG).show();
        else if (!password.equals(confirmPassword))
            Toast.makeText(getApplicationContext(), getString(R.string.password_not_match), Toast.LENGTH_LONG).show();
        else
        {
            HTTPConnection con = new HTTPConnection("/signup", "username", "password", "firstname", "lastname", "licence")
            {
                @Override
                protected void onPostExecute(String result)
                {
                    if (result == null || result.length() == 0)
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_response), Toast.LENGTH_LONG).show();
                    } else if (result.equals("1"))
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.username_already_existed), Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.new_user_created) + result, Toast.LENGTH_LONG).show();
                        onRegistrationSuccess();
                    }
                }
            };
            con.execute(username, password, firstname, lastname, licence);
        }
    }

    public void cancel_request(View view)
    {
        onBackPressed();
    }

    private void onRegistrationSuccess()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
