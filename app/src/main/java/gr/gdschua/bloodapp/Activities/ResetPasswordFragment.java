package gr.gdschua.bloodapp.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    
    private final Context thisContext = getActivity();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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

        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        EditText currPW = view.findViewById(R.id.currPW);
        EditText newPW = view.findViewById(R.id.newPW_1);
        EditText repeatNewPW = view.findViewById(R.id.newPW_2);
        Button applyBTN = view.findViewById(R.id.button);

        new DAOUsers().getUser().addOnSuccessListener(dataSnapshot -> applyBTN.setOnClickListener(v -> {
            if (!newPW.getText().toString().equals(repeatNewPW.getText().toString())) {
                new AlertDialog.Builder(thisContext)
                        .setMessage(R.string.pwd_not_matching)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(dataSnapshot.getValue(User.class).getEmail(), currPW.getText().toString());

                Objects.requireNonNull(user).reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPW.getText().toString()).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(thisContext, getString(R.string.pwd_updated), Toast.LENGTH_LONG).show();
                                        getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_user, new HomeFragment()).commit();
                                    } else {
                                        Toast.makeText(thisContext,  getString(R.string.pwd_upd_error), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                new AlertDialog.Builder(thisContext)
                                        .setMessage(R.string.pwd_wrong_curr)
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                        });
            }
        }));
        return view;
    }
}