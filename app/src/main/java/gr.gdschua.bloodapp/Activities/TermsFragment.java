package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import gr.gdschua.bloodapp.R;


public class TermsFragment extends Fragment {

    Button nextButton;
    Button prevButton;

    public TermsFragment() {
        // Required empty public constructor
    }


    public static TermsFragment newInstance(String param1, String param2) {
        TermsFragment fragment = new TermsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Terms of Use");
        View rootView = inflater.inflate(R.layout.fragment_terms, container, false);
        nextButton=rootView.findViewById(R.id.next_button_terms);
        prevButton=rootView.findViewById(R.id.back_button_terms);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(rootView).navigate(R.id.action_termsFragment_to_detailsFragment);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(rootView).popBackStack();
            }
        });

        return rootView;
    }
}