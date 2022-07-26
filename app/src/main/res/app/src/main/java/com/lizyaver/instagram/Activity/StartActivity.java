package com.lizyaver.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lizyaver.instagram.R;

public class StartActivity extends AppCompatActivity {

    Button login, register;
    FirebaseUser firebaseUser;


    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //redirect if the user is null
        if (firebaseUser != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login_start);
        register = findViewById(R.id.register_start);

        login.setOnClickListener(View -> {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        });

        register.setOnClickListener(View -> {
            startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        });
    }
}