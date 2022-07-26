package com.lizyaver.instagram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lizyaver.instagram.R;

public class LoginActivity extends AppCompatActivity {

    EditText emailAdress, password;
    Button login;
    TextView txt_login;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        emailAdress = findViewById(R.id.email_log);
        password = findViewById(R.id.password_log);
        login = findViewById(R.id.btn_Login_log);
        txt_login = findViewById(R.id.txt_login_log);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(View -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        login.setOnClickListener(View -> {
            ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Please wait...");
            pd.show();

            String str_email = emailAdress.getText().toString();
            String str_password = password.getText().toString();

            if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else {
                auth.signInWithEmailAndPassword(str_email, str_password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(auth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
                            }else {
                                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}