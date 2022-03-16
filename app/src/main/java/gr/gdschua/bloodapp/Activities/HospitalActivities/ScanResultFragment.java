package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;


public class ScanResultFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private DAOUsers daoUsers = new DAOUsers();
    private User currUser;

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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_scan_result, container, false);
        ImageView ResultIV=view.findViewById(R.id.ResultIV);
        TextView  ResultTV=view.findViewById(R.id.ResultTV);

        FragmentTransaction ft= getParentFragmentManager().beginTransaction();


        if (mParam1.equals("true")) {
            ResultIV.setColorFilter(ContextCompat.getColor(getContext(), R.color.light_green), android.graphics.PorterDuff.Mode.SRC_IN);
            ResultIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_check_circle_outline_24, getContext().getTheme()));
            ResultTV.setText(getString(R.string.qr_succ));
            daoUsers.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        currUser = task.getResult().getValue(User.class);
                        currUser.setXp(currUser.getXp()+300);
                    }
                }
            });
        }
        else if(mParam1.equals("false1")){
            ResultIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_remove_circle_outline_24, getContext().getTheme()));
            ResultIV.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            ResultTV.setText(getString(R.string.qr_fail_2));
        }
        else if(mParam1.equals("false2")){
            ResultIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_remove_circle_outline_24, getContext().getTheme()));
            ResultIV.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            ResultTV.setText(getString(R.string.qr_fail_1));
        }

        view.findViewById(R.id.ResultOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}