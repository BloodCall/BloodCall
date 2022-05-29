package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.R;

public class PostAdapter extends ArrayAdapter<Post> {

    private final Context mContext;
    private final List<Post> postList;


    public PostAdapter(@NonNull Context context, List<Post> list) {
        super(context, 0, list);
        mContext = context;
        postList = list;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.forum_list_item, parent, false);
        }

        TextView authorName = listItem.findViewById(R.id.authorNameTV);
        TextView postTitle = listItem.findViewById(R.id.postTitleTV);
        TextView commentNumber = listItem.findViewById(R.id.commentNumberTV);
        ImageView authorProperty = listItem.findViewById(R.id.authorPropertyIV);

        authorName.setText(postList.get(position).getAuthorName());
        postTitle.setText(postList.get(position).getTitle());
        if(postList.get(position).getComments()==null){
            commentNumber.setText(String.valueOf(0));
        }else{
            commentNumber.setText(String.valueOf(postList.get(position).getComments().size()));
        }


        if(postList.get(position).getAuthorType().equals("User")){
            authorProperty.setImageDrawable(listItem.getResources().getDrawable(R.drawable.ic_person_black));
        }else{
            authorProperty.setImageDrawable(listItem.getResources().getDrawable(R.drawable.hospital_ic_menu));
        }
        return listItem;
    }
}
