package com.example.www.celllocator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    EditText id, lac,mnc,mcc;
    Button update, locate;
    String cellmcc,cellmnc;  //Mobile Country Code
    String cellid,celllac; //Cell ID

    Boolean error;
    String strURLSent;
    String GetOpenCellID_fullresult;

    String myLatitude, myLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText) findViewById(R.id.edit_cellid);
        lac = (EditText) findViewById(R.id.edit_lac);
        mcc = (EditText) findViewById(R.id.edit_mcc);
        mnc = (EditText) findViewById(R.id.edit_mnc);
        update = (Button) findViewById(R.id.cmd_update);
        locate = (Button) findViewById(R.id.cmd_locateme);

        //Set CELL_ID and LAC fields to their respective values
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //TODO: Will need to call function that gets these values
                //Used on so app will run

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                String networkOperator = telephonyManager.getNetworkOperator();
                cellmcc = networkOperator.substring(0, 3);
                cellmnc = networkOperator.substring(3);
                final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                    if (location != null) {
                        cellid = Integer.toString(location.getLac());
                        celllac = Integer.toString(location.getCid());
                    }
                }
                id.setText(cellid);
                lac.setText(celllac);
                mcc.setText(cellmcc);
                mnc.setText(cellmnc);
            }
        });

        //Use CELL_ID and LAC to obtain longitude and latitude values. Will use these values to
        //locate physical location of phone
        locate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Double longitude, latitude;
                try {
                    GetOpenCellID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //TODO: Will need to push these values to function in order to get long/lat

                //Used only so app runs

                //TODO: Will need to open map and locate phone using long/lat.
                //This shows location, but doesn't have a marker/pin
                final LatLng x;
                x = new LatLng((double)Integer.parseInt(myLatitude),Integer.parseInt(myLongitude));
                String map = String.format(Locale.ENGLISH, "geo:%f,%f", myLatitude, myLongitude);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(intent);

            }

        });
    }
    public String getGetOpenCellID_fullresult(){
        return GetOpenCellID_fullresult;
    }
    public void GetOpenCellID() throws Exception {
        groupURLSent();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(strURLSent);
        HttpResponse response = client.execute(request);
        GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
        spliteResult();
    }
    public void groupURLSent(){
        strURLSent =
                "http://www.opencellid.org/cell/get?mcc=" + mcc
                        +"&mnc=" + mnc
                        +"&cellid=" + cellid
                        +"&lac=" + lac
                        +"&fmt=txt";
    }
    private void spliteResult(){
        if(GetOpenCellID_fullresult.equalsIgnoreCase("err")){
            error = true;
        }else{
            error = false;
            String[] tResult = GetOpenCellID_fullresult.split(",");
            myLatitude = tResult[0];
            myLongitude = tResult[1];
        }

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
