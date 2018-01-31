package it.unive.dais.cevid.datadroid.template;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by buzdu on 1/28/2018.
 */

public class AddAutoveloxActivity extends DialogFragment implements View.OnClickListener{
    private Button CancelN, SubmitN;
    private EditText input_Name;
    private EditText input_Region;
    private LatLng latLng;
    private MapsActivity mapsActivity;
    private String action;

    public void GetUserInputAtivity(MapsActivity mapsActivity,LatLng latLng,String action){
        this.mapsActivity=mapsActivity;
        this.latLng=latLng;
        this.action=action;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.get_autovelox_info_from_user, null);
        CancelN = (Button) view.findViewById(R.id.cancelButton);
        SubmitN = (Button) view.findViewById(R.id.submitButton);
        input_Name = (EditText) view.findViewById(R.id.give_it_a_name);
        input_Region = (EditText) view.findViewById(R.id.region_name);
        CancelN.setTextColor(Color.RED);
        SubmitN.setTextColor(Color.BLUE);
        CancelN.setOnClickListener(this);
        SubmitN.setOnClickListener(this);
        setCancelable(false);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cancelButton){
            dismiss();
            FragmentManager manager = getFragmentManager();
            AddNewAutoveloxDialogActivity myDialog = new AddNewAutoveloxDialogActivity();
            myDialog.AddAutoveloxDialog(mapsActivity,latLng);
            myDialog.show(manager, "Add Autovelox");
        }
        else {
            String marker_name = input_Name.getText().toString();
            String marker_region = input_Region.getText().toString();
            String str = ""+marker_name+";"+marker_region+";";
            System.out.println("G-1");

            if(action=="CP") {
                dismiss();
                MarkerOptions opt = new MarkerOptions().title(marker_name).position(mapsActivity.currentPosition).snippet(marker_region);
                mapsActivity.gMap.addMarker(opt);
                // --- qui dovrebbe essere fatto l'inserimento di un autovelox con le coordinate di current_position nel file --- //

                // --- prova --- ancora da completare --- //

                Autovelox autovelox = new Autovelox("yeeeees","","","","","",1234,45,45);


                String fileName = Resources.getSystem().getResourceName(R.raw.autovelox);
                System.out.println(fileName);
                System.out.println("write csv file:");

                System.out.println("DONE");


            }
            else {
                dismiss();
                MarkerOptions opt = new MarkerOptions().title(marker_name).position(latLng).snippet(marker_region);
                mapsActivity.gMap.addMarker(opt);

                // --- qui dovrebbe essere fatto l'inserimento di un autovelox con le coordinate di tap_position nel file --- //

            }
        }
    }

}
