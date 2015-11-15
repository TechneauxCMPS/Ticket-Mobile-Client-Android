package com.techneaux.techneauxmobileapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

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
    private static TextView ErrorTicket;
    private static Button clearPhoto;

    private static Button SubmitTicketBTN;
    private static Button CameraBTN;
    boolean isImageFitToScreen;

    private static final int CAMERA_REQUEST = 1888;
    private static ImageView imageView;

    private static Bitmap photo;
    private String photo_ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_activity);
        employeename = (TextView) findViewById(R.id.ticket_employeename);
        companyname = (TextView) findViewById(R.id.ticket_companynameLoggedIn);
        phonenumber = (TextView) findViewById(R.id.ticket_phonenumber);
        emailaddress = (TextView) findViewById(R.id.ticket_emailaddress);
        SubmitTicketBTN = (Button) findViewById(R.id.SubmitTicketBTN);
        CameraBTN = (Button) this.findViewById(R.id.cameraBTN);
        Location = (EditText) findViewById(R.id.locationsite);
        Description = (EditText) findViewById(R.id.ticket_description);
        imageView = (ImageView)this.findViewById(R.id.imageView);
        ErrorTicket = (TextView) this.findViewById(R.id.ticketError);
        clearPhoto = (Button) this.findViewById(R.id.clearPhoto);
        setButtons();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //Company name read
        String mystring = new String("Company Name: " + prefs.getString("companyname", null));
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 14, mystring.length(), 0);
        companyname.setText(content);
        //end company name read

        //employee name read
        mystring = new String("Employee Name: " + prefs.getString("emp_firstname", null) + " " + prefs.getString("emp_lastname", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 15, mystring.length(), 0);
        employeename.setText(content);
        //end name read

        //phone number read
        mystring = new String("Phone Number: " + prefs.getString("emp_phonenumber", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 14, mystring.length(), 0);
        phonenumber.setText(content);
        //end phone number read

        //email read
        mystring = new String("Email Address: " + prefs.getString("emp_emailaddress", null));
        content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 15, mystring.length(), 0);
        emailaddress.setText(content);
        //email end read

        //location read
        Location.setText(prefs.getString("ticket_location", null));
        //end location read

        String base = prefs.getString("ticket_photo", null);
        photo_ticket = "";

        if (base != null) {
            photo_ticket=base;
            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            photo = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
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

        // throws a toast saying information has been verfied when Verify button is clicked.
        SubmitTicketBTN.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String sDescription = Description.getText().toString(); //get text from username layout object
                String sLocation = Location.getText().toString(); //get text from password layout object

                if (sLocation.matches("")) //tests to see if Username field has text
                {
                    ErrorTicket.setText("Location Site not Entered!");

                    return;
                } else if (sDescription.matches("")) //tests to see if Password field has text
                {
                    ErrorTicket.setText("Description not Entered!");

                    return;
                } else //username & password is accepted, save company name and switch to next screen,
                {

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("ticket_location", sLocation);
                    editor.commit();


                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    JSONObject TicketInfo = new JSONObject();
                    try {
                        TicketInfo.put("authKey", prefs.getString("authKey",null));
                        TicketInfo.put("location", sLocation);
                        TicketInfo.put("description", sDescription);
                        TicketInfo.put("photo", photo_ticket);



                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Log.d("API", "In Ticket: START " + photo_ticket +" END");
                    Log.d("API", "In Ticket Count: " + photo_ticket.length());

                    Thread thread = new Thread(new API_Communications("https://cmps.techneaux.com/submit-ticket", TicketInfo));
                    //Thread thread = new Thread(new API_Communications("http://httpbin.org/post", TicketInfo));
                    thread.start();
                    while (API_Communications.result == null) {
                    }

                    String result = API_Communications.result;

                    JSONObject obj = null;
                    try {

                        obj = new JSONObject(result);


                    } catch (Throwable t) {
                        ErrorTicket.setText("Invalid Response from Server, please try again.\n " + result);


                    }
                    String error = null;
                    if (obj.has("niceMessage")) {
                        try {
                            error = obj.get("niceMessage").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    String PhotoStatus = "";
                    if (error == null) {

                        if(prefs.getString("ticket_photo", null) == null)
                        {
                            PhotoStatus = "No Photo";
                        }
                        else
                        {
                            PhotoStatus = "Photo Attached";
                        }

                        Toast.makeText(getApplicationContext(), "Ticket Sent Successfully!\n\n"
                                        + companyname.getText().toString() + "\nLocation: "
                                        + Location.getText().toString() + "\n" +
                                        employeename.getText().toString() + "\n"
                                        + phonenumber.getText().toString() + "\n"
                                        + emailaddress.getText().toString() + "\nDescription: "
                                        + Description.getText().toString() + "\nPhoto: "
                                        + PhotoStatus,
                                Toast.LENGTH_LONG).show();
                        Description.setText("");
                        photo_ticket="";
                        editor.putString("ticket_photo", null);
                        editor.commit();
                        ErrorTicket.setText("");
                        imageView.setImageBitmap(null);
                    }
                    else{

                        ErrorTicket.setText(error);


                    }

                    return;
                }

            }


        });

        clearPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                photo_ticket="";
                editor.putString("ticket_photo", null);
                editor.commit();

                imageView.setImageBitmap(null);
            }
        });
        CameraBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadPhoto(imageView);
            }
        });







    }
    private void loadPhoto(ImageView imageView) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.photo_preview, null);
        ImageView image = (ImageView) layout.findViewById(R.id.imageView2);
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1500 ));
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("API", "Init Photo Size:" + encoded.length());
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("ticket_photo", encoded);
            editor.commit();

            photo_ticket=encoded;

           //ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
           //ClipData clip = ClipData.newPlainText("lol", encoded);
           //clipboard.setPrimaryClip(clip);

        }
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
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            try {
                MainActivity.wipeData(editor);
                RegistrationActivity.wipeData(editor, prefs);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            wipeData(editor,prefs);
            final View v = findViewById(android.R.id.content);
            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        }
        if(id == R.id.EmployeeCredentials)
        {
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();;
            editor.putInt("screen_state", 2);
            editor.commit();
            final View v = findViewById(android.R.id.content);
            Intent myIntent = new Intent(v.getContext(), RegistrationActivity.class);
            startActivityForResult(myIntent, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void wipeData(SharedPreferences.Editor editor,SharedPreferences prefs)
    {
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

        if(!(prefs.getInt("screen_state",0) == 3  )) {

            Location.setText(null);
            Description.setText(null);
        }
    }

}
