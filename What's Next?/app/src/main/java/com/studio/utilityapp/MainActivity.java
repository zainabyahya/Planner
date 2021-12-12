package com.studio.utilityapp;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.studio.utilityapp.SignupFrontend.LoginActivity;
import com.studio.utilityapp.SignupFrontend.SessionHandler;
import com.studio.utilityapp.SignupFrontend.User;
import com.studio.utilityapp.Utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SessionHandler session;


    GPSTracker gps;
    Geocoder geocoder;
    List<Address> addresses;
    TextView quote;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);
        quote = findViewById(R.id.quote);
        TextView welcomeText2 = findViewById(R.id.welcomeText2);
        welcomeText.setText("Welcome "+user.getFullName()+", your session will expire on "+user.getSessionExpiryDate());



        //////////////// LOCATION API /////////////////////////////

        gps= new GPSTracker(MainActivity.this);
        geocoder = new Geocoder(this, Locale.getDefault());
        if(gps.canGetLocation()){
            double latitude=gps.getLatitude();
            double longitude=gps.getLongitude();
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String city = addresses.get(0).getLocality();
                    welcomeText2.setText("Logged In From "+city);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else{
            gps.showSettingsAlert();
        }



        findViewById(R.id.button).setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ToDoListActivity.class));
        });
        findViewById(R.id.button2).setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), PlanMyDayActivity.class));
        });
        findViewById(R.id.button3).setOnClickListener(view -> {
            session.logoutUser();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });


        QuoteOfTheDay quote = new QuoteOfTheDay();
        quote.execute();
    }




    //////////////////// QUOTE API ///////////////////////

    private class QuoteOfTheDay extends AsyncTask<String, Integer, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ArrayList<String> items = new ArrayList<String>();

            String queryURL = "https://quotes.rest/qod?language=en";

            try {

                URL url = new URL(queryURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null)

                {sb.append(line + "\n");}

                String result = sb.toString();
                JSONObject jsonObject1 = new JSONObject(result);
                JSONObject jsonObject = jsonObject1.getJSONObject("contents");
                JSONArray jsonArray = jsonObject.getJSONArray("quotes");
                String quote = jsonArray.getJSONObject(0).getString("quote");
                items.add(quote);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            return items;
        }

        @Override
        protected void onProgressUpdate(Integer... value) {
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            if(!result.isEmpty()){
                quote.setText(""+result.get(0));
            }else{
                quote.setText("Can't fetch quote from API");
            }


        }
    }
}
