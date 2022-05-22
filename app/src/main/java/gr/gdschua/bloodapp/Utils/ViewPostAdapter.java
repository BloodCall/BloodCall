package gr.gdschua.bloodapp.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.R;

public class ViewPostAdapter extends RecyclerView.Adapter<ViewPostAdapter.PostViewHolder>{

    public LayoutInflater inflater;
    public List<Post> post;


    @NonNull
    @Override
    public ViewPostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.full_post, parent, false);
        return new PostViewHolder(view,post);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPostAdapter.PostViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return post.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder
    {
        public PostViewHolder(@NonNull View itemView,List<Post> post) {
            super(itemView);
            TextView author = itemView.findViewById(R.id.full_post_author);
            author.setText(post.get(0).getAuthorName());

            ImageView prop = itemView.findViewById(R.id.propertyImageView);
            if(post.get(0).getAuthorType().equals("User")){
                prop.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_person_black));
            }else{
                prop.setImageDrawable(itemView.getResources().getDrawable(R.drawable.hospital_ic_menu));
            }

            TextView flair = itemView.findViewById(R.id.full_post_flair);
            flair.setText(post.get(0).getFlair());

            TextView lvl = itemView.findViewById(R.id.full_post_lvl);
            if(!lvl.equals("unknown")){
                lvl.setText("Level " + post.get(0).getAuthorLevel());
            }

            TextView title = itemView.findViewById(R.id.full_post_title);
            title.setText(post.get(0).getTitle());

            TextView body = itemView.findViewById(R.id.full_post_body);
            body.setText(post.get(0).getBody());

        }
    }
}
