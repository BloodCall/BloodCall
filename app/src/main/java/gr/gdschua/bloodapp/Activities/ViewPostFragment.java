package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOPosts;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.Comment;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.CommentAdapter;
import gr.gdschua.bloodapp.Utils.LevelHandler;
import gr.gdschua.bloodapp.Utils.ViewPostAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPostFragment extends Fragment {

    Post currPost;
    User currUser;
    Hospital currHospital;
    CommentAdapter commentAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public ViewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewPostFragment newInstance(String param1) {
        ViewPostFragment fragment = new ViewPostFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        EditText comment_body_et = view.findViewById(R.id.comment_body_ET);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        new DAOPosts().getPost(mParam1).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                currPost = task.getResult().getValue(Post.class);
                ViewPostAdapter viewPostAdapter = new ViewPostAdapter();
                viewPostAdapter.inflater = getLayoutInflater();
                viewPostAdapter.post = Collections.singletonList(currPost);
                commentAdapter = new CommentAdapter();
                commentAdapter.comments = currPost.getComments();
                commentAdapter.inflater = getLayoutInflater();
                ConcatAdapter concatAdapter = new ConcatAdapter(viewPostAdapter,commentAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(concatAdapter);
            }
        });
        ImageButton imageButton = view.findViewById(R.id.add_comment_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DAOUsers().getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        currUser = task.getResult().getValue(User.class);
                        if (currUser != null) {
                            String comment_body = comment_body_et.getText().toString();

                            if (!comment_body.isEmpty()) {
                                ArrayList<Comment> temp;
                                if (currPost.getComments() != null) {
                                    temp = currPost.getComments();
                                } else {
                                    temp = new ArrayList<>();
                                }
                                temp.add(new Comment(comment_body, currUser.getFullName(), String.valueOf(LevelHandler.getLevel(currUser.getXp())), "User"));
                                currPost.setComments(temp);
                                currPost.updateSelf();
                                comment_body_et.getText().clear();
                                commentAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(temp.size());
                            }
                        }
                        else{
                            new DAOHospitals().getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    currHospital = task.getResult().getValue(Hospital.class);
                                    String comment_body = comment_body_et.getText().toString();

                                    if (!comment_body.isEmpty()) {
                                        ArrayList<Comment> temp;
                                        if (currPost.getComments() != null) {
                                            temp = currPost.getComments();
                                        } else {
                                            temp = new ArrayList<>();
                                        }
                                        temp.add(new Comment(comment_body, currHospital.getName(), null, "Professional"));
                                        currPost.setComments(temp);
                                        currPost.updateSelf();
                                        comment_body_et.getText().clear();
                                        commentAdapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(temp.size());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        return view;
    }
}