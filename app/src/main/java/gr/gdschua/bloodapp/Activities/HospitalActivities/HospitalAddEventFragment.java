package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.app.DatePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gr.gdschua.bloodapp.DatabaseAccess.DAOEvents;
import gr.gdschua.bloodapp.Entities.Event;
import gr.gdschua.bloodapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HospitalAddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HospitalAddEventFragment extends Fragment {

    final Calendar myCalendar= Calendar.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DAOEvents daoEvents=new DAOEvents();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HospitalAddEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HospitalAddEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HospitalAddEventFragment newInstance(String param1, String param2) {
        HospitalAddEventFragment fragment = new HospitalAddEventFragment();
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
        View view=inflater.inflate(R.layout.fragment_hospital_add_event, container, false);
        EditText editText=view.findViewById(R.id.eventDateBox);

        view.findViewById(R.id.addEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> latLonList = null;
                TextView name=view.findViewById(R.id.eventName);
                TextView addr=view.findViewById(R.id.eventAddress);
                TextView date=view.findViewById(R.id.eventDateBox);

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    latLonList = geocoder.getFromLocationName(addr.getText().toString(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Event event= new Event(name.getText().toString(),date.getText().toString(), FirebaseAuth.getInstance().getUid(),latLonList.get(0).getLatitude(),latLonList.get(0).getLongitude());
                    daoEvents.insertEvent(event);
                }
                catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(),"Address not found!",Toast.LENGTH_SHORT).show();
                }
            }

        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel(editText);
            }
        };
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),R.style.DatePickerTheme,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return view;
    }

    private void updateLabel(EditText editText){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }
}