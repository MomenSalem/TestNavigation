package com.example.testnavigation;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Log in section
        Button signInButton = findViewById(R.id.signInButton);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);


        // Sign up section
        Button signUpButton = findViewById(R.id.goToSignUpButton);

        try {
            dataBaseHelper = new DataBaseHelper(MainActivity.this, "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataBaseHelper != null) {
                // Assuming DataBaseHelper has a method to close the database or cleanup
                dataBaseHelper.close();
            }
        }

        // if the user press on submit, we should check if it is the data base
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the email and password are correct
                if (!areEmptyEmailAddressAndPassword(emailEditText, passwordEditText)){

                    // Check if this user is in the database
                    if (isUserExists(emailEditText, passwordEditText)){
                        goToHomeActivity();
                    }

                }else {
                    showAlertDialog(Constants.EMPTY_EMAIL_ADDRESS_AND_PASSWORD);
                }
            }
        });

        // if the user press on the sign up button, we should go to the sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHomeActivity();
            }
        });
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showAlertDialog(String alertMessage) {
        new AlertDialog.Builder(MainActivity.this).setMessage(alertMessage)
                .setPositiveButton("OK", (dialog, which) -> { /* Handle yes */ })
                .show();
    }

    private void showToastMessage(String s) {
        Toast toast = Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean areEmptyEmailAddressAndPassword(EditText emailEditText, EditText passwordEditText) {
        return emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty();
    }

    private boolean isUserExists(EditText emailEditText, EditText passwordEditText) {

        String emailAddress = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // get the user from the database
        Cursor cursor = dataBaseHelper.getUser(emailAddress);

        // check if the user exists
        if (cursor.getCount() == 0) {
            showAlertDialog(Constants.USER_NOT_EXISTS);
            return false;
        }

        cursor.moveToFirst();
        if (cursor.getString(3).equals(password)) {
            return true;
        } else {
            showAlertDialog(Constants.PASSWORD_NOT_CORRECT);
            return false;
        }
    }
}