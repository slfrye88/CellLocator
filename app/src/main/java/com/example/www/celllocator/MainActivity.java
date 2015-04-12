package com.example.www.celllocator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
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

public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MYTAG";
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
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
                        celllac = String.valueOf(location.getLac());
                        cellid = String.valueOf(location.getCid());
                    }
                }
                id.setText(cellid);
                lac.setText(celllac);
                mcc.setText(cellmcc);
                mnc.setText(cellmnc);
            }
        });


    }
    //just to push
    public void sendMessage(View view) {

            Connect con =new Connect();
            con.doInBackground();
        double l1=Double.parseDouble(myLatitude);
        double l2=Double.parseDouble(myLongitude);
        Log.v(TAG, "here"+myLatitude);
        //Log.v(TAG, myLatitude);
        LatLng x = new LatLng(l1,l2);
        Bundle args = new Bundle();
        args.putParcelable("x", x);
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("bundle", args);
        startActivity(intent);
    }

    private class Connect extends AsyncTask<String, Void, String> {
        public String getGetOpenCellID_fullresult() {
            return GetOpenCellID_fullresult;
        }
        @Override
        protected String doInBackground(String... params) {

            //INSERT YOUR FUNCTION CALL HERE
            try {
                /*                strURLSent =
                        "http://www.opencellid.org/cell/get?mcc=" + cellmcc
                                + "&mnc=" + cellmnc
                                + "&cellid=" + cellid
                                + "&lac=" + celllac
                                + "&key=190eb219-14b5-4651-a6f0-d106e78f3298"
                                + "&fmt=txt";*/
                strURLSent="http://www.open-electronics.org/celltrack/cell.php?hex=0&mcc="+cellmcc+"&mnc="+cellmnc+"&lac="+celllac+"&cid="+cellid+"&lac0=&cid0=&lac1=&cid1=&lac2=&cid2=&lac3=&cid3=&lac4=&cid4=";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(strURLSent);
                HttpResponse response = client.execute(request);
                Log.v(TAG, "inside cellid");
                GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity());
                myLatitude= GetOpenCellID_fullresult.substring(GetOpenCellID_fullresult.lastIndexOf("Cell<br>Lat=")+12,GetOpenCellID_fullresult.lastIndexOf(" <br> Lon="));
                myLongitude = GetOpenCellID_fullresult.substring(GetOpenCellID_fullresult.lastIndexOf(" <br> Lon=")+10,GetOpenCellID_fullresult.lastIndexOf(" <br> Range="));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed!";

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
