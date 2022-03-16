package gr.gdschua.bloodapp.Activities;

import static android.view.View.LAYER_TYPE_SOFTWARE;
import static android.view.View.inflate;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.ByteArrayOutputStream;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class MarkerInfoFragment extends DialogFragment {

    private final DAOUsers daoUsers = new DAOUsers();
    private User currUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_marker_info, container, false);
        TextView tv = view.findViewById(R.id.textViewMarkerTitle);
        TextView tv2 = view.findViewById(R.id.textViewMarkerCenter);

        View inflatedView = getLayoutInflater().inflate(R.layout.share_event_layout, null);

        TextView organizerName = inflatedView.findViewById(R.id.organizerShareName);
        TextView organizerAddress = inflatedView.findViewById(R.id.organizerShareAddress);
        tv.setText(getArguments().get("name").toString());

        if (getArguments().get("organizer") != null) {
            tv2.setText(String.format(getString(R.string.marker_type_1), getArguments().get("address").toString(), getArguments().get("organizer").toString(), getArguments().get("email").toString()));
        } else {
            tv2.setText(String.format(getString(R.string.marker_type_2), getArguments().get("address").toString(), getArguments().get("email").toString()));
        }


        Button closeButton = view.findViewById(R.id.closeDialogButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button shareButton = view.findViewById(R.id.shareEventButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //convert layout to bitmap to share
                organizerName.setText(getArguments().get("name").toString());
                organizerAddress.setText(getArguments().get("address").toString());
                FrameLayout fLayout = inflatedView.findViewById(R.id.frameLayout);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_blood_drop);
                Bitmap bm = Bitmap.createBitmap( 1080,2177, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bm);
                fLayout.measure(Resources.getSystem().getDisplayMetrics().widthPixels,Resources.getSystem().getDisplayMetrics().heightPixels);
                fLayout.layout(0, 0, 1080,2177);
                fLayout.setLayerType(LAYER_TYPE_SOFTWARE, null);
                fLayout.draw(c);

                daoUsers.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            currUser = task.getResult().getValue(User.class);
                            if (bm != null) {

                                Toast toast = Toast.makeText(getContext(), "Image was saved", Toast.LENGTH_LONG);
                                toast.show();
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("image/jpeg");
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bm, "Title", null);
                                Uri imageUri =  Uri.parse(path);
                                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                                startActivity(Intent.createChooser(share, "Select"));
                                currUser.setXp(currUser.getXp()+25);
                                daoUsers.updateUser(currUser);
                                dismiss();

                            } else {
                                Toast toast = Toast.makeText(getContext(),
                                        "No image saved.", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    }
                });






            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.30f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

}