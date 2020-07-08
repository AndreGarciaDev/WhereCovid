package com.asadeltacom.wherecovid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.fragment.app.Fragment;

public class WorldFragment extends Fragment {

    private String c, d, r;
    //String resposta = "{\"cases\":887057,\"deaths\":44265,\"recovered\":185291}";
    private String resposta = "https://coronavirus-19-api.herokuapp.com/all";
    private TextView cases, deaths, recovered;

    private ProgressDialog progressDialog;
    private ProgressBar simpleProgressBar;
    private View rootView;

    public WorldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new MyAsyncTasks().execute(resposta);
        rootView =  inflater.inflate(R.layout.world_fragment, container, false);

        // initiate progress bar and start button
        simpleProgressBar = rootView.findViewById(R.id.simpleProgressBar);
        // visible the progress bar
        simpleProgressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    public class MyAsyncTasks extends AsyncTask<String, Void, String> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experience
            progressDialog = new ProgressDialog(getContext());
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
                    url = new URL(resposta);

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
        protected void onPostExecute(String s) {

            Log.d("data", s.toString());
            // dismiss the progress dialog after receiving data from API
            //progressDialog.dismiss();
            try {
                simpleProgressBar.setVisibility(View.INVISIBLE);
                // JSON Parsing of data
                JSONObject oneObject;
                oneObject = new JSONObject(s);

                //JSONArray jsonArray = new JSONArray(s);
                //JSONObject oneObject = jsonArray.getJSONObject(0);
                // Pulling items from the array
                c = oneObject.getString("cases");
                d = oneObject.getString("deaths");
                r = oneObject.getString("recovered");

                String TAG = "myApp WhereCovid";
                Log.d(TAG, "ENTROU!");

                // display the data in UI
                cases = rootView.findViewById(R.id.cases);
                cases.setText(c);
                deaths = rootView.findViewById(R.id.deaths);
                deaths.setText(d);
                recovered = rootView.findViewById(R.id.recovered);
                recovered.setText(r);
                // Picasso library to display the image from URL
                //Picasso.with(getApplicationContext())
                //        .load(image)
                //        .into(imageView);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}