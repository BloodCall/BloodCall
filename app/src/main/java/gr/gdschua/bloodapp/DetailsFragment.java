package gr.gdschua.bloodapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class DetailsFragment extends Fragment {



    Spinner bloodTypeSpinner;
    Spinner posNegSpinner;
    de.hdodenhof.circleimageview.CircleImageView profilePicButton;
    Button nextButton;
    Button backButton;
    EditText userEmailET;
    EditText userPasswordET;
    FirebaseAuth mAuth;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                profilePicButton.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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


        mAuth = FirebaseAuth.getInstance();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //run code to save information to database
                //Toast.makeText(getActivity(),"Not Implemented", Toast.LENGTH_SHORT).show();

                userEmailET = rootView.findViewById(R.id.email_signup_box);
                userPasswordET = rootView.findViewById(R.id.password_signup_box);


                String userEmail = userEmailET.getText().toString();
                String userPassword = userPasswordET.getText().toString();

                //maybe find better way to manage wrong input than toasts
                if(userEmail.isEmpty())
                {
                    //Toast.makeText(getActivity(),"Email is empty", Toast.LENGTH_SHORT).show();
                    userEmailET.setError("Email is empty");
                    userEmailET.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
                {
                    userEmailET.setError("Enter the valid email address");
                    userEmailET.requestFocus();
                    return;
                }
                if(userPassword.isEmpty())
                {
                    userPasswordET.setError("Enter the password");
                    userPasswordET.requestFocus();
                    return;
                }
                if(userPassword.length()<6)
                {
                    userPasswordET.setError("Length of the password should be more than 6");
                    userPasswordET.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //HERE NEED TO MANAGED EXCEPTIONS LIKE IF THE USER ALREADY EXISTS!!!
                        Log.i("Result:",task.getResult().toString());

                        if(task.isSuccessful())
                        {
                            Toast.makeText(getActivity(),"You are successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"You are not Registered! Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {

                    i.putExtra("return-data", true);
                    startActivityForResult(
                            Intent.createChooser(i, "Select a profile picture"), 0);
                }catch (ActivityNotFoundException ex){
                    ex.printStackTrace();
                }
            }
        });



        return rootView;
    }
}