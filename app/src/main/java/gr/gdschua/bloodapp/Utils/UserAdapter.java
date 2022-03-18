package gr.gdschua.bloodapp.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import gr.gdschua.bloodapp.Entities.CheckIn;
import gr.gdschua.bloodapp.Entities.User;

public class UserAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> userList;


    public UserAdapter(@NonNull Context context, List<User> list) {
        super(context, 0, list);
        mContext = context;
        userList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_2, parent, false);

        User currUser = userList.get(position);


        TextView timeStamp = (TextView) listItem.findViewById(android.R.id.text1);
        timeStamp.setTextColor(Color.BLACK);
        timeStamp.setText(position+1+": "+currUser.getFullName());

        TextView name = (TextView) listItem.findViewById(android.R.id.text2);
        name.setTextColor(Color.BLACK);
        name.setText("XP: "+currUser.getXp());

        return listItem;
    }
}
