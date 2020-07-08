package com.asadeltacom.wherecovid;

import android.app.ProgressDialog;
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

import androidx.fragment.app.Fragment;

public class BrazilFragment extends Fragment {

    ListView listView;
    ArrayList<String> statesList = new ArrayList<>();
    private final static String URL = "https://covid19-brazil-api.now.sh/api/report/v1";

    private ProgressDialog progressDialog;
    private ProgressBar simpleProgressBar;
    private View rootView;

    public BrazilFragment() {
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
        rootView =  inflater.inflate(R.layout.brazil_fragment, container, false);

        // initiate progress bar and start button
        simpleProgressBar = rootView.findViewById(R.id.simpleProgressBar);
        // visible the progress bar
        simpleProgressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {

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
                //importa a String principal
                JSONObject principal = new JSONObject(data);
                //obtém o array contendo todos os "data"
                JSONArray jsonDataNode = principal.getJSONArray("data");
                //monta laço que percorre o array e imprime os dados de "data"
                for(int i=0; i < jsonDataNode.length(); i++) {

                    // Agora voce possui um determinado array na posicao i
                    JSONObject jsonChildNode = jsonDataNode.getJSONObject(i);

                    String uf = jsonChildNode.getString("uf");
                    String postTitle = jsonChildNode.getString("state");
                    String cases = jsonChildNode.getString("cases");
                    String deaths = jsonChildNode.getString("deaths");
                    String refuses = jsonChildNode.getString("refuses");
                    //String suspects = jsonChildNode.getString("suspects");

                    statesList.add(uf + "    >  " + postTitle + "\n                                    " + cases + "  >  " + deaths + "  >  " + refuses);

                    /*
                    Object o = json.get(i);
                    if(o instanceof JSONArray) {
                        // Trabalhar com o Array
                    } else if(o instanceof JSONObject) {
                        // Trabalhar com o objeto
                    }
                    */
                }

                // Get ListView object from xml
                listView = (ListView) rootView.findViewById(R.id.listViewCities);
                //conteúdo exibido quando a lista ainda estiver vazia
                listView.setEmptyView(rootView.findViewById(R.id.simpleProgressBar));
                // Define a new Adapter
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.activity_list_item, android.R.id.text1, statesList);
                // Assign adapter to ListView
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        String text = statesList.get(position);
                        String itemBrazil = text.replaceAll("[^A-Z]{2}", "");// [[:alpha:]] ^\d+$ ^[a-zA-Z]+$ [A-Za-z] /u{2}
                        String itb = itemBrazil.substring(0,2);

                        if (!itemBrazil.equals("")){
                            //Toast.makeText(getContext(), itb, Toast.LENGTH_SHORT).show();
                            Log.i("If", "Estados: " + itb);
                            Intent myIntent = new Intent(getContext(), BrazilInformation.class);
                            myIntent.putExtra ("ufBrazil", itb);
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
}
