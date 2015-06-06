package com.example.ips_watchesme.civichackmobile;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {

    private EditText dln;
    private EditText password;
    private Button submitButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Add a listener to the button. */
        dln = (EditText) findViewById(R.id.dlnText);
        password = (EditText) findViewById(R.id.passwordText);
        submitButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please contact your employer to be registered.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(View v) {
        String dlnText = dln.getText().toString();
        String passwordText = password.getText().toString();
        RequestParams params = new RequestParams();

        params.put("username", dlnText);
        params.put("password", passwordText);
        invokeWS(params);
    }

    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://45.55.145.106:5000/auth/local", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, String response) {
                System.out.println("Successfully logged in.");
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObj.get("token"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "SUCCESS.", Toast.LENGTH_LONG).show();
                }
           }

            @Override
            public void onFailure(int i, Header[] headers, String response, Throwable t) {
                System.out.println("There was an error logging in.");
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
