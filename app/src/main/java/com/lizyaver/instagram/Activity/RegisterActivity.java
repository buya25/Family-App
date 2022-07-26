package com.lizyaver.instagram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lizyaver.instagram.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullName, emailAddress, password;
    Button btn_Register;
    TextView txt_Login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = findViewById(R.id.username);
        fullName =  findViewById(R.id.fullname);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_Register = findViewById(R.id.btn_register_final);
        txt_Login = findViewById(R.id.txt_login_final);

        auth = FirebaseAuth.getInstance();

        txt_Login.setOnClickListener(View -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btn_Register.setOnClickListener(View -> {
            pd = new ProgressDialog(this);
            pd.setMessage("Please Wait...");
            pd.show();

            String str_username = username.getText().toString();
            String str_fullName = fullName.getText().toString();
            String str_emailAdress = emailAddress.getText().toString();
            String str_password = password.getText().toString();


            if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullName) ||
                TextUtils.isEmpty(str_emailAdress) || TextUtils.isEmpty(str_password)){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else if (str_password.length() < 6){
                Toast.makeText(this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
            }else {
                Register(str_username, str_fullName, str_emailAdress, str_password);
            }

        });

    }

    private void Register(String userName, String fullName, String emailAddress, String password){
        auth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userID = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("username", userName.toLowerCase());
                        hashMap.put("fullname", fullName);
                        hashMap.put("bio", "");
                        hashMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagram-7f5bb.appspot.com/o/captain.png?alt=media&token=d8be3f86-e2a3-4009-a768-0775f3fe927f");


                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                    }else {
                        pd.dismiss();
                        Toast.makeText(this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}