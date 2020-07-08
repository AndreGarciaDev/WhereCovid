package com.asadeltacom.wherecovid;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Mapa da aplicação
    private GoogleMap mMap;
    // Responsável por disponibilizar a localização do smartphone
    private GoogleApiClient mGoogleApiClient;
    // Guarda a ultima posição do smartphone
    private Location mLastLocation;
    //private Object LocationServices;

    private Double lat, lng;
    private String country, cases;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            country = extras.getString("country");
            cases = extras.getString("cases");
            lat = extras.getDouble("lat");
            lng = extras.getDouble("lng");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Vamos instanciar o GoogleApiClient, caso seja nulo
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this) // Interface ConnectionCallbacks
                    .addOnConnectionFailedListener(this) //Interface OnConnectionFailedListener
                    .addApi(LocationServices.API) // Vamos a API do LocationServices
                    .build();
        }
        Button button;
        Switch sw;

        button = (Button) findViewById(R.id.btEstat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });

        button = (Button) findViewById(R.id.btNav);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BrowserWindow.class);
                startActivity(intent);
            }
        });

        // initiate a Switch
        sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Toast.makeText(getApplicationContext(), "Not implemented...", Toast.LENGTH_LONG).show();
                } else {
                    // The toggle is disabled

                }
            }
        });
    }

    // Ao iniciar, conectamos
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    //Ao finalizar, desconectamos
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (lat != null && lng != null) {
            String TAG = "RECEBIDO: ";
            Log.v(TAG, "intent FLAG - lat: " + lat + "  lng: " + lng);
            // Adicionar localidade, a partir do click em uma flag
            LatLng flag = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(flag).title(country + " cases: " + cases));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(flag));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(flag, 3));
        }
    }

    //Método invocado quando o GoogleApiClient conseguir se conectar
    @Override
    public void onConnected(Bundle bundle) {
        //Também é preciso que você implemente para a solicitação de geolocalização.
        // você deverá tratar caso não consiga
        // pegamos a ultima localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (lat == null && lng == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                if(mMap != null){
                    // Criamos o LatLng através do Location
                    final LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    // Adicionamos um Marker com a posição...
                    mMap.addMarker(new MarkerOptions().position(latLng).title("WhereCovid"));
                    // Um zoom no mapa para a sua posição atual...
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //Neste método você deverá tratar caso não consiga se conectar
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.final_1);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(R.string.final_2);
        builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //sound(0);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
