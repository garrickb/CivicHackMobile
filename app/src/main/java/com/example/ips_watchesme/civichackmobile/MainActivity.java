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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;


public class MainActivity extends Activity {

    private EditText dln;
    private EditText password;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Add a listener to the button. */
        dln = (EditText) findViewById(R.id.dlnText);
        password = (EditText) findViewById(R.id.passwordText);
        submitButton = (Button) findViewById(R.id.loginButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    private void login(View v) {
        String dlnText = dln.getText().toString();
        String passwordText = password.getText().toString();
        RequestParams params = new RequestParams();

        params.put("dln", dlnText);
        params.put("password", password);
        invokeWS(params);
    }
    public void invokeWS(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://45.55.145.106/login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                System.out.println("Successfully logged in.");
                Toast.makeText(getApplicationContext(), "SUCCESS LOGGING IN.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("There was an error logging in.");
                Toast.makeText(getApplicationContext(), "ERROR LOGGING IN.", Toast.LENGTH_LONG).show();
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
