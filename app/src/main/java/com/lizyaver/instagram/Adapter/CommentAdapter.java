package com.lizyaver.instagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lizyaver.instagram.Activity.MainActivity;
import com.lizyaver.instagram.R;
import com.lizyaver.instagram.model.Comments;
import com.lizyaver.instagram.model.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private List<Comments> mComments;
    private String postid;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comments> mComments, String postid) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comments comments = mComments.get(position);

        holder.comment.setText(comments.getComment());
        getUserInfo(holder.image_profile, holder.username, comments.getPublisher());

        holder.comment.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comments.getPublisher());
            mContext.startActivity(intent);
        });

        holder.image_profile.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("publisherid", comments.getPublisher());
            mContext.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comments.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                            (dialogInterface, i) -> dialogInterface.dismiss());

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialogInterface, i) ->
                            {
                                FirebaseDatabase.getInstance().getReference("Comments")
                                        .child(postid).child(comments.getCommentid())
                                        .removeValue().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()){
                                                Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                dialogInterface.dismiss();
                            });
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
