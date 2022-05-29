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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOEvents;
import gr.gdschua.bloodapp.Entities.Event;
import gr.gdschua.bloodapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HospitalAddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HospitalAddEventFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final Calendar myCalendar = Calendar.getInstance();
    private final DAOEvents daoEvents = new DAOEvents();

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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hospital_add_event, container, false);
        EditText editText = view.findViewById(R.id.eventDateBox);

        view.findViewById(R.id.addEventButton).setOnClickListener(v -> {
            List<Address> latLonList = null;
            TextView name = view.findViewById(R.id.eventName);
            TextView addr = view.findViewById(R.id.eventAddress);
            TextView date = view.findViewById(R.id.eventDateBox);

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                latLonList = geocoder.getFromLocationName(addr.getText().toString(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Event event = new Event(name.getText().toString(), date.getText().toString(), FirebaseAuth.getInstance().getUid(), Objects.requireNonNull(latLonList).get(0).getLatitude(), latLonList.get(0).getLongitude());
                daoEvents.insertEvent(event).addOnCompleteListener(task -> {
                    Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.event_added, Snackbar.LENGTH_LONG);
                    requireActivity().onBackPressed();
                    snackbar.show();
                });
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), getString(R.string.add_event_addr_error), Toast.LENGTH_SHORT).show();
            }
        });

        DatePickerDialog.OnDateSetListener date = (view12, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel(editText);
        };
        editText.setOnClickListener(view1 -> {
            DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
            dpDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dpDialog.show();
        });

        return view;
    }

    private void updateLabel(EditText editText) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }
}