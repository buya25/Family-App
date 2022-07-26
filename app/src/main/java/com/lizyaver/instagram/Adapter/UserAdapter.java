package com.lizyaver.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lizyaver.instagram.Activity.MainActivity;
import com.lizyaver.instagram.Fragment.ProfileFragment;
import com.lizyaver.instagram.R;
import com.lizyaver.instagram.model.CircleImageView;
import com.lizyaver.instagram.model.User;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean inFragment;

    private FirebaseUser firebaseUser;


    public UserAdapter(Context mContext, List<User> mUser, boolean inFragment) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.inFragment = inFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUser.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        Glide.with(mContext).load(user.getImageUrl()).into(holder.image_profile);
        isFollowing(user.getId(), holder.btn_follow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(View ->{
            //using the if condition here is to avoid jumping from a fragment directly to activity
            //if its false it opens the mainActivity
            if (inFragment) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();


                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }else {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.btn_follow.setOnClickListener(View -> {
            if (holder.btn_follow.getText().toString().equals("follow")){
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).setValue(true);

                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("followers").child(firebaseUser.getUid()).setValue(true);
                addNotifications(user.getId());
            }else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).removeValue();

                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("followers").child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(userid).exists()){
                    button.setText("following");
                }else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addNotifications(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

}
