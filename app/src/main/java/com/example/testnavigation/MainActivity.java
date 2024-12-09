package com.example.testnavigation;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    EditText emailEditText;
    EditText passwordEditText;
    CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Shared Preferences
        sharedPrefManager = SharedPrefManager.getInstance(MainActivity.this);

        // Log in section
        Button signInButton = findViewById(R.id.signInButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        // Make the password to be hidden
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        // Sign up section
        Button signUpButton = findViewById(R.id.goToSignUpButton);

        // Get the id of the 'Remember me' checkbox
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        // load the credentials from the shared preferences
        loadCredentials();


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
//        dataBaseHelper.insertTask(new Task("HomeWork1", "Android", "2020-11-24 10:00", "High", true, true));
////        dataBaseHelper.insertTask(new Task("T21", "Description 2", "2020-11-24 10:00", "Medium", false, true));


        // if the user press on submit, we should check if it is the data base
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if the email and password are correct
                if (!areEmptyEmailAddressAndPassword(emailEditText, passwordEditText)) {

                    // Check if this user is in the database
                    if (isUserExists(emailEditText, passwordEditText)) {

                        // check if the user want to remember him
                        if (rememberMeCheckBox.isChecked()) {
                            sharedPrefManager.writeBoolean("rememberMe", true);
                            sharedPrefManager.writeString("email", emailEditText.getText().toString());
                            sharedPrefManager.writeString("password", passwordEditText.getText().toString());

                        } else {
                            sharedPrefManager.readBoolean("rememberMe", false);
                        }

                        // go to the home page
                        goToHomeActivity();
                    }
                } else {
                    showAlertDialog(Constants.EMPTY_EMAIL_ADDRESS_AND_PASSWORD);
                }
            }
        });

        // if the user press on the sign up button, we should go to the sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to the sign up page
                goToSignUpActivity();
            }
        });
    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
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

    private void loadCredentials() {
        if (sharedPrefManager.readBoolean("rememberMe", false)) {
            emailEditText.setText(sharedPrefManager.readString("email", "no email"));
            passwordEditText.setText(sharedPrefManager.readString("password", "no password"));
            rememberMeCheckBox.setChecked(true);
        }
    }

}