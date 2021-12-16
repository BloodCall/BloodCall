package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import gr.gdschua.bloodapp.DatabaseAcess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HospitalHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HospitalHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    DAOHospitals daoHospitals = new DAOHospitals();
    Hospital currUser;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HospitalHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HospitalHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HospitalHomeFragment newInstance(String param1, String param2) {
        HospitalHomeFragment fragment = new HospitalHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_hospital_home, container, false);
        TextView email=view.findViewById(R.id.hosp_emailTextView);
        TextView name=view.findViewById(R.id.hosp_fullNameTextView);
        TextView address=view.findViewById(R.id.hosp_adrressTextView);
        daoHospitals.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currUser = task.getResult().getValue(Hospital.class);
                    email.setText(currUser.getEmail());
                    name.setText(currUser.getName());
                    address.setText(currUser.getAddress(getActivity()));
                }
            }
        });


        return view;
    }
}