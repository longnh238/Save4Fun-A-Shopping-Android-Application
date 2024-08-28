package com.example.save4fun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.save4fun.db.DBUsersHelper;
import com.example.save4fun.model.User;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextSignUpUsername, editTextSignUpPassword, editTextSignUpConfirmPassword;
    TextView textViewGoToLogin;
    Button buttonSignUp;
    DBUsersHelper dbUsersHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextSignUpUsername = findViewById(R.id.editTextSignUpUsername);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        editTextSignUpConfirmPassword = findViewById(R.id.editTextSignUpConfirmPassword);

        textViewGoToLogin = findViewById(R.id.textViewGoToLogin);

        buttonSignUp = findViewById(R.id.buttonSignUp);

        dbUsersHelper = new DBUsersHelper(SignUpActivity.this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextSignUpUsername.getText().toString();
                String password = editTextSignUpPassword.getText().toString();
                String confirmPassword = editTextSignUpConfirmPassword.getText().toString();
                if (username.equals("") || password.equals("") || confirmPassword.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the fields", Toast.LENGTH_LONG).show();
                } else {
                    if (password.equals(confirmPassword)) {
                        boolean userExisted = dbUsersHelper.isUserExisted(username);
                        if (!userExisted) {
                            User user = new User(username, password);
                            boolean res = dbUsersHelper.insertUser(user);
                            if (res) {
                                Toast.makeText(SignUpActivity.this, "Registered successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Passwords not matching", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        textViewGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}