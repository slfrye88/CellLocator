package com.example.www.celllocator;

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

import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    EditText id, lac;
    Button update, locate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText) findViewById(R.id.edit_cellid);
        lac = (EditText) findViewById(R.id.edit_lac);
        update = (Button) findViewById(R.id.cmd_update);
        locate = (Button) findViewById(R.id.cmd_locateme);

        //Set CELL_ID and LAC fields to their respective values
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {

                //TODO: Will need to call function that gets these values
                //Used on so app will run
                String cellid = "test cell id";
                String celllac = "test cell lac";
                id.setText(cellid);
                lac.setText(celllac);
            }
        });

        //Use CELL_ID and LAC to obtain longitude and latitude values. Will use these values to
        //locate physical location of phone
        locate.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {

                Double longitude, latitude;

                //TODO: Will need to push these values to function in order to get long/lat
                EditText cid = (EditText) findViewById(R.id.edit_cellid);
                String cellid = cid.getText().toString();
                EditText clac = (EditText) findViewById(R.id.edit_lac);
                String celllac = clac.getText().toString();

                //Used only so app runs
                longitude = 30.409456;
                latitude = 59.915494;

                //TODO: Will need to open map and locate phone using long/lat.
                //This shows location, but doesn't have a marker/pin
                String map = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);

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
