package com.techneaux.techneauxmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static Button submitLoginInfo; //object to link to the submit login info layout object
    private static EditText username; //object to link to the username layout object
    private static EditText password; //object to link to the password layout object
    private static TextView CSNumber; //object to link to the customer service number  layout object
    public static final String MY_PREFS_NAME = "MyPrefsFile"; //file to store prefs for the app.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //********Initialize the layout objects to a variable for manipulation********
        submitLoginInfo = (Button) findViewById(R.id.loginSubmit);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        CSNumber = (TextView) findViewById(R.id.CSNumberText);
        setButtons();
        //********End Initialize the layout objects to a variable for manipulation********


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        username.setText(prefs.getString("companyname",null));
        //password.setText(prefs.getString("password", null));



    }
    private void setButtons()
    {
        CSNumber.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                String phone_no= "5045540101";
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone_no));
                startActivity(callIntent);

            }
        });



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

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("companyname", sUsername);
                   // editor.putString("password", sPassword);
                    editor.commit();

                    Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
                    startActivityForResult(myIntent, 0);
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
        if (id == R.id.wipe_data) {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("companyname", "");
            editor.putString("password", "");
            editor.commit();
            
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            username.setText(prefs.getString("companyname", null));
            //password.setText(prefs.getString("password", null));
            Toast.makeText(getApplicationContext(), "Data Erased",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
