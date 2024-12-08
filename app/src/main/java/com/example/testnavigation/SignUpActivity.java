package com.example.testnavigation;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;

    EditText emailAddressEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        passwordEditText = findViewById(R.id.PasswordEditText);
        passwordConfirmEditText = findViewById(R.id.confirmPasswordEditText);

        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordConfirmEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        // sign up button
        Button signUpButton = findViewById(R.id.signUpButton);

        // go to sign in button
        Button goToSignInButton = findViewById(R.id.goToSignInButton);

        // create an object of the database helper with try catch
        try {
            dataBaseHelper = new DataBaseHelper(SignUpActivity.this, "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        }

        signUpButton.setOnClickListener(view -> {

            if (!areEmptyFields(emailAddressEditText, firstNameEditText, lastNameEditText, passwordEditText, passwordConfirmEditText)) {

//                showAllUsers();

                // check if the email address is valid and in the correct format
                if (isEmailValid(emailAddressEditText)) {
                    if (isNameValid(firstNameEditText)) {
                        if (isNameValid(lastNameEditText)) {
                            if (isValidPassword(passwordEditText)) {
                                if (areEquals(passwordEditText, passwordConfirmEditText)) {

                                    // Add the user to the database
                                    addToDB(emailAddressEditText, firstNameEditText, lastNameEditText, passwordEditText);

                                } else {
                                    showAlertDialog(Constants.PASSWORD_CONFIRM_NOT_VALID);
                                }
                            } else {
                                showAlertDialog(Constants.PASSWORD_NOT_VALID);
                            }
                        } else {
                            showAlertDialog(Constants.LAST_NAME_NOT_VALID);
                        }
                    } else {
                        showAlertDialog(Constants.FIRST_NAME_NOT_VALID);
                    }
                } else {
                    showAlertDialog(Constants.EMAIL_NOT_VALID);
                }
            } else {
                showAlertDialog(Constants.EMPTY_FIELDS);
            }

            // clear the fields
            clearFields();

            // Go back to the main activity
            goToMainActivity();
        });

        goToSignInButton.setOnClickListener(view -> {
            // Return back to the main activity
            goToMainActivity();
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void clearFields() {
        emailAddressEditText.setText("");
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        passwordEditText.setText("");
        passwordConfirmEditText.setText("");
    }

    private void addToDB(EditText emailAddressEditText, EditText firstNameEditText, EditText lastNameEditText, EditText passwordEditText) {

        // Get the values from the edit text fields
        String emailAddress = emailAddressEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Check if the user already exists
        if (dataBaseHelper.checkUserExists(emailAddress)) {
            showToastMessage(Constants.USER_ALREADY_EXISTS);
            return;
        }
        // Make an object of the user
        User user = new User(emailAddress, firstName, lastName, password);

        // Insert the user to the database
        dataBaseHelper.insertUser(user);

        //  Show a toast message
        showToastMessage(Constants.USER_ADDED);
    }

    public boolean areEquals(EditText password, EditText passwordConfirm) {
        return password.getText().toString().equals(passwordConfirm.getText().toString());
    }

    public boolean isValidPassword(EditText password) {

        String passwordText = password.getText().toString();

        // Regular expression to check the password criteria
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";

        // Compile the pattern and match it with the input password
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(passwordText).matches();
    }


    private boolean isNameValid(EditText NameEditText) {
        return NameEditText.getText().toString().length() > 5 && NameEditText.getText().toString().length() < 20;
    }

    private boolean isEmailValid(EditText emailAddressEditText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressEditText.getText().toString()).matches();
    }

    private boolean areEmptyFields(EditText emailAddressEditText, EditText firstNameEditText, EditText lastNameEditText, EditText passwordEditText, EditText passwordConfirmEditText) {
        return emailAddressEditText.getText().toString().isEmpty() || firstNameEditText.getText().toString().isEmpty() || lastNameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()
                || passwordConfirmEditText.getText().toString().isEmpty();
    }

    private void showToastMessage(String s) {
        Toast toast = Toast.makeText(SignUpActivity.this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showAlertDialog(String alertMessage) {
        new AlertDialog.Builder(SignUpActivity.this).setMessage(alertMessage)
                .setPositiveButton("OK", (dialog, which) -> { /* Handle yes */ })
                .show();
    }

    public void showAllUsers() {
        Cursor cursor = dataBaseHelper.getAllUsers();
        if (cursor.getCount() == 0) {
            showAlertDialog("No users found");
        } else {
            showAlertDialog(cursor.getCount() + " users found");

            while (cursor.moveToNext()) {
                showAlertDialog(cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
            }
            cursor.close();
        }
    }
}