package com.example.ips_watchesme.civichackmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class LoggingScreen extends Activity {

    private Button startButton;
    private TextView textDistance;
    private TextView textTime;
    private long startTime = 0;
    private int elapsedMin = 0, elapsedSecond = 0;
    private boolean timerRunning = false;
    private float distance = 0;

    private Location loc;

    public void savePlayerLocation(final Context ctx){
        try {
            final LocationManager locationManager=(LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
                new Thread(new Runnable(){
                    public void run(){
                        try {
                            Looper.prepare();
                            final Timer t=new Timer();
                            final LocationListener locationListener=new LocationListener(){
                                public void onLocationChanged(Location location){
                                    locationManager.removeUpdates(this);
                                    t.cancel();

                                    /* Log the change in distance. */
                                    if(loc != null) {
                                        distance += loc.distanceTo(location);
                                    }
                                    loc = location;

                                    /* Update the UI. */
                                    textDistance.setText(distance + " miles");

                                    Looper.myLooper().quit();
                                }
                                public void onProviderDisabled(              String provider){
                                }
                                public void onProviderEnabled(              String provider){
                                }
                                public void onStatusChanged(              String provider,              int status,              Bundle extras){
                                }
                            }
                                    ;
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener,Looper.myLooper());
                            Looper.loop();
                        }
                        catch (          Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                ).start();

        }
        catch (  Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logging_screen, menu);

        startButton = (Button) findViewById(R.id.startButton);
        textTime = (TextView) findViewById(R.id.textTime);
        textDistance = (TextView) findViewById(R.id.textDistance);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPress(v);
            }
        });

        Timer time = new Timer();

        time.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (timerRunning) {
                    elapsedSecond += 1; //increase every sec
                    if (elapsedSecond >= 59) {
                        elapsedSecond -= 59;
                        elapsedMin++;
                    }
                    mHandler.obtainMessage(1).sendToTarget();
                    savePlayerLocation(getApplicationContext());
                }

            }
        }, 0, 1000);

        return true;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            textTime.setText(((Integer.toString(elapsedMin).length() == 1)?"0":"") + elapsedMin + ":" + ((Integer.toString(elapsedSecond).length() == 1)?"0":"") + elapsedSecond); //this is the textview
        }
    };

    private void buttonPress(View v) {
        /* If the timer isn't running, let's keep track of the starting time. */
        if(!timerRunning) {
            /* reset the previous time. */
            elapsedSecond = 0;
            elapsedMin = 0;
            textTime.setText("00:00");

            startTime =  System.currentTimeMillis();
            startButton.setText("STOP");
            startButton.setBackgroundColor(Color.RED);

        } else {
            /* We're submitting the time to the database. */
            long elapsedTime = System.currentTimeMillis() - startTime;
            Toast.makeText(getApplicationContext(), "Time: " + elapsedTime, Toast.LENGTH_LONG).show();

            RequestParams params = new RequestParams();
            params.put("startDate", startTime);
            params.put("endDate", System.currentTimeMillis());
            params.put("distanceValue", distance);
            params.put("distanceType", "m");

            AsyncHttpClient client = new AsyncHttpClient();
            SharedPreferences settings = getSharedPreferences("Login", Context.MODE_PRIVATE);
            client.addHeader("Authorization", settings.getString("key", ""));
            client.post("http://45.55.145.106:5000/api/trips", params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, String response) {
                    System.out.println("Successfully logged in.");
                }

                @Override
                public void onFailure(int i, Header[] headers, String response, Throwable t) {
                    System.out.println("There was an error logging your trip.");
                    //Toast.makeText(getApplicationContext(), "FAILURE: " + response, Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        Toast.makeText(getApplicationContext(), "FAILURE: " + jsonObj.get("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "FAILURE.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    Toast.makeText(getApplicationContext(), "Retry: " + retryNo, Toast.LENGTH_LONG).show();
                }

            });

            startButton.setText("START RECORDING");
            startButton.setBackgroundColor(Color.LTGRAY);
        }

        timerRunning = !timerRunning;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //TODO: Popupwindow
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();

            /*  Remove key in storage.  */
            SharedPreferences settings = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("key");
            editor.commit();

            /* Redirect to the main activity. */
            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(mainIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
