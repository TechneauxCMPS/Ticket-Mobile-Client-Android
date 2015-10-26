package com.techneaux.techneauxmobileapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private Button verifyButton;    // Create button
    public static final String MY_PREFS_NAME = "MyPrefsFile"; //file to store prefs for the app.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_info);

        verifyButton = (Button) findViewById(R.id.verifyButton);      // verify button

        // throws a toast saying information has been verfied when Verify button is clicked.
        verifyButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Information has been verified.",
                        Toast.LENGTH_LONG).show();
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
            MainActivity.wipeData(editor);
            Toast.makeText(getApplicationContext(), "Data Erased",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
