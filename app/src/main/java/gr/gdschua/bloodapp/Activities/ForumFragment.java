package gr.gdschua.bloodapp.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
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

import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.PostAdapter;
import kotlin.text.Charsets;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Handler mainThreadHandler;
    private Context thisContext;
    ArrayList<Post> postArrayList = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForumFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        ListView postListView = view.findViewById(R.id.forumListView);
        forumThread thread = new forumThread();
        thread.start();

        mainThreadHandler= new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                postListView.setAdapter((PostAdapter)(msg.obj));
                view.findViewById(R.id.progressBar2).setVisibility(View.GONE);
                if(postArrayList.isEmpty()){
                    view.findViewById(R.id.no_post_text).setVisibility(View.VISIBLE);
                }
            }
        };

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fabButton);
        floatingActionButton.setOnClickListener(view1 -> {
            AddPostFragment addPostFragment = AddPostFragment.newInstance();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_out_down);
            fragmentTransaction.replace(getParentFragment().getId(),addPostFragment).addToBackStack(null).commit();
        });

        ListView listView = view.findViewById(R.id.forumListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post clicked = (Post) adapterView.getItemAtPosition(i);
                ViewPostFragment viewPostFragment = ViewPostFragment.newInstance(clicked.getId());
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, 0, 0, R.anim.slide_out_down);
                fragmentTransaction.replace(getParentFragment().getId(),viewPostFragment).addToBackStack(null).commit();
            }
        });
        return view;
    }


    class forumThread extends Thread {
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
                url = new URL("https://europe-west1-bloodcall-951a8-default-rtdb.cloudfunctions.net/getPosts");
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
                Type postListType = new TypeToken<ArrayList<Post>>(){}.getType();
                postArrayList= gson.fromJson(result,postListType);
                PostAdapter userAdapter = new PostAdapter(thisContext,postArrayList);
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
}