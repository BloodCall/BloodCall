package gr.gdschua.bloodapp.Activities;

import android.content.Context;
import android.os.Bundle;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

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

import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.UserAdapter;
import kotlin.text.Charsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    private Context thisContext;
    private Handler mainThreadHandler;
    private final leaderboardThread workerThread = null;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LeaderboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaderboardFragment newInstance() {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        thisContext = getActivity();
        Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    class leaderboardThread extends Thread {
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
                url = new URL("https://europe-west1-bloodcall-951a8-default-rtdb.cloudfunctions.net/getUsersforLeaderboard");
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
                Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
                ArrayList<User> userArrayList= gson.fromJson(result,userListType);
                UserAdapter userAdapter = new UserAdapter(thisContext,userArrayList);
                Message msg = new Message();
                msg.what=200;
                msg.obj=userAdapter;
                mainThreadHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        ProgressBar progressBar = view.findViewById(R.id.progressBar3);
        ListView listView = view.findViewById(R.id.LeaderboardLV);
        leaderboardThread thread = new leaderboardThread();
        thread.start();

        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listheader, listView, false);
        listView.addHeaderView(header, null, false);

        mainThreadHandler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                progressBar.setVisibility(View.GONE);
                listView.setAdapter((UserAdapter)(msg.obj));
            }
        };

        return view;
    }



}