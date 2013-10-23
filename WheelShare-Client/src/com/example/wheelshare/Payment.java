package com.example.wheelshare;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Payment extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        String message = intent.getStringExtra("price");
        ((TextView) findViewById(R.id.price)).setText(message);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.payment, menu);
        return true;
    }

    public void addCredit(View view)
    {
        int result;
        Intent newIntent = new Intent(this, WelcomeActivity.class);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
        EditText editText = (EditText) findViewById(R.id.creditCard);
        String message = editText.getText().toString();
        result = validCard(message);
        if (result == 1)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_credit), Toast.LENGTH_LONG).show();
            //paymentFail();
        } else if (!checkBox.isChecked())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.please_agree), Toast.LENGTH_LONG).show();
        } else
        {
            Toast.makeText(getApplicationContext(), getString(R.string.payment_placed), Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            new HTTPConnection("/payment", "username", "rideID")
            {
                @Override
                protected void onPostExecute(String result)
                {
                    // TODO Auto-generated method stub
                    super.onPostExecute(result);
                    if (result.equals("1"))
                        Toast.makeText(getApplicationContext(), getString(R.string.payment_processed), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            }.execute(intent.getStringExtra("username"), intent.getStringExtra("rideID"));
            startActivity(newIntent);
        }

    }

    private int validCard(String message)
    {
        int card[] = new int[16];
        int valid, sum = 0;
        if (message.length() != 16)
            valid = 1;
        else
        {
            for (int i = 0; i < 16; i++)
                card[i] = Character.getNumericValue(message.charAt(i));


            for (int i = 14; i >= 0; i -= 2)
            {
                card[i] = card[i] * 2;
                if (card[i] > 9)
                    card[i] = card[i] - 9;
            }
            for (int i = 0; i < card.length; i++)
            {
                sum = card[i] + sum;
            }
            if (sum % 10 == 0)
                valid = 2;
            else
                valid = 1;
        }
        return valid;
    }
}
