package com.techneaux.techneauxmobileapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private static Button submitLoginInfo;
    private static EditText username;
    private static EditText password;
    private static TextView CSNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submitLoginInfo = (Button) findViewById(R.id.loginSubmit);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        CSNumber = (TextView) findViewById(R.id.CSNumberText);




        submitLoginInfo.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
               String sUsername = username.getText().toString();
               String sPassword = password.getText().toString();
                if(sUsername.matches(""))
                {
                    Toast.makeText(getApplicationContext(), "Username not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                else if(sPassword.matches(""))
                {
                    Toast.makeText(getApplicationContext(), "Password not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Username: " + sUsername + " Password: " + sPassword,
                            Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
