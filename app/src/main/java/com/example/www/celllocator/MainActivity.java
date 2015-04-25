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
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MYTAG";
    EditText id, lac,mnc,mcc;
    Button update, locate,wifi;
    String cellmcc,cellmnc;  //Mobile Country Code
    String cellid,celllac; //Cell ID

    Boolean error;
    URL strURLSent;
    String GetOpenCellID_fullresult;

    String myLatitude, myLongitude;
    class LoadListener{
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            Log.e("result",html);
        }
    }
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
        wifi = (Button) findViewById(R.id.cmd_wifi);
        //Set CELL_ID and LAC fields to their respective values
        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
    public void sendMessage(View view) throws IOException {

        Connect con = new Connect();
        con.doInBackground();
        double l1 = Double.parseDouble(myLatitude);
        double l2 = Double.parseDouble(myLongitude);
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
                strURLSent = new URL("http://www.open-electronics.org/celltrack/cell.php?hex=0&mcc="+cellmcc+"&mnc="+cellmnc+"&lac="+celllac+"&cid="+cellid+"&lac0=&cid0=&lac1=&cid1=&lac2=&cid2=&lac3=&cid3=&lac4=&cid4=");
                HttpURLConnection urlConnection = (HttpURLConnection) strURLSent.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = in.read()) != -1) {
                    sb.append((char) ch);
                }
                String response = sb.toString();

                Log.v(TAG, "inside cellid");
                GetOpenCellID_fullresult = response;
                myLatitude= GetOpenCellID_fullresult.substring(GetOpenCellID_fullresult.lastIndexOf("Cell<br>Lat=")+12,GetOpenCellID_fullresult.lastIndexOf(" <br> Lon="));
                myLongitude = GetOpenCellID_fullresult.substring(GetOpenCellID_fullresult.lastIndexOf(" <br> Lon=")+10,GetOpenCellID_fullresult.lastIndexOf(" <br> Range="));

                urlConnection.disconnect();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed!";

        }

    }

    public void sendMessage1(View view) throws IOException {

        Connect1 con=new Connect1();
        con.doInBackground();
        URL link = new URL("http://freegeoip.net/csv/"+con.Ip);

        HttpURLConnection connection = (HttpURLConnection) link.openConnection();
        connection.connect();

        InputStream is = connection.getInputStream();

        int status = connection.getResponseCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String latlong=null;
        String line = reader.readLine();
        String[] fields=line.split(",");

        myLatitude=fields[8];
        myLongitude=fields[9];
        Log.v("latitude",myLatitude);
        Log.v("longitude",myLongitude);
        double l1 = Double.parseDouble(myLatitude);
        double l2 = Double.parseDouble(myLongitude);
        Log.v(TAG, "here"+myLatitude);
        //Log.v(TAG, myLatitude);
        LatLng x = new LatLng(l1,l2);
        Bundle args = new Bundle();
        args.putParcelable("x", x);
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("bundle", args);
        startActivity(intent);
          /*  int index = nthOccurrence(line, ',', 7);
            latlong=line.substring(index+1);

              Log.v("newloc",latlong.substring(0,latlong.length()-4));
        is.close();
        int latind=nthOccurrence(latlong,',',1);
        myLatitude=latlong.substring(0,latind);
        myLongitude=latlong.substring(latind+1);*/
    }

    private class Connect1 extends AsyncTask<String, Void, String> {
        public String Ip;
        @Override
        protected String doInBackground(String... params) {

            //INSERT YOUR FUNCTION CALL HERE
            try {
                strURLSent = new URL("http://www.iplocation.net/");
                HttpURLConnection urlConnection = (HttpURLConnection) strURLSent.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = in.read()) != -1) {
                    sb.append((char) ch);
                }
                String response = sb.toString();
                Ip=response.substring(response.lastIndexOf("color='green'>")+14,response.lastIndexOf("</font></b>.<br />"));
                Log.v(TAG, Ip);

                urlConnection.disconnect();


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
