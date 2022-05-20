package gr.gdschua.bloodapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class UserAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final List<User> userList;


    public UserAdapter(@NonNull Context context, List<User> list) {
        super(context, 0, list);
        mContext = context;
        userList = list;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.leaderboard_user_layout, parent, false);
        }

        User currUser = userList.get(position);


        TextView pos= listItem.findViewById(R.id.position_list);
        pos.setText(position+1+".");
        pos.setTextColor(ContextCompat.getColor(mContext,R.color.changed_red));

        if(position != 0 && position != 1 && position != 2 ){
            listItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else if (position == 0) {
            listItem.setBackgroundColor(Color.parseColor("#F1E5AC"));
        }else if (position == 1) {
            listItem.setBackgroundColor(Color.parseColor("#E1E1E1"));
        }else {
            listItem.setBackgroundColor(Color.parseColor("#DCA56E"));
        }

        de.hdodenhof.circleimageview.CircleImageView profilePicture = listItem.findViewById(R.id.profilePic_list);


        try {
            profilePicture.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.default_profile));
            File localFile = File.createTempFile("uimg_"+currUser.getId(), "jpg");
            FirebaseStorage.getInstance().getReference().child("UserImages/" + currUser.getId()).getFile(localFile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profilePicture.setImageBitmap(bitmap);
                    localFile.delete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }



        TextView name = (TextView) listItem.findViewById(R.id.userName_list);
        name.setTextColor(Color.BLACK);
        name.setText(currUser.getFullName());

        TextView lvl = (TextView) listItem.findViewById(R.id.userLevel_list);
        lvl.setTextColor(Color.BLACK);
        lvl.setText("Level "+ LevelHandler.getLevel(currUser.getXp()));

        TextView xp = (TextView) listItem.findViewById(R.id.userXp_list);
        xp.setText(currUser.getXp()+" XP");

        return listItem;
    }
}
