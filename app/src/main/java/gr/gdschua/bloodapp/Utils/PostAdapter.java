package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
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
        TextView authorProperty = listItem.findViewById(R.id.authorPropertyTV);

        authorName.setText(postList.get(position).getAuthorName());
        postTitle.setText(postList.get(position).getTitle());
        authorProperty.setText(postList.get(position).getAuthorType());
        return listItem;
    }
}
