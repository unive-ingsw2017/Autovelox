package it.unive.dais.cevid.datadroid.template;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by buzdu on 1/30/2018.
 */

public class DeleteAutoveloxDialogActivity extends DialogFragment implements View.OnClickListener{
    private Button CancelD, Yes;
    private Marker marker;
    private MapsActivity mapsActivity;

    public void DeleteAutoveloxDialogActivity(MapsActivity mapsActivity,Marker marker){
        this.mapsActivity=mapsActivity;
        this.marker=marker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_autovelox_dialog, null);
        CancelD = (Button) view.findViewById(R.id.cancel_delete_autovelox_operation);
        Yes = (Button) view.findViewById(R.id.confirm_delete_autovelox_operation);
        CancelD.setTextColor(Color.RED);
        Yes.setTextColor(Color.BLUE);
        CancelD.setOnClickListener(this);
        Yes.setOnClickListener(this);
        setCancelable(false);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cancel_delete_autovelox_operation){
            dismiss();
            Toast.makeText(mapsActivity,"Operazione eliminazione cancellata",Toast.LENGTH_SHORT).show();
        }
        else {
            dismiss();
            marker.setVisible(false);

            //-- da implementare la eliminazione dell'autovelox --//

            Toast.makeText(mapsActivity,"L'autovelox è stato eliminato",Toast.LENGTH_SHORT).show();

        }
    }
}
