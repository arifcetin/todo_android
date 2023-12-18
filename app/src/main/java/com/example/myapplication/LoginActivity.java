package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;



public class LoginActivity extends AppCompatActivity {
    private EditText userName;
    private EditText userPassword;
    private Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.editTextTextPersonName);
        userPassword = findViewById(R.id.editTextNumberPassword);
        login = findViewById(R.id.button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String password = userPassword.getText().toString();

                if(name.equals("halenur") && password.equals("123")){
                    openActivity();
                }
                else
                    login.setBackgroundColor(Color.RED);
            }
        });
    }

    public void openActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}