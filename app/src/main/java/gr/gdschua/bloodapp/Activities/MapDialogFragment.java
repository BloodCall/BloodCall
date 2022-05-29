package gr.gdschua.bloodapp.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.MyDialogCloseListener;

public class MapDialogFragment extends DialogFragment {

    private MyDialogCloseListener closeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_dialog,container,false);

        TextView dismissTV = view.findViewById(R.id.textView15);
        CheckBox regularCb = view.findViewById(R.id.bloodCheckBox);
        CheckBox platCb = view.findViewById(R.id.plateletsCheckBox);
        CheckBox plasmaCb = view.findViewById(R.id.plasmaCheckBox);

        dismissTV.setOnClickListener(view1 -> {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            if(regularCb.isChecked()){
                preferences.edit().putString("regular","0").apply();
            }else{
                preferences.edit().putString("regular","null").apply();
            }
            if(platCb.isChecked()){
                preferences.edit().putString("platelets","1").apply();

            }else{
                preferences.edit().putString("platelets","null").apply();
            }
            if(plasmaCb.isChecked()){
                preferences.edit().putString("plasma","2").apply();

            }else{
                preferences.edit().putString("plasma","null").apply();
            }

            dismiss();
        });

        return view;
    }

    public void DismissListener(MyDialogCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(closeListener != null) {
            closeListener.handleDialogClose(null);
        }

    }

}
