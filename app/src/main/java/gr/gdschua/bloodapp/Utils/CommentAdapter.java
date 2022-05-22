package gr.gdschua.bloodapp.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gr.gdschua.bloodapp.Entities.Comment;
import gr.gdschua.bloodapp.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    public LayoutInflater inflater;
    public List<Comment> comments;

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        holder.auth.setText(comments.get(position).getAuthor());
        if(comments.get(position).getAuthorProperty().equals("User")){
            holder.prop.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_person_black));
        }else{
            holder.prop.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.hospital_ic_menu));
        }
        if(comments.get(position).getUserLvl() != null){
            holder.lvl.setText("Level " + comments.get(position).getUserLvl());
        }
        holder.body.setText(comments.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        if (comments != null){
            return comments.size();
        }
        return 0;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        TextView auth;
        ImageView prop;
        TextView lvl;
        TextView body;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            auth = itemView.findViewById(R.id.comment_author);

            prop = itemView.findViewById(R.id.imageViewProp);

            lvl = itemView.findViewById(R.id.comment_auth_lvl);

            body = itemView.findViewById(R.id.comm_text);
        }
    }

}
