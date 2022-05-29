package gr.gdschua.bloodapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.BitmapResizer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final Context thisContext = getActivity();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    de.hdodenhof.circleimageview.CircleImageView profilePic;
    Bitmap profilePictureBitmap;



    // TODO: Rename and change types of parameters
    private String mParam1;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        profilePic = view.findViewById(R.id.profilePic);
        EditText fName = view.findViewById(R.id.hospSignupName);
        EditText lName = view.findViewById(R.id.hospSignupAddr);
        EditText email = view.findViewById(R.id.hospSignupEmail);
        Spinner bloodTypeSpinner = view.findViewById(R.id.bloodtype_spinner);
        Spinner posNegSpinner = view.findViewById(R.id.bloodtype_spinner_pos_neg);
        Button updateBtn = view.findViewById(R.id.updateBtn);
        Button changePwBtn = view.findViewById(R.id.changePasswordBtn);

        final ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    try {
                        profilePictureBitmap = BitmapResizer.processBitmap(Objects.requireNonNull(data).getData(), 400, requireActivity());
                        profilePic.setImageBitmap(profilePictureBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        new DAOUsers().getUser().addOnSuccessListener(dataSnapshot -> {
            User currUser = dataSnapshot.getValue(User.class);
            String[] nameSpl = currUser.getFullName().split("\\s+");
            fName.setText(nameSpl[0]);
            lName.setText(nameSpl[1]);
            email.setText(currUser.getEmail());
            String[] bldSpl = currUser.getBloodType().split("((?=[+-])|(?<=[+-]))");
            bloodTypeSpinner.setSelection(((ArrayAdapter<String>)bloodTypeSpinner.getAdapter()).getPosition(bldSpl[0]));
            posNegSpinner.setSelection(((ArrayAdapter<String>)posNegSpinner.getAdapter()).getPosition(bldSpl[1]));


            profilePic.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*").putExtra("outputX", 240).putExtra("outputY", 240).putExtra("aspectX", 1).putExtra("aspectY", 1).putExtra("scale", true);
                launchGalleryActivity.launch(intent);
            });

            updateBtn.setOnClickListener(v -> {

                Thread t = new Thread(() -> {
                    FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic());
                    currUser.updateSelf();
                });
                if (!email.getText().toString().equals(currUser.getEmail())) {
                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(email.getText().toString());
                }
                FirebaseMessaging.getInstance().unsubscribeFromTopic(currUser.getTopic());
                currUser.setEmail(email.getText().toString());
                currUser.setFullName(fName.getText() + " " + lName.getText());
                currUser.setBloodType(bloodTypeSpinner.getSelectedItem().toString().trim() + posNegSpinner.getSelectedItem().toString().trim());
                t.start();
                if (profilePictureBitmap != null) {
                    File outputFile = null;
                    try {
                        outputFile = File.createTempFile("profPic" + Math.random(), ".png", thisContext.getCacheDir());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    profilePictureBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(outputFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FirebaseStorage.getInstance().getReference("UserImages").child(currUser.getId()).putFile(Uri.fromFile(outputFile));
                }
                getParentFragmentManager().popBackStack();
            });
        });

        view.findViewById(R.id.changePasswordBtn).setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_user,ResetPasswordFragment.newInstance(null,null)).commit());


        try {
            File localFile = File.createTempFile(mParam1, "jpg");
             FirebaseStorage.getInstance().getReference().child("UserImages/" + mParam1).getFile(localFile).addOnCompleteListener(task -> {
                 if (task.isSuccessful()) {
                     Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                     profilePic.setImageBitmap(bitmap);
                     localFile.delete();
                 } else {
                     Log.e("ERROR", "IMAGE NOT FOUND!");
                 }
             });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}