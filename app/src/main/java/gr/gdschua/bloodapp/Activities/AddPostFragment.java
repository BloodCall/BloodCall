package gr.gdschua.bloodapp.Activities;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOPosts;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.LevelHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPostFragment extends Fragment {

    DAOUsers daoUsers = new DAOUsers();
    DAOHospitals daoHospitals = new DAOHospitals();
    DAOPosts daoPosts = new DAOPosts();

    User currUser;
    Hospital currHospital;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPostFragment newInstance() {
        AddPostFragment fragment = new AddPostFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        Button addPostBtn = view.findViewById(R.id.createPostButton);


        Spinner dropdown = view.findViewById(R.id.flairSpinner);
        String[] items = new String[]{"Question", "Experience"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.my_selected_item, items);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        dropdown.setAdapter(adapter);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                EditText postTitle = view.findViewById(R.id.postTitleEditText);
                String postTitleStr = postTitle.getText().toString();
                EditText postBody = view.findViewById(R.id.postBodyEditText);
                String postBodyStr = postBody.getText().toString();
                TextView textView = (TextView)dropdown.getSelectedView();
                String flair = textView.getText().toString();

                daoUsers.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        currUser = task.getResult().getValue(User.class);
                        if (currUser == null){
                            daoHospitals.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    currHospital = task.getResult().getValue(Hospital.class);

                                    Post currPost = new Post(currHospital.getName(),"unavailable",postTitleStr,postBodyStr,flair,"Professional");
                                    daoPosts.insertPost(currPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.post_added), Snackbar.LENGTH_LONG);
                                            requireActivity().onBackPressed();
                                            snackbar.show();
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            Post currPost = new Post(currUser.getFullName(),String.valueOf(LevelHandler.getLevel(currUser.getXp())),postTitleStr,postBodyStr,flair,"User");
                            daoPosts.insertPost(currPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string.post_added), Snackbar.LENGTH_LONG);
                                    requireActivity().onBackPressed();
                                    snackbar.show();
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }
}