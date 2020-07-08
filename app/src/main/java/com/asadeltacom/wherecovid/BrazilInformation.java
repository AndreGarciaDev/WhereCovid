package com.asadeltacom.wherecovid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class BrazilInformation extends AppCompatActivity {

    private String u, c, d, r, s, st;
    //String resposta = "{\"cases\":887057,\"deaths\":44265,\"recovered\":185291}";
    private String ufBrazil, URL = "https://covid19-brazil-api.now.sh/api/report/v1/brazil/uf/";
    private TextView ufName, cases, deaths, refuses, suspects, state;
    private ImageView image_state;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_brazil);

        Bundle extras = getIntent().getExtras();
        ufBrazil = extras.getString ("ufBrazil");
        Log.d("Posição do estado = ", ufBrazil);
        new MyAsyncTasks().execute(URL + ufBrazil);

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
                    url = new URL(URL + ufBrazil);

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

            //Log.d("data", data);
            // dismiss the progress dialog after receiving data from API
            //progressDialog.dismiss();
            try {
                // JSON Parsing of data
                JSONObject oneObject = new JSONObject(data);
                Log.d("JSONObject   >>>>>>>>>>", oneObject.getString("uf"));
                // Pulling items from the array
                u = oneObject.getString("uf");
                c = oneObject.getString("cases");
                d = oneObject.getString("deaths");
                r = oneObject.getString("refuses");
                s = oneObject.getString("suspects");
                st = oneObject.getString("state");

                String TAG = "Country Information";
                Log.d(TAG, "String recuperada do JSON estado: " + u + " - " + st);

                // display the data in UI
                ufName = findViewById(R.id.ufName);
                ufName.setText(u);
                cases = findViewById(R.id.casesBr);
                cases.setText(c);
                deaths = findViewById(R.id.deathsBr);
                deaths.setText(d);
                refuses = findViewById(R.id.refusesBr);
                refuses.setText(r);
                suspects = findViewById(R.id.suspectsBr);
                suspects.setText(s);
                state = findViewById(R.id.stateBr);
                state.setText(st);

                // Picasso library to display the image from URL
                image_state = (ImageView)findViewById(R.id.image_state);
                Picasso.with(getApplicationContext())
                        .load("https://devarthurribeiro.github.io/covid19-brazil-api/static/flags/" + u + ".png")
                        //.placeholder(R.drawable.user_placeholder)
                        .error(R.mipmap.pergunta)
                        .resize(200, 150)
                        //.centerCrop()
                        .into(image_state);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Crash API...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
