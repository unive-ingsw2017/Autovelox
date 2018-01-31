package it.unive.dais.cevid.datadroid.template;

import android.app.DialogFragment;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by buzdu on 1/27/2018.
 */

public class AddNewAutoveloxDialogActivity extends DialogFragment implements View.OnClickListener{
    Button CP, TP, CancelOp;
    private MapsActivity mapsActivity ;
    private LatLng latLng;

    public void AddAutoveloxDialog(MapsActivity mapsActivity, LatLng latLng){
        this.mapsActivity=mapsActivity;
        this.latLng=latLng;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_autovelox_dialog, null);
        CP = (Button) view.findViewById(R.id.CP);
        TP = (Button) view.findViewById(R.id.TP);
        CancelOp = (Button) view.findViewById(R.id.cancel_operation);
        CP.setTextColor(Color.BLUE);
        TP.setTextColor(Color.BLUE);
        CancelOp.setTextColor(Color.RED);
        CP.setOnClickListener(this);
        TP.setOnClickListener(this);
        CancelOp.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cancel_operation) {
            dismiss();
            Toast.makeText(mapsActivity,"Operation canceled",Toast.LENGTH_SHORT).show();
        }
        else {
            dismiss();
            FragmentManager manager = getFragmentManager();
            AddAutoveloxActivity myInput = new AddAutoveloxActivity();
            myInput.show(manager, "Get Input");
            if (view.getId()==R.id.CP) {
                myInput.GetUserInputAtivity(mapsActivity,latLng,"CP");
            }
            else {
                myInput.GetUserInputAtivity(mapsActivity,latLng,"TP");
            }
        }
    }
}


























