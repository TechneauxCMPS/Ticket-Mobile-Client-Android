package com.techneaux.techneauxmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private Button verifyButton;    // Create button
    private static EditText firstname; //object to link to the username layout object
    private static EditText lastname; //object to link to the password layout object
    private static EditText phonenumber; //object to link to the username layout object
    private static EditText emailaddress; //object to link to the password layout object
    private static TextView loggedInName; //object to link to the companyName layout object
    private static TextView empError;
    public static final String MY_PREFS_NAME = "MyPrefsFile"; //file to store prefs for the app.
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_info);

        verifyButton = (Button) findViewById(R.id.verifyButton);      // verify button
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        phonenumber = (EditText) findViewById(R.id.Phone);
        emailaddress = (EditText) findViewById(R.id.emailaddress);
        emailaddress = (EditText) findViewById(R.id.emailaddress);
        loggedInName = (TextView) findViewById(R.id.companynameLoggedIn);
        empError = (TextView) findViewById(R.id.employeeError);
        spinner = (ProgressBar) findViewById(R.id.employeeProgressBar);
        spinner.setVisibility(View.INVISIBLE);
        setButtons();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        firstname.setText(prefs.getString("emp_firstname", null)); // init first name to what was stored
        lastname.setText(prefs.getString("emp_lastname", null)); // init last name to what was stored
        phonenumber.setText(prefs.getString("emp_phonenumber", null)); // init phone number to what was stored
        emailaddress.setText(prefs.getString("emp_emailaddress", null)); // init email address to what was stored

        String mystring = new String("Company Name: " + prefs.getString("companyname", null));
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 14, mystring.length(), 0);
        loggedInName.setText(content); // init companyname to what was stored
        final View v = findViewById(android.R.id.content);

        if ((prefs.getInt("screen_state", 0) == 3)) {
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
    private void setButtons() {
        // throws a toast saying information has been verfied when Verify button is clicked.
        verifyButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                verifyButton.setEnabled(false);
                verifyButton.setText("Verifying...");
                String sFirstName = firstname.getText().toString(); //get text from username layout object
                String sLastName = lastname.getText().toString(); //get text from password layout object
                String sPhone = phonenumber.getText().toString(); //get text from username layout object
                String sEmail = emailaddress.getText().toString(); //get text from password layout object

                if (sFirstName.matches("")) //tests to see if first name field has text
                {
                    empError.setText("Employee First Name not entered!");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                }
                if (sLastName.matches("")) //tests to see if last name field has text
                {
                    empError.setText("Employee Last Name not entered!");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                }
                if (sPhone.matches("")) //tests to see if phone number field has text
                {
                    empError.setText("Phone Number not entered!");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                }

                if (isPhoneValid(sPhone) == false) {
                    empError.setText("Phone Number not valid!");

                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                }
                if (sEmail.matches("")) //tests to see if email address field has text
                {
                    empError.setText("Email Address not entered!");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                }
                if (isEmailValid(sEmail) == false) {
                    empError.setText("Email Address not valid!");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify");
                    return;
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    JSONObject EmployeeInfo = new JSONObject();
                    try {
                        EmployeeInfo.put("authKey", prefs.getString("authKey", null));
                        EmployeeInfo.put("firstName", sFirstName);
                        EmployeeInfo.put("lastName", sLastName);
                        EmployeeInfo.put("email", sEmail);
                        EmployeeInfo.put("phoneNumber", sPhone);


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    spinner.setVisibility(v.VISIBLE);
                    Thread thread = new Thread(new API_Communications("https://cmps.techneaux.com/update-employee", EmployeeInfo));
                    thread.start();
                    runThread(sFirstName,sLastName,sEmail,sPhone, v );
                    return;
                }

            }
        });
    }
    private void runThread(final String sFirstName, final String sLastName, final String sEmail, final String sPhone, final View v ) {

        new Thread() {
            public void run() {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                while (API_Communications.result == null) {
                }

                final String result = API_Communications.result;

                JSONObject obj = null;
                try {

                    obj = new JSONObject(result);


                } catch (Throwable t) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            verifyButton.setEnabled(true);
                            verifyButton.setText("Verify");
                            empError.setText("Invalid Response from Server, please try again.\n " + result);
                        }
                    });


                }
                String error = null;
                if (obj.has("niceMessage")) {
                    try {
                        error = obj.get("niceMessage").toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (error == null) {
                    editor.putString("emp_firstname", sFirstName);
                    editor.putString("emp_lastname", sLastName);
                    editor.putString("emp_phonenumber", sPhone);
                    editor.putString("emp_emailaddress", sEmail);
                    editor.putInt("screen_state", 3);
                    editor.commit();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            verifyButton.setEnabled(true);
                            verifyButton.setText("Verify");
                            spinner.setVisibility(View.INVISIBLE);
                            Intent myIntent = new Intent(v.getContext(), TicketActivity.class);
                            startActivityForResult(myIntent, 0);
                        }
                    });


                } else {
                    final String finalError = error;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            verifyButton.setEnabled(true);
                            verifyButton.setText("Verify");
                            spinner.setVisibility(View.INVISIBLE);
                            empError.setText(finalError);
                        }
                    });

                }






                    }

             }.start();
    }
    /**
     * method is used for checking valid phone number format.
     * Borrowed from http://buzycoder.blogspot.com/2013/06/android-check-if-phone-number-is-valid.html
     *
     * @param phone number
     * @return boolean true for valid false for invalid
     */
    static boolean isPhoneValid(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }

    /**
     * method is used for checking valid email id format.
     * Borrowed from http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    private static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
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

    /**
     * Preconditions: None
     * Post Conditions: all data fields are wiped out
     * Parameters: None
     * Use: whenever wipe data button is used in app.
     */
    public static void wipeData(SharedPreferences.Editor editor, SharedPreferences prefs) {
        if (!(prefs.getInt("screen_state", 0) == 2)) {

            firstname.setText(null); //sets field to empty
            lastname.setText(null); //sets field to empty
            phonenumber.setText(null); //sets field to empty
            emailaddress.setText(null); //sets field to empty
        }
        editor.putString("ticket_photo", null);
        editor.putString("ticket_location", null);
        editor.putInt("screen_state", 0);
        editor.putString("emp_firstname", null);
        editor.putString("emp_lastname", null);
        editor.putString("emp_phonenumber", null);
        editor.putString("emp_emailaddress", null);
        editor.putInt("screen_state", 0);
        editor.putString("companyname", null); //Wipe out company name
        editor.putInt("screen_state", 0); //Wipe out company name
        editor.putString("authKey", null);
        editor.commit();


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
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            MainActivity.wipeData(editor);
            wipeData(editor, prefs);
            final View v = findViewById(android.R.id.content);
            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            Toast.makeText(getApplicationContext(), "Data Erased",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
