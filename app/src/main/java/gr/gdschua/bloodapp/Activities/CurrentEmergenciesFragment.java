package gr.gdschua.bloodapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Alert;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.EmergenciesAdapter;
import gr.gdschua.bloodapp.Utils.PostAdapter;
import kotlin.text.Charsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentEmergenciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentEmergenciesFragment extends Fragment {
    private Handler mainThreadHandler;
    private Context thisContext;
    private DAOHospitals daoHospitals = new DAOHospitals();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Alert> alertArrayList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CurrentEmergenciesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CurrentEmergenciesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrentEmergenciesFragment newInstance() {
        CurrentEmergenciesFragment fragment = new CurrentEmergenciesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_emergencies, container, false);
        ListView emergenciesLV =  view.findViewById(R.id.current_emergencies_lv);
        EmergenciesThread thread = new EmergenciesThread();
        thread.start();

        mainThreadHandler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                emergenciesLV.setAdapter((EmergenciesAdapter)(msg.obj));
                emergenciesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Alert alertItem = (Alert) adapterView.getItemAtPosition(i);
                        daoHospitals.getUser(alertItem.getOwner()).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    Hospital currHospital = task.getResult().getValue(Hospital.class);
                                    assert currHospital != null;

                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + currHospital.getLat() + "," + currHospital.getLon());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                }
                            }
                        });
                    }
                });
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(alertArrayList.isEmpty()){
                    view.findViewById(R.id.no_emerg_text).setVisibility(View.VISIBLE);
                }
                ((TextView)view.findViewById(R.id.emerg_count_tv)).setText(String.valueOf(alertArrayList.size()));
            }
        };


        return  view;
    }


    class EmergenciesThread extends Thread {
        Handler mHandler= new Handler(Looper.getMainLooper());

        @Override
        public synchronized void start() {
            super.start();
        }

        @Override
        public void run() {
            super.run();
            URL url = null;
            try {
                url = new URL("https://europe-west1-bloodcall-951a8-default-rtdb.cloudfunctions.net/getAlerts");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) Objects.requireNonNull(url).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(Objects.requireNonNull(urlConnection).getInputStream());
                String result = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                Gson gson = new Gson();
                Type alertListType = new TypeToken<ArrayList<Alert>>(){}.getType();
                alertArrayList= gson.fromJson(result,alertListType);
                EmergenciesAdapter emergenciesAdapter = new EmergenciesAdapter(thisContext,alertArrayList);
                Message msg = new Message();
                msg.what=200;
                msg.obj= emergenciesAdapter;
                mainThreadHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
    }
}


