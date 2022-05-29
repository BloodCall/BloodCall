package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;


public class ScanResultFragment extends Fragment {

    private Context thisContext;
    private static final String ARG_PARAM1 = "param1";
    private final DAOHospitals daoHospitals = new DAOHospitals();
    private String mParam1;
    private Hospital currHospital;

    public ScanResultFragment() {
    }

    public static ScanResultFragment newInstance(String param1) {
        ScanResultFragment fragment = new ScanResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        thisContext = getActivity();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_result, container, false);
        ImageView ResultIV = view.findViewById(R.id.ResultIV);
        TextView ResultTV = view.findViewById(R.id.ResultTV);

        FragmentTransaction ft = getParentFragmentManager().beginTransaction();


        switch (mParam1) {
            case "true":
                ResultIV.setColorFilter(ContextCompat.getColor(thisContext, R.color.light_green), android.graphics.PorterDuff.Mode.SRC_IN);
                ResultIV.setImageDrawable(ContextCompat.getDrawable(thisContext, R.drawable.ic_baseline_check_circle_outline_24));
                ResultTV.setText(getString(R.string.qr_succ));
                daoHospitals.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            currHospital = task.getResult().getValue(Hospital.class);
                            if (currHospital != null) {
                                currHospital.setServiced(currHospital.getServiced() + 1);
                                daoHospitals.updateUser(currHospital);
                            }

                        }
                    }
                });
                break;
            case "false1":
                ResultIV.setImageDrawable(ContextCompat.getDrawable(thisContext, R.drawable.ic_baseline_remove_circle_outline_24));
                ResultIV.setColorFilter(ContextCompat.getColor(thisContext, android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
                ResultTV.setText(getString(R.string.qr_fail_2));
                break;
            case "false2":
                ResultIV.setImageDrawable(ContextCompat.getDrawable(thisContext, R.drawable.ic_baseline_remove_circle_outline_24));
                ResultIV.setColorFilter(ContextCompat.getColor(thisContext, android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
                ResultTV.setText(getString(R.string.qr_fail_1));
                break;
        }

        view.findViewById(R.id.ResultOK).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        return view;
    }
}