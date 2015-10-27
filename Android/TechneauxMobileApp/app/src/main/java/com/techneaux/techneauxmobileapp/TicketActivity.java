package com.techneaux.techneauxmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by CMD Drake on 10/27/2015.
 */
public class TicketActivity  extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "MyPrefsFile"; //file to store prefs for the app.

    private static TextView employeename; //object to link to the employeename layout object
    private static TextView companyname; //object to link to the company name layout object
    private static TextView phonenumber; //object to link to the phone number layout object
    private static TextView emailaddress; //object to link to the email address layout object

    private static EditText Location; //object to link to the location layout object
    private static EditText Description; //object to link to the description layout object


    private static Button SubmitTicketBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_activity);
        employeename = (TextView) findViewById(R.id.ticket_employeename);
        companyname = (TextView) findViewById(R.id.ticket_companynameLoggedIn);
        phonenumber = (TextView) findViewById(R.id.ticket_phonenumber);
        emailaddress = (TextView) findViewById(R.id.ticket_emailaddress);
        SubmitTicketBTN = (Button)  findViewById(R.id.SubmitTicketBTN);

        Location = (EditText) findViewById(R.id.locationsite);
        Description = (EditText) findViewById(R.id.ticket_description);

        setButtons();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //Company name read
        String mystring=new String("Company Name: " + prefs.getString("companyname" , null));
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 14 , mystring.length(), 0);
        companyname.setText(content);
        //end company name read

        //employee name read
        mystring=new String("Employee Name: " +prefs.getString( "emp_firstname", null) + " " +prefs.getString("emp_lastname", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 15 , mystring.length(), 0);
        employeename.setText(content);
        //end name read

        //phone number read
        mystring=new String("Phone Number: " + prefs.getString("emp_phonenumber", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 14, mystring.length(), 0);
        phonenumber.setText(content);
        //end phone number read

        mystring=new String("Email Address: " +prefs.getString( "emp_emailaddress", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 15, mystring.length(), 0);
        emailaddress.setText(content);



    }
    /**
     * Preconditions: None
     * Post Conditions: Buttons have functional actions upon click
     * Parameters: None
     * Use: Only needs to be called once to set the onclick methods.
     */
    private void setButtons()
    {
        // throws a toast saying information has been verfied when Verify button is clicked.
        SubmitTicketBTN.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String sDescription = Description.getText().toString(); //get text from username layout object
                String sLocation = Location.getText().toString(); //get text from password layout object

                if (sLocation.matches("")) //tests to see if Username field has text
                {
                    Toast.makeText(getApplicationContext(), "Location Site not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (sDescription.matches("")) //tests to see if Password field has text
                {
                    Toast.makeText(getApplicationContext(), "Description not entered!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else //username & password is accepted, save company name and switch to next screen,
                {
                    Toast.makeText(getApplicationContext(), "Ticket Sent Successfully!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }


        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.wipe_data) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
