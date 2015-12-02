package com.techneaux.techneauxmobileapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private static Button submitLoginInfo; //object to link to the submit login info layout object
    private static EditText username; //object to link to the username layout object
    private static EditText password; //object to link to the password layout object
    private static TextView CSNumber; //object to link to the customer service number  layout object
    public static final String MY_PREFS_NAME = "MyPrefsFile"; //file to store prefs for the app.
    public static TextView errorText;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //********Initialize the layout objects to a variable for manipulation********
        submitLoginInfo = (Button) findViewById(R.id.loginSubmit);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        CSNumber = (TextView) findViewById(R.id.CSNumberText);
        errorText=(TextView) findViewById(R.id.loginErrorText);
        spinner = (ProgressBar)findViewById(R.id.loginProgressBar);
        spinner.setVisibility(View.INVISIBLE);

        setButtons(); //function to initialize the two buttons (submit and call)
        //********End Initialize the layout objects to a variable for manipulation********


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        username.setText(prefs.getString("companyname",null)); // init username to what was stored

        final View v = findViewById(android.R.id.content);

        if( (prefs.getInt("screen_state",0) == 2  )) //checks saved state from last run time,
        {                                            // if state = 2, go to Registration

            Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
            startActivityForResult(myIntent, 0);
        }
        else if( (prefs.getInt("screen_state",0) == 3  )) { //if 3, go to Ticket Activity

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
                submitLoginInfo.setText("Logging In...");
                submitLoginInfo.setEnabled(false);
                String sUsername = username.getText().toString(); //get text from username layout object
                String sPassword = password.getText().toString(); //get text from password layout object

                if (sUsername.matches("")) //tests to see if Username field has text
                {
                    errorText.setText("Username not entered!");
                    submitLoginInfo.setText("Submit");
                    submitLoginInfo.setEnabled(true);
                    return;
                } else if (sPassword.matches("")) //tests to see if Password field has text
                {
                    errorText.setText("Password not entered!");
                    submitLoginInfo.setText("Submit");
                    submitLoginInfo.setEnabled(true);
                    return;
                } else //username & password is accepted, save company name and switch to next screen,
                {

                    JSONObject login = new JSONObject(); //create JSON Obj to hold data for API
                    try {
                        login.put("companyID", sUsername); //push companyID
                        login.put("password", sPassword); //push password


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    spinner.setVisibility(v.VISIBLE); //have load spinne be visible

                    //Code to create a thread to send data to API
                    Thread thread = new Thread(new API_Communications("https://cmps.techneaux.com/login", login));
                    thread.start();


                    runThread(v, sUsername);//process API data returned
                    return;
                }


            }
        });
        //********End Submit Login Info ********
    }
    private void runThread(final View v, final String sUsername) {

        new Thread() {
            public void run() {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();


                while (API_Communications.result == null) { // wait for reply from API
                }

                final String result = API_Communications.result; //get response from API
                JSONObject obj = null;
                try { //attempt to make json obj from API Response

                    obj = new JSONObject(result);


                } catch (Throwable t) { // post error message if api does not give a proper JSON obj
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() { //need to invoke runonUI to set text boxes
                            errorText.setText("Invalid Response from Server, please try again." + result);
                            submitLoginInfo.setText("Submit");
                            submitLoginInfo.setEnabled(true);
                        }
                    });



                }
                String error = null; //string to hold error from api json reply
                if (obj.has("niceMessage")) { //parse a nice debug message error
                    try {                     //from API to print to screen
                        error = obj.get("niceMessage").toString(); //parse out nicemessage

                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() { //need to invoke runonUI to set text boxes
                                spinner.setVisibility(v.INVISIBLE);
                                submitLoginInfo.setText("Submit");
                                submitLoginInfo.setEnabled(true);

                            }
                        });
                        e.printStackTrace();
                    }
                }
                if (obj.has("authKey")) { //check if the object has a field for an authkey
                    if (error == null) { //make sure there is no error from api
                        try {
                            String authKey = obj.get("authKey").toString(); //get authkey from jsonobj
                            editor.putString("authKey", authKey);

                            editor.commit();
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() { //need to invoke runonUI to set text boxes
                                    spinner.setVisibility(v.INVISIBLE);
                                    submitLoginInfo.setText("Submit");
                                    submitLoginInfo.setEnabled(true);

                                }
                            });
                            e.printStackTrace();
                        }
                    }

                    editor.putString("companyname", sUsername); //save username to shared prefs
                    editor.putInt("screen_state", 2); //update app state to 2, so app can jump to the next screen.

                    editor.commit();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() { //need to invoke runonUI to set text boxes
                            spinner.setVisibility(v.INVISIBLE);
                            submitLoginInfo.setText("Submit");
                            submitLoginInfo.setEnabled(true);

                            //go to next screen
                            Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
                            startActivityForResult(myIntent, 0);
                        }
                    });

                } else {
                    final String finalError = error; //got an error to print to screen
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() { //need to invoke runonUI to set text boxes
                            spinner.setVisibility(v.INVISIBLE);
                            submitLoginInfo.setText("Submit");
                            submitLoginInfo.setEnabled(true);
                            errorText.setText(finalError);
                        }
                    });

                }

            }
        }.start();//thread starter
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


        editor.putString("ticket_photo", null); //wipe out ticket photo data
        editor.putString("ticket_location", null); //wipe out ticket location data
        editor.putInt("screen_state", 0); // wipe app state to defaults
        editor.putString("emp_firstname", null); //wipe firstname
        editor.putString("emp_lastname", null); //wipe last name
        editor.putString("emp_phonenumber", null); // wipe phone number
        editor.putString("emp_emailaddress", null); //wipe email address
        editor.putString("companyname", null); //Wipe out company name
        editor.putInt("screen_state", 0); //Wipe out company name
        editor.putString("authKey",null); //wipe authkey
        editor.commit(); //save!


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
            AlertDialog eraseConfirm = eraseDialogAlertMaker(); //dialog before wiping
                                                            // data to confirm wipe

            eraseConfirm.show(); //show dialog



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlertDialog eraseDialogAlertMaker(){

        AlertDialog eraseDialog =

                new AlertDialog.Builder(this)
                        //set message, title, and icon
                        .setTitle("Erase Data?")
                        .setMessage("Are you sure that you want to erase all stored data?")

                                //set three option buttons
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //whatever should be done when answering "YES" goes here
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                wipeData(editor);
                                Toast.makeText(getApplicationContext(), "Data Erased",
                                        Toast.LENGTH_LONG).show();

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //whatever should be done when answering "NO" goes here


                            }
                        })//setNegativeButton

                        .create();

        return eraseDialog;
    }
}
