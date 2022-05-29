package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Alert;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

public class EmergenciesAdapter extends ArrayAdapter<Alert> {
    private final Context mContext;
    private final List<Alert> alertList;
    DAOHospitals daoHospitals = new DAOHospitals();

    public EmergenciesAdapter(@NonNull Context context, ArrayList<Alert> list) {
        super(context, 0, list);
        mContext = context;
        alertList = list;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.emergency_item, parent, false);

        TextView hospitalNameTV = listItem.findViewById(R.id.hospitalNameTV);
        TextView hospitalAddressTV = listItem.findViewById(R.id.hospitalAddressTV);
        TextView alertBloodTypeTV = listItem.findViewById(R.id.alertBloodTypeTV);


        daoHospitals.getUser(alertList.get(position).owner).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    Hospital currHospital = task.getResult().getValue(Hospital.class);
                    assert currHospital != null;
                    hospitalNameTV.setText(currHospital.getName());
                    hospitalAddressTV.setText(currHospital.getAddress());
                    alertBloodTypeTV.setText(alertList.get(position).getBloodType());
                }
            }
        });

        return listItem;
    }

}
