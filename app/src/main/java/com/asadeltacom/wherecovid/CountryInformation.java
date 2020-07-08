package com.asadeltacom.wherecovid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class CountryInformation extends AppCompatActivity {

    private String p, c, d, r, cp, mp, fl;
    //String resposta = "{\"cases\":887057,\"deaths\":44265,\"recovered\":185291}";
    //private String country, URL = "https://coronavirus-19-api.herokuapp.com/countries/";
    //private String country, FLAG, URL = "https://corona.lmao.ninja/countries/";
    private String country, FLAG, URL = "https://corona.lmao.ninja/v2/countries/";

    private TextView pais;
    private TextView cases;
    private TextView deaths;
    private TextView recovered;
    private TextView cpm;
    private TextView mpm;
    private ImageView image_flag;
    private ProgressDialog progressDialog;
    private Double lat, lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_country);

        Bundle extras = getIntent().getExtras();
        country = extras.getString ("country");
        Log.d("Posição do país = ", country);
        new MyAsyncTasks().execute(URL + country);

    }

    public class MyAsyncTasks extends AsyncTask<String, String, String> {

       /* @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experience
            progressDialog = new ProgressDialog(getApplication());
            progressDialog.setMessage("Favor aguardar...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }*/

        @Override
        protected String doInBackground(String... params) {

            // implement API in background and store the response in current variable
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(URL + country);

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);

                    }
                    // return the data to onPostExecute method
                    return current;

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String data) {

            Log.d("data", data);
            // dismiss the progress dialog after receiving data from API
            //progressDialog.dismiss();
            try {
                // JSON Parsing of data
                JSONObject oneObject;
                oneObject = new JSONObject(data);

                //JSONArray jsonArray = new JSONArray(s);
                //JSONObject oneObject = jsonArray.getJSONObject(0);
                // Pulling items from the array
                p = oneObject.getString("country");
                c = oneObject.getString("cases");
                d = oneObject.getString("deaths");
                r = oneObject.getString("recovered");
                cp = oneObject.getString("casesPerOneMillion");
                mp = oneObject.getString("deathsPerOneMillion");
                fl = oneObject.getString("countryInfo");

                try{
                    // JSON Parsing of data
                    JSONObject secondObject = new JSONObject(fl);
                    lat = secondObject.getDouble("lat");
                    lng = secondObject.getDouble("long");
                    FLAG = secondObject.getString("flag");
                    Log.d("FLAG   >>>>>>>>>>", FLAG);
                } catch (Exception e){
                    FLAG = "https://ya-webdesign.com/transparent250_/meme-png-pack.png";
                }

                String TAG = "Country Information";
                Log.d(TAG, "Flag país: " + FLAG);

                // display the data in UI
                pais = findViewById(R.id.countryName);
                pais.setText(p);
                cases = findViewById(R.id.cases);
                cases.setText(c);
                deaths = findViewById(R.id.deaths);
                deaths.setText(d);
                recovered = findViewById(R.id.recovered);
                recovered.setText(r);
                cpm = findViewById(R.id.casesPerOneMillion);
                cpm.setText(cp);
                mpm = findViewById(R.id.deathsPerOneMillion);
                mpm.setText(mp);

                // Picasso library to display the image from URL
                image_flag = (ImageView)findViewById(R.id.image_flag);
                Picasso.with(getApplicationContext())
                        .load(FLAG)
                        //.placeholder(R.drawable.user_placeholder)
                        .error(R.mipmap.pergunta)
                        //.resize(50, 50)
                        //.centerCrop()
                        .into(image_flag);

                image_flag.setOnClickListener(new View.OnClickListener() {
                    //@Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(getApplicationContext(), MapsActivity.class);
                        myIntent.putExtra ("country", p);
                        myIntent.putExtra ("cases", c);
                        myIntent.putExtra ("lat", lat);
                        myIntent.putExtra ("lng", lng);
                        // limpa a pilha de Acitivities que estão acumulando
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(myIntent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Crash API...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
