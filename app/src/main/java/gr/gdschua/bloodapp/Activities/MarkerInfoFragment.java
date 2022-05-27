package gr.gdschua.bloodapp.Activities;

import static android.view.View.LAYER_TYPE_SOFTWARE;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class MarkerInfoFragment extends DialogFragment {

    private Context thisContext;
    private final DAOUsers daoUsers = new DAOUsers();
    private User currUser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_marker_info, container, false);
        TextView tv = view.findViewById(R.id.textViewMarkerTitle);
        TextView tv2 = view.findViewById(R.id.textViewMarkerCenter);
        ImageView iv = view.findViewById(R.id.imageView2);

        Button navigateBtn = view.findViewById(R.id.navigateBtn);


        View inflatedView = getLayoutInflater().inflate(R.layout.share_event_layout, null);

        TextView eventName = inflatedView.findViewById(R.id.eventShareName);
        TextView organizerTV = inflatedView.findViewById(R.id.organizerTV);
        TextView eventIntro = inflatedView.findViewById(R.id.eventIntroTV);
        TextView organizerAddress = inflatedView.findViewById(R.id.organizerShareAddress);
        tv.setText(requireArguments().get("name").toString());

        if (Objects.requireNonNull(requireArguments()).get("organizer") != null) {
            navigateBtn.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + requireArguments().get("lat") + "," + requireArguments().get("lon"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            });
            iv.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_event, null));
            tv2.setText(String.format(getString(R.string.marker_type_1), getArguments().get("address").toString(), getArguments().get("organizer").toString(), getArguments().get("date"), getArguments().get("email").toString()));
            Button shareButton = view.findViewById(R.id.shareEventButton);
            shareButton.setOnClickListener(view13 -> {
                //convert layout to bitmap to share
                eventName.setText(requireArguments().get("name").toString());
                organizerTV.setText(getArguments().get("organizer").toString());
                organizerAddress.setText(getArguments().get("address").toString());
                FrameLayout fLayout = inflatedView.findViewById(R.id.frameLayout);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_blood_drop);
                Bitmap bm = Bitmap.createBitmap(1080, 2177, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bm);
                fLayout.measure(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
                fLayout.layout(0, 0, 1080, 2177);
                fLayout.setLayerType(LAYER_TYPE_SOFTWARE, null);
                fLayout.draw(c);

                daoUsers.getUser().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currUser = task.getResult().getValue(User.class);
                        if (bm != null) {

                            Toast toast = Toast.makeText(getContext(), getString(R.string.info_img_saved), Toast.LENGTH_LONG);
                            toast.show();
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/jpeg");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            String path = MediaStore.Images.Media.insertImage(thisContext.getContentResolver(), bm, "Title", null);
                            Uri imageUri = Uri.parse(path);
                            share.putExtra(Intent.EXTRA_STREAM, imageUri);
                            startActivity(Intent.createChooser(share, "Select"));
                            if (currUser != null) {
                                currUser.setXp(currUser.getXp() + 25);
                                daoUsers.updateUser(currUser);
                            }
                            dismiss();

                        } else {
                            Toast toast = Toast.makeText(getContext(),
                                    getString(R.string.info_no_img_saved), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            });
        } else {
            navigateBtn.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + requireArguments().get("lat") + "," + requireArguments().get("lon"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            });
            tv2.setText(String.format(getString(R.string.marker_type_2), getArguments().get("address").toString(), getArguments().get("email").toString()));
            Button shareButton = view.findViewById(R.id.shareEventButton);
            shareButton.setOnClickListener(view12 -> {

                //convert layout to bitmap to share
                eventName.setText(requireArguments().get("name").toString());
                eventIntro.setVisibility(View.INVISIBLE);
                organizerAddress.setText(getArguments().get("address").toString());
                FrameLayout fLayout = inflatedView.findViewById(R.id.frameLayout);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_blood_drop);
                Bitmap bm = Bitmap.createBitmap(1080, 2177, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bm);
                fLayout.measure(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
                fLayout.layout(0, 0, 1080, 2177);
                fLayout.setLayerType(LAYER_TYPE_SOFTWARE, null);
                fLayout.draw(c);
                if (bm != null) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.info_img_saved), Toast.LENGTH_LONG);
                    toast.show();
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(thisContext.getContentResolver(), bm, "Title", null);
                    Uri imageUri = Uri.parse(path);
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    startActivity(Intent.createChooser(share, "Select"));
                    dismiss();

                } else {
                    Toast toast = Toast.makeText(getContext(),
                            getString(R.string.info_no_img_saved), Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }


        Button closeButton = view.findViewById(R.id.closeDialogButton);
        closeButton.setOnClickListener(view1 -> dismiss());
        ImageView regularIV = view.findViewById(R.id.regularIV);
        ImageView plateletsIV = view.findViewById(R.id.plateletsIV);
        ImageView plasmaIV = view.findViewById(R.id.plasmaIV);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        assert getArguments() != null;
        if (getArguments().get("accepts") != null) {
            ArrayList<String> accepts = (ArrayList<String>) getArguments().get("accepts");
            for(int i=0;i<accepts.size();i++){
                if(accepts.get(i).equals("0")){
                    regularIV.setVisibility(View.VISIBLE);
                }else if(accepts.get(i).equals("1")){
                    plateletsIV.setVisibility(View.VISIBLE);
                }else if(accepts.get(i).equals("2")){
                    plasmaIV.setVisibility(View.VISIBLE);
                }
            }
        }


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.30f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }


}