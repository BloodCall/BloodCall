package gr.gdschua.bloodapp.Activities.HospitalActivities;


import static com.google.android.gms.vision.Detector.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.CheckIn;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;


public class HospitalQrScanFragment extends Fragment {


    public HospitalQrScanFragment() {
        // Required empty public constructor
    }

    Hospital currHospital;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hospital_qr_scan, container, false);
        DAOHospitals daoHospitals=new DAOHospitals();
        daoHospitals.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                currHospital=task.getResult().getValue(Hospital.class);
            }
        });
        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        surfaceView = view.findViewById(R.id.cameraSurfaceView);
        barcodeText = view.findViewById(R.id.textScanResult);
        initialiseDetectorsAndSources();
        return view;
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {
                        User currUser;
                        @Override
                        public void run() {
                            currUser=null;
                            barcodes.valueAt(0);
                            if (barcodes.valueAt(0).displayValue.startsWith("BLCL:")) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).displayValue.replace("BLCL:","");
                                barcodeText.setText(barcodeData);
                                barcodeDetector.release();
                                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                DAOUsers daoUsers=new DAOUsers();
                                daoUsers.getUser(barcodeData).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        currUser=task.getResult().getValue(User.class);
                                        if (currUser!=null){
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                            Date date = new Date();
                                            currUser.checkIns.add(new CheckIn(formatter.format(date),currHospital.getName()));
                                            daoUsers.updateUser(currUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                                                    ScanResultFragment fragment= ScanResultFragment.newInstance("true");
                                                    ft.replace(R.id.nav_host_fragment_content_hosp,fragment).addToBackStack(null).setReorderingAllowed(true);
                                                    ft.commit();
                                                }
                                            });
                                        }
                                        else{
                                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 150);
                                            ScanResultFragment fragment= ScanResultFragment.newInstance("false1");
                                            ft.replace(R.id.nav_host_fragment_content_hosp,fragment).addToBackStack(null).setReorderingAllowed(true);
                                            ft.commit();
                                        }
                                    }
                                });
                            } else {
                                ScanResultFragment fragment = ScanResultFragment.newInstance("false2");
                                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                ft.replace(R.id.nav_host_fragment_content_hosp, fragment).addToBackStack(null).setReorderingAllowed(true);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 150);
                                ft.commit();
                            }
                        }
                    });

                }
            }
        });
    }
}