package com.techneaux.techneauxmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
        setButtons(); //function to initialize the two buttons (submit and call)
        //********End Initialize the layout objects to a variable for manipulation********


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        username.setText(prefs.getString("companyname",null)); // init username to what was stored
        final View v = findViewById(android.R.id.content);

        if( !(prefs.getInt("screen_state",0) < 2  ))
        {
            Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
            startActivityForResult(myIntent, 0);
        }
        else {
            Intent myIntent = new Intent(v.getContext(), TicketActivity.class);
            startActivityForResult(myIntent, 0);
        }



    }

    /**
     * Preconditions: None
     * Post Conditions: Buttons have functional actions upon click
     * Parameters: None
     * Use: Only needs to be called once to set the onclick methods.
     */
    private void setButtons()
    {
        //********Customer Service Call Phone ********
        CSNumber.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                String phone_no = "5045540101"; //number to call
                Intent callIntent = new Intent(Intent.ACTION_CALL); // making new intent object
                // to switch screen
                callIntent.setData(Uri.parse("tel:" + phone_no)); //define screen to switch to with parameters
                startActivity(callIntent); //switch

            }
        });
        //********End Customer Service Call Phone ********


        //********Submit Login Info ********
        submitLoginInfo.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                String sUsername = username.getText().toString(); //get text from username layout object
                String sPassword = password.getText().toString(); //get text from password layout object

                if (sUsername.matches("")) //tests to see if Username field has text
                {
                    Toast.makeText(getApplicationContext(), "Username not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (sPassword.matches("")) //tests to see if Password field has text
                {
                    Toast.makeText(getApplicationContext(), "Password not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else //username & password is accepted, save company name and switch to next screen,
                {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("companyname", sUsername);
                    editor.putInt("screen_state", 2);

                    editor.commit();

                    Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
                    startActivityForResult(myIntent, 0);
                    return;
                }


            }
        });
        //********End Submit Login Info ********
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * Preconditions: None
     * Post Conditions: login fields are wiped out
     * Parameters: None
     * Use: whenever wipe data button is used in app.
     */
    public static void wipeData(SharedPreferences.Editor editor)
    {


        editor.putString("companyname", null); //Wipe out company name
        editor.putInt("screen_state", 0); //Wipe out company name
        editor.commit();

        username.setText(null); //sets field to empty
        password.setText(null); //sets field to empty
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wipe_data) { //wipes all data from login screen
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            wipeData(editor);
            Toast.makeText(getApplicationContext(), "Data Erased",
                    Toast.LENGTH_LONG).show(); // display confirmation message
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
