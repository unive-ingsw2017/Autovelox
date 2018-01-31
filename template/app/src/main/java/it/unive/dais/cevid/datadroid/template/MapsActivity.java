/**
 * Questo package contiene le componenti Android riusabili.
 * Si tratta di classi che contengono già funzionalità base e possono essere riusate apportandovi modifiche
 */
package it.unive.dais.cevid.datadroid.template;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;


import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


/**
 * Questa classe è la componente principale del toolkit: fornisce servizi primari per un'app basata su Google Maps, tra cui localizzazione, pulsanti
 * di navigazione, preferenze ed altro. Essa rappresenta un template che è una buona pratica riusabile per la scrittura di app, fungendo da base
 * solida, robusta e mantenibile.
 * Vengono rispettate le convenzioni e gli standard di qualità di Google per la scrittura di app Android; ogni pulsante, componente,
 * menu ecc. è definito in modo che sia facile riprodurne degli altri con caratteristiche diverse.
 * All'interno del codice ci sono dei commenti che aiutano il programmatore ad estendere questa app in maniera corretta, rispettando le convenzioni
 * e gli standard qualitativi.
 * Per scrivere una propria app è necessario modificare questa classe, aggiungendo campi, metodi e codice che svolge le funzionalità richieste.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMarkerClickListener,LocationListener {

    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;
    // alcune costanti
    private static final String TAG = "MapsActivity";


    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LatLng dest;
    private int radar_state;
    private List<MapItem> markerlist = null;
    private MapItem closestitem = null;


    /**
     * Questo oggetto è la mappa di Google Maps. Viene inizializzato asincronamente dal metodo {@code onMapsReady}.
     */
    protected GoogleMap gMap;
    /**
     * Pulsanti in sovraimpressione gestiti da questa app. Da non confondere con i pulsanti che GoogleMaps mette in sovraimpressione e che non
     * fanno parte degli oggetti gestiti manualmente dal codice.
     */
    protected ImageButton button_here, button_car, button_delete, button_radar;
    /**
     * API per i servizi di localizzazione.
     */
    protected FusedLocationProviderClient fusedLocationClient;
    /**
     * Posizione corrente. Potrebbe essere null prima di essere calcolata la prima volta.
     */
    @Nullable
    protected LatLng currentPosition = null;
    /**
     * Il marker che viene creato premendo il pulsante button_here (cioè quello dell'app, non quello di Google Maps).
     * E' utile avere un campo d'istanza che tiene il puntatore a questo marker perché così è possibile rimuoverlo se necessario.
     * E' null quando non è stato creato il marker, cioè prima che venga premuto il pulsante HERE la prima volta.
     */
    @Nullable
    protected Marker hereMarker = null;


    /**
     * Questo metodo viene invocato quando viene inizializzata questa activity.
     * Si tratta di una sorta di "main" dell'intera activity.
     * Inizializza i campi d'istanza, imposta alcuni listener e svolge gran parte delle operazioni "globali" dell'activity.
     *
     * @param savedInstanceState bundle con lo stato dell'activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // inizializza le preferenze
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        MarkerPoints = new ArrayList<>();

        // trova gli oggetti che rappresentano i bottoni e li salva come campi d'istanza
        button_here = (ImageButton) findViewById(R.id.button_here);
        button_car = (ImageButton) findViewById(R.id.button_car);
        button_delete = (ImageButton) findViewById(R.id.button_delete);
        button_radar = (ImageButton) findViewById(R.id.button_radar);

        // API per i servizi di localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // inizializza la mappa asincronamente
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // quando viene premito il pulsante HERE viene eseguito questo codice
        button_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here button clicked");
                gpsCheck();
                updateCurrentPosition();
                if (hereMarker != null) hereMarker.remove();
                if (currentPosition != null) {
                    MarkerOptions opts = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.you_are_here));
                    opts.position(currentPosition);
                    opts.title(getString(R.string.marker_title));
                    opts.snippet(String.format("lat: %g\nlng: %g", currentPosition.latitude, currentPosition.longitude));
                    hereMarker = gMap.addMarker(opts);
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, getResources().getInteger(R.integer.zoomFactor_button_here)));
                } else
                    Log.d(TAG, "no current position available");

                new Thread(new Runnable(){

                    @Override
                    public void run() {
                        demo();
                    }
                }).start();
            }
        });

        button_radar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "radar button clicked");
                gpsCheck();
                updateCurrentPosition();
                be_careful();
            }
        });
    }

    public void naviaAdestinazione(LatLng destinazione){

    }
    // ciclo di vita della app
    //

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Applica le impostazioni (preferenze) della mappa ad ogni chiamata.
     */
    @Override
    protected void onResume() {
        super.onResume();
        applyMapSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gMap.clear();
    }

    /**
     * Quando arriva un Intent viene eseguito questo metodo.
     * Può essere esteso e modificato secondo le necessità.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        double coordinate;
        double coordinate2;

        if(requestCode == 1){
            if(resultCode==Activity.RESULT_OK){
                String result = intent.getStringExtra("result");
                String result2 = intent.getStringExtra("result2");
                coordinate = Double.parseDouble(result);
                coordinate2 = Double.parseDouble(result2);

                dest = new LatLng(coordinate,coordinate2);

                System.out.println("PRIMA DELLA NAVIGAZIONE");

                try {
                    gMap.clear();
                    navigaVersoDestinazione(dest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Questo metodo viene chiamato quando viene richiesto un permesso.
     * Si tratta di un pattern asincrono che va gestito come qui impostato: per gestire nuovi permessi, qualora dovesse essere necessario,
     * è possibile riprodurre un comportamento simile a quello già implementato.
     *
     * @param requestCode  codice di richiesta.
     * @param permissions  array con i permessi richiesti.
     * @param grantResults array con l'azione da intraprende per ciascun dei permessi richiesti.
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permissions granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                } else {
                    Log.e(TAG, "permissions not granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                    Snackbar.make(this.findViewById(R.id.map), R.string.msg_permissions_not_granted, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    /**
     * Invocato quando viene creato il menu delle impostazioni.
     *
     * @param menu l'oggetto menu.
     * @return ritornare true per visualizzare il menu.
     * @see Activity#onCreateOptionsMenu(Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_with_options, menu);
        return true;
    }

    /**
     * Invocato quando viene cliccata una voce nel menu delle impostazioni.
     *
     * @param item la voce di menu cliccata.
     * @return ritorna true per continuare a chiamare altre callback; false altrimenti.
     * @see Activity#onOptionsItemSelected(MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.MENU_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.MENU_ABOUT:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.MENU_NAVIGAZIONE:
                //startActivity(new Intent(this, activity_scelta_destinazione.class));
                Intent i = new Intent(this, activity_scelta_destinazione.class);

                startActivityForResult(i, 1);
        }
        return false;
    }


    public void navigaVersoDestinazione(LatLng dest) throws InterruptedException {
        while (currentPosition==null){
            button_here.performClick();
            Thread.sleep(2000);
        }
        LatLng origin = currentPosition;
        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    // onConnection callbacks
    //
    //

    /**
     * Viene chiamata quando i servizi di localizzazione sono attivi.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param bundle il bundle passato da Android.
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(Bundle)
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "location service connected");
    }

    /**
     * Viene chiamata quando i servizi di localizzazione sono sospesi.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param i un intero che rappresenta il codice della causa della sospenzione.
     * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "location service connection suspended");
        Toast.makeText(this, R.string.conn_suspended, Toast.LENGTH_LONG).show();
    }

    /**
     * Viene chiamata quando la connessione ai servizi di localizzazione viene persa.
     * Aggiungere qui eventuale codice da eseguire in tal caso.
     *
     * @param connectionResult oggetto che rappresenta il risultato della connessione, con le cause della disconnessione ed altre informazioni.
     * @see com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener#onConnectionFailed(ConnectionResult)
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "location service connection lost");
        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }

    // maps callbacks
    //
    //

    /**
     * Chiamare questo metodo per aggiornare la posizione corrente del GPS.
     * Si tratta di un metodo proprietario, che non ridefinisce alcun metodo della superclasse né implementa alcuna interfaccia: un metodo
     * di utilità per aggiornare asincronamente in modo robusto e sicuro la posizione corrente del dispositivo mobile.
     */
    public void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requiring permission");
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location loc) {
                            if (loc != null) {
                                currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                                Log.i(TAG, "current position updated");
                            }
                        }
                    });
        }
    }

    /**
     * Viene chiamato quando si clicca sulla mappa.
     * Aggiungere qui codice che si vuole eseguire quando l'utente clicca sulla mappa.
     *
     * @param latLng la posizione del click.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // nascondi il pulsante della navigazione (non quello di google maps, ma il nostro pulsante custom)
        button_car.setVisibility(View.INVISIBLE);
        button_delete.setVisibility(View.INVISIBLE);
    }

    /**
     * Viene chiamato quando si clicca a lungo sulla mappa (long click).
     * Aggiungere qui codice che si vuole eseguire quando l'utente clicca a lungo sulla mappa.
     *
     * @param latLng la posizione del click.
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        // protected void navigate(@NonNull LatLng from, @NonNull LatLng to)
        // gpsCheck();
        // updateCurrentPosition()
/*
        LatLng pos_attuale = currentPosition;
        Location arrivo = new Location(LocationManager.GPS_PROVIDER);
        arrivo.setLatitude(45.451);
        arrivo.setLongitude(9.182);
        LatLng destinazione = new LatLng(arrivo.getLatitude(),arrivo.getLongitude());

        navigate(pos_attuale,destinazione);
        */



    }

    /**
     * Viene chiamato quando si muove la camera.
     * Attenzione: solamente al momento in cui la camera comincia a muoversi, non durante tutta la durata del movimento.
     *
     * @param reason
     */
    @Override
    public void onCameraMoveStarted(int reason) {
        setHereButtonVisibility();
    }

    /**
     * Metodo proprietario che imposta la visibilità del pulsante HERE.
     * Si occupa di nascondere o mostrare il pulsante HERE in base allo zoom attuale, confrontandolo con la soglia di zoom
     * impostanta nelle preferenze.
     * Questo comportamento è dimostrativo e non è necessario tenerlo quando si sviluppa un'applicazione modificando questo template.
     */
    public void setHereButtonVisibility() {
        if (gMap != null) {
            if (gMap.getCameraPosition().zoom < SettingsActivity.getZoomThreshold(this)) {
                button_here.setVisibility(View.INVISIBLE);
            } else {
                button_here.setVisibility(View.VISIBLE);
            }
        }
    }


    private String getUrl(LatLng origin, LatLng dest) {

        System.out.println("R-1");
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        System.out.println("R-2");
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        System.out.println("R-3");
        // Sensor enabled
        String sensor = "sensor=false";
        System.out.println("R-4");
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        System.out.println("R-5");
        // Output format
        String output = "json";
        System.out.println("R-6");
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        System.out.println("R-7");
        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                gMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Questo metodo è molto importante: esso viene invocato dal sistema quando la mappa è pronta.
     * Il parametro è l'oggetto di tipo GoogleMap pronto all'uso, che viene immediatamente assegnato ad un campo interno della
     * classe.
     * La natura asincrona di questo metodo, e quindi dell'inizializzazione del campo gMap, implica che tutte le
     * operazioni che coinvolgono la mappa e che vanno eseguite appena essa diventa disponibile, vanno messe in questo metodo.
     * Ciò non significa che tutte le operazioni che coinvolgono la mappa vanno eseguite qui: è naturale aver bisogno di accedere al campo
     * gMap in altri metodi, per eseguire operazioni sulla mappa in vari momenti, ma è necessario tenere a mente che tale campo potrebbe
     * essere ancora non inizializzato e va pertanto verificata la nullness.
     *
     * @param googleMap oggetto di tipo GoogleMap.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            gMap.setMyLocationEnabled(true);
        }

        gMap.setOnMapClickListener(this);
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng){
                FragmentManager manager = getFragmentManager();
                AddNewAutoveloxDialogActivity myDialog = new AddNewAutoveloxDialogActivity();
                myDialog.show(manager, "Add Autovelox");
                myDialog.AddAutoveloxDialog(MapsActivity.this,latLng);
            }
        });
        gMap.setOnCameraMoveStartedListener(this);
        gMap.setOnMarkerClickListener(this);

        UiSettings uis = gMap.getUiSettings();
        uis.setZoomGesturesEnabled(true);
        uis.setMyLocationButtonEnabled(true);
        gMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        gpsCheck();
                        return false;
                    }
                });
        uis.setCompassEnabled(true);
        uis.setZoomControlsEnabled(true);
        uis.setMapToolbarEnabled(true);

        applyMapSettings();

    }

    /**
     * Metodo proprietario che forza l'applicazione le impostazioni (o preferenze) che riguardano la mappa.
     */
    protected void applyMapSettings() {
        if (gMap != null) {
            Log.d(TAG, "applying map settings");
            gMap.setMapType(SettingsActivity.getMapStyle(this));
        }
        setHereButtonVisibility();
    }

    /**
     * Naviga dalla posizione from alla posizione to chiamando il navigatore di Google.
     *
     * @param from posizione iniziale.
     * @param to   posizione finale.
     */
    protected void navigate(@NonNull LatLng from, @NonNull LatLng to) {
        Intent navigation = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        navigation.setPackage("com.google.android.apps.maps");
        Log.i(TAG, String.format("starting navigation from %s to %s", from, to));
        startActivity(navigation);
    }

    // marker stuff
    //
    //

    /**
     * Callback che viene invocata quando viene cliccato un marker.
     * Questo metodo viene invocato al click di QUALUNQUE marker nella mappa; pertanto, se è necessario
     * eseguire comportamenti specifici per un certo marker o gruppo di marker, va modificato questo metodo
     * con codice che si occupa di discernere tra un marker e l'altro in qualche modo.
     *
     * @param marker il marker che è stato cliccato.
     * @return ritorna true per continuare a chiamare altre callback nella catena di callback per i marker; false altrimenti.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        button_car.setVisibility(View.VISIBLE);
        button_delete.setVisibility(View.VISIBLE);
        button_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, R.string.msg_button_car, Snackbar.LENGTH_SHORT);
                if (currentPosition != null) {
                    navigate(currentPosition, marker.getPosition());
                }
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Pulsante premuto", Snackbar.LENGTH_SHORT);
                FragmentManager manager = getFragmentManager();
                DeleteAutoveloxDialogActivity d = new DeleteAutoveloxDialogActivity();
                d.show(manager, "Add Autovelox");
                d.DeleteAutoveloxDialogActivity(MapsActivity.this,marker);

            }
        });
        return false;
    }

    /**
     * Metodo di utilità che permette di posizionare rapidamente sulla mappa una lista di MapItem.
     * Attenzione: l'oggetto gMap deve essere inizializzato, questo metodo va pertanto chiamato preferibilmente dalla
     * callback onMapReady().
     *
     * @param l   la lista di oggetti di tipo I tale che I sia sottotipo di MapItem.
     * @param <I> sottotipo di MapItem.
     * @return ritorna la collection di oggetti Marker aggiunti alla mappa.
     */
    @NonNull
    protected <I extends MapItem> Collection<Marker> putMarkersFromMapItems(List<I> l) throws Exception {
        Collection<Marker> r = new ArrayList<>();
        for (MapItem i : l) {
            MarkerOptions opts = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.autovelox_marker)).title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription());
            r.add(gMap.addMarker(opts));
        }
        return r;
    }

    /**
     * Metodo proprietario di utilità per popolare la mappa con i dati provenienti da un parser.
     * Si tratta di un metodo che può essere usato direttamente oppure può fungere da esempio per come
     * utilizzare i parser con informazioni geolocalizzate.
     *
     * @param parser un parser che produca sottotipi di MapItem, con qualunque generic Progress o Input
     * @param <I>    parametro di tipo che estende MapItem.
     * @return ritorna una collection di marker se tutto va bene; null altrimenti.
     */
    @Nullable
    protected <I extends MapItem> Collection<Marker> putMarkersFromData(@NonNull AsyncParser<I, ?> parser) {
        try {
            List<I> l = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            Log.i(TAG, String.format("parsed %d lines", l.size()));
            return putMarkersFromMapItems(l);
        } catch (Exception e) {
            Log.e(TAG, String.format("exception caught while parsing: %s", e));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Controlla lo stato del GPS e dei servizi di localizzazione, comportandosi di conseguenza.
     * Viene usata durante l'inizializzazione ed in altri casi speciali.
     */
    protected void gpsCheck() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i(TAG, "All location settings are satisfied.");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }


    // demo code

    @Nullable
    private Collection<Marker> markers;

    private void demo() {
        try {
            InputStream is = getResources().openRawResource(R.raw.autovelox);
            CsvRowParser p = new CsvRowParser(new InputStreamReader(is), true, ";", null);
            List<CsvRowParser.Row> rows = p.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            List<MapItem> l = new ArrayList<>();


            for (final CsvRowParser.Row r : rows) {
                final float [] distanza = new float[2];
                final String lat = r.get("Latitudine"), lng = r.get("Longitudine");
                double lati = currentPosition.latitude;
                double longi = currentPosition.longitude;
                Location.distanceBetween(Double.parseDouble(lat),Double.parseDouble(lng),lati,longi,distanza);
                if(distanza[0]<50000)
                    l.add(new MapItem() {
                        @Override
                        public LatLng getPosition() throws ParserException {
                            return new LatLng(Double.parseDouble(r.get("Latitudine")), Double.parseDouble(r.get("Longitudine")));
                        }

                        @Override
                        public String getTitle() throws ParserException {
                            return r.get("Identificatore in OpenStreetMap");
                        }

                        @Override
                        public String getDescription() throws ParserException {
                            return r.get("Regione");
                        }
                    });
            }

            markers = putMarkersFromMapItems(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // funzione che ritorna la lista di tutti i marker da mettere su maps

    private List<MapItem> getMarkerList(){
        List<MapItem> l = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.autovelox);
        CsvRowParser p = new CsvRowParser(new InputStreamReader(is), true, ";", null);
        List<CsvRowParser.Row> rows = null;
        try {
            rows = p.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (final CsvRowParser.Row r : rows) {
            l.add(new MapItem() {
                @Override
                public LatLng getPosition() throws ParserException {
                    return new LatLng(Double.parseDouble(r.get("Latitudine")), Double.parseDouble(r.get("Longitudine")));
                }

                @Override
                public String getTitle() throws ParserException {
                    return r.get("Identificatore in OpenStreetMap");
                }

                @Override
                public String getDescription() throws ParserException {
                    return r.get("Regione");
                }
            });
        }
        return l;
    }

    private void stop_radar(){
        radar_state=0;
    }

    // funzione che ritorna la distanza tra un elemento della mappa e la posizione attuale

    private double distance(MapItem item){
        final float [] distanza = new float[2];
        try {
            Location.distanceBetween(item.getPosition().latitude,item.getPosition().longitude,currentPosition.latitude,currentPosition.longitude,distanza);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distanza[0];
    }

    private void add_closest_point_on_map(){
        try {
            MapsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    updateCurrentPosition();
                    gpsCheck();
                    MarkerOptions opt;
                    try {
                        if(closestitem!=null) {
                            MarkerOptions opts = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.you_are_here));
                            opts.position(currentPosition);
                            opts.title(getString(R.string.marker_title));
                            opts.snippet(String.format("lat: %g\nlng: %g", currentPosition.latitude, currentPosition.longitude));
                            opt = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.autovelox_marker)).title(closestitem.getTitle()).position(closestitem.getPosition()).snippet(closestitem.getDescription());

                            gMap.clear();
                            gMap.addMarker(opts);
                            gMap.addMarker(opt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // la funzine che fa il lavoro di radar , guarda i autovelox piu vicini e
    // segnala se l'autovelox piu' vicino si trova a meno di un km distanza
    private void be_careful(){
        radar_state=1;
        button_radar.setImageResource(R.drawable.button_radar_working);
        button_radar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                button_radar.setImageResource(R.drawable.button_radar);
                stop_radar();
                gMap.clear();
                return true;
            }
        });
        if (markerlist==null) markerlist = getMarkerList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (radar_state==1){
                    minDistanceMarker(markerlist);
                    try {
                        add_closest_point_on_map();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    // funzione che ritorna il mapitem dell'autovelox piu vicino alla posizione attuale
    private void minDistanceMarker(List<MapItem> l){
        double mindistance = 0;
        double Temp;
        for (MapItem mi : l){
            if(mindistance == 0) {
                mindistance = distance(mi);
                closestitem = mi;
            }
            else{
                Temp = distance(mi);
                if (mindistance>Temp) {
                    mindistance = Temp;
                    closestitem = mi;
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }
}
