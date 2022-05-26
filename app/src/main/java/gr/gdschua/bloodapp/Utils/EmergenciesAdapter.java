package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import gr.gdschua.bloodapp.Entities.Alert;
import gr.gdschua.bloodapp.R;

public class EmergenciesAdapter extends ArrayAdapter<Alert> {
    private final Context mContext;
    private final List<Alert> alertList;

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

        TextView tv1 = listItem.findViewById(R.id.textView01);
        tv1.setText(alertList.get(position).owner);
        return listItem;
    }

}
