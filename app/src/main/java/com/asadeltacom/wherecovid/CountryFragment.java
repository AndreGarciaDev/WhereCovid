package com.asadeltacom.wherecovid;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

public class CountryFragment extends Fragment {

    ListView listView;
    ArrayList<String> countriesList = new ArrayList<>();
    //private final static String URL = "https://coronavirus-19-api.herokuapp.com/countries";
    //private final static String URL = "https://corona.lmao.ninja/countries";
    private final static String URL = "https://corona.lmao.ninja/v2/countries";

    private ProgressDialog progressDialog;
    private ProgressBar simpleProgressBar;
    private View rootView;

    //private FloatingActionButton fab;

    public CountryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new FetchDataTask().execute(URL);
        rootView =  inflater.inflate(R.layout.country_fragment, container, false);

        criarNotificacao(); // Notificar sobre click na flag

        // initiate progress bar and start button
        simpleProgressBar = rootView.findViewById(R.id.simpleProgressBar);
        // visible the progress bar
        simpleProgressBar.setVisibility(View.VISIBLE);

        /*fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        */
        return rootView;
    }

    private class FetchDataTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            InputStream inputStream = null;
            String result= null;
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);

            try {

                HttpResponse response = client.execute(httpGet);
                inputStream = response.getEntity().getContent();

                // convert input stream to string
                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                    Log.i("App", "Data received:" +result);

                }
                else
                    result = "Failed to fetch data";

                return result;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dataFetched) {
            //parse the JSON data and then display
            parseJSON(dataFetched);
        }


        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        private void parseJSON(String data){

            try{
                simpleProgressBar.setVisibility(View.INVISIBLE);
                JSONArray jsonMainNode = new JSONArray(data);

                int jsonArrLength = jsonMainNode.length();

                for(int i=0; i < jsonArrLength; i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String postTitle = jsonChildNode.getString("country");
                    String cases = jsonChildNode.getString("cases");
                    String deaths = jsonChildNode.getString("deaths");
                    String recovered = jsonChildNode.getString("recovered");
                    //String casesPerOneMillion = jsonChildNode.getString("casesPerOneMillion");
                    //String deathsPerOneMillion = jsonChildNode.getString("deathsPerOneMillion");
                    countriesList.add(postTitle + "\n                         " + cases + "  >  " + deaths + "  >  " + recovered);
                }

                // Get ListView object from xml
                listView = (ListView) rootView.findViewById(R.id.listViewCountries);
                //conteúdo exibido quando a lista ainda estiver vazia
                listView.setEmptyView(rootView.findViewById(R.id.simpleProgressBar));
                // Define a new Adapter
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.activity_list_item, android.R.id.text1, countriesList);
                // Assign adapter to ListView
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        String text = countriesList.get(position);
                        String itemPais = text.replaceAll("[^a-zA-Zçéô.'-\\(\\)]* [^a-zA-Zçéô.'-\\(\\)]+", "");// [[:alpha:]] ^\d+$ ^[a-zA-Z]+$ [A-Za-z]

                        if (!itemPais.equals("")){
                            Toast.makeText(getContext(), "click " + itemPais, Toast.LENGTH_SHORT).show();
                            Log.i("If", "Countries: " + itemPais);
                            Intent myIntent = new Intent(getActivity(), CountryInformation.class);
                            myIntent.putExtra ("country", itemPais);
                            startActivity(myIntent);
                        } else {
                            Toast.makeText(getContext(), ". . .", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }catch(Exception e){
                Log.i("App", "Error parsing data" +e.getMessage());
                Toast.makeText(getContext(), "Crash API...", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void criarNotificacao(){
        int id = 1;
        String titulo = "Busca de local no mapa.";
        String texto = "Clicar na bandeira leva ao mapa.";
        //int icone = android.R.drawable.ic_dialog_info;
        int icone = android.R.mipmap.sym_def_app_icon;

        Intent intent = new Intent(getContext(), StatisticsActivity.class);
        PendingIntent p = getPendingIntent(id, intent, getContext());

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(getContext());
        notificacao.setSmallIcon(icone);
        notificacao.setContentTitle(titulo);
        notificacao.setContentText(texto);
        notificacao.setContentIntent(p);

        NotificationManagerCompat nm = NotificationManagerCompat.from(getContext());
        nm.notify(id, notificacao.build());
    }

    private PendingIntent getPendingIntent(int id, Intent intent, Context context){
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);

        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return p;
    }

}