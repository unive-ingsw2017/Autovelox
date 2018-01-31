package it.unive.dais.cevid.datadroid.template;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


public class activity_scelta_destinazione extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {



    String location="milano";
    List<Address> addressList;

    Button geolocationBt;

    LatLng latLng;

    GoogleMap gMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_destinazione);

        geolocationBt = (Button) findViewById(R.id.button2);
        geolocationBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText searchText = (EditText) findViewById(R.id.fname);
                String search = searchText.getText().toString();
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(activity_scelta_destinazione.this);
                try {
                    addressList = geocoder.getFromLocationName(search, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address address = addressList.get(0);
               // System.out.println("Go PREMUO EL BOTTON, GO CERCA UN POSTO CHE COMPARE QUA "+address.getLatitude()+" e "+address.getLongitude());
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", String.valueOf(latLng.latitude));
                returnIntent.putExtra("result2", String.valueOf(latLng.longitude));
                setResult(Activity.RESULT_OK, returnIntent);
                System.out.println("Ho chiuso la activity della ricerca");
            //   tragitto t = new tragitto(latLng);
                finish();

            }
        });


    }

    public LatLng getDestinazione(){
        return latLng;
    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

}