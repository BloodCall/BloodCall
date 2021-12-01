package gr.gdschua.bloodapp;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


public class DetailsFragment extends Fragment {

    Spinner bloodTypeSpinner;
    Spinner posNegSpinner;
    ImageButton profilePicButton;
    Button nextButton;
    Button backButton;

    public DetailsFragment() {
        // Required empty public constructor
    }


    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Your Details");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        bloodTypeSpinner=rootView.findViewById(R.id.bloodtype_spinner);
        posNegSpinner=rootView.findViewById(R.id.bloodtype_spinner_pos_neg);
        profilePicButton=rootView.findViewById(R.id.profile_picture_button);
        nextButton=rootView.findViewById(R.id.next_button_details);
        backButton=rootView.findViewById(R.id.back_button_details);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(rootView).popBackStack();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Not Implemented", Toast.LENGTH_SHORT).show();
            }
        });

        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Not Implemented",Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }
}