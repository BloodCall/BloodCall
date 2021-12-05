package gr.gdschua.bloodapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gr.gdschua.bloodapp.DatabaseAcess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class HomeFragment extends Fragment {

    DAOUsers dao = new DAOUsers();
    TextView bloodTypeTV;
    TextView fullNameTextView;
    StorageReference mStroageReference;
    User currUser;
    de.hdodenhof.circleimageview.CircleImageView profilePicture;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bloodTypeTV=view.findViewById(R.id.bloodTypeTextView);
        fullNameTextView=view.findViewById(R.id.fullNameTextView);
        profilePicture=view.findViewById(R.id.profilePic);
        //this is how you retrieve data yo
        dao.getUser().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currUser= dataSnapshot.getValue(User.class);
                bloodTypeTV.setText(currUser.getBloodType());
                fullNameTextView.setText(currUser.getFullName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(view.getContext(),"FAILED DOG",Toast.LENGTH_LONG).show();
            }
        });

        mStroageReference=FirebaseStorage.getInstance().getReference().child("UserImages/"+FirebaseAuth.getInstance().getUid());

        try {
            final File localFile=File.createTempFile(FirebaseAuth.getInstance().getUid(),"jpg");
            mStroageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap=BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilePicture.setImageBitmap(bitmap);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

}