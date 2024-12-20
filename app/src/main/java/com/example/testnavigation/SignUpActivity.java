package com.example.testnavigation;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
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

            // check if the email not empty
            if (!isEmpty(emailAddressEditText)) {
                // Check if the user already exists
                if (dataBaseHelper.checkUserExists(emailAddressEditText.getText().toString())) {
                    showToastMessage(Constants.USER_ALREADY_EXISTS);
                    // colorize the input in red
                    colorizeInputInRed(emailAddressEditText);
                    return;
                }
            }

            if (!areEmptyFields(firstNameEditText, lastNameEditText, passwordEditText, passwordConfirmEditText)) {

                // check if the email address is valid and in the correct format
                if (isEmailValid(emailAddressEditText)) {

                    colorizeInputInWhite(emailAddressEditText);

                    if (isNameValid(firstNameEditText)) {

                        // Set the background color to white
                        colorizeInputInWhite(firstNameEditText);

                        if (isNameValid(lastNameEditText)) {

                            // Set the background color to white
                            colorizeInputInWhite(firstNameEditText);

                            if (isValidPassword(passwordEditText)) {

                                // Set the background color to white
                                colorizeInputInWhite(passwordEditText);

                                if (areEquals(passwordEditText, passwordConfirmEditText)) {

                                    // Add the user to the database
                                    addToDB(emailAddressEditText, firstNameEditText, lastNameEditText, passwordEditText);

                                } else {
                                    showAlertDialog(Constants.PASSWORD_CONFIRM_NOT_VALID);
                                    colorizeInputInRed(passwordConfirmEditText);
                                }
                            } else {
                                showAlertDialog(Constants.PASSWORD_NOT_VALID);
                                colorizeInputInRed(passwordEditText);
                            }
                        } else {
                            showAlertDialog(Constants.LAST_NAME_NOT_VALID);
                            colorizeInputInRed(lastNameEditText);
                        }
                    } else {
                        showAlertDialog(Constants.FIRST_NAME_NOT_VALID);
                        colorizeInputInRed(firstNameEditText);
                    }
                } else {
                    showAlertDialog(Constants.EMAIL_NOT_VALID);
                    colorizeInputInRed(emailAddressEditText);
                }
            } else {

                showAlertDialog(Constants.EMPTY_FIELDS);

                // check if the email not empty
                if (!isEmpty(emailAddressEditText)) {
                    colorizeInputInWhite(emailAddressEditText);
                } else {
                    colorizeInputInRed(emailAddressEditText);
                }
                // check if the first name not empty
                if (!isEmpty(firstNameEditText)) {
                    colorizeInputInWhite(firstNameEditText);
                } else {
                    colorizeInputInRed(firstNameEditText);
                }
                // check if the last name not empty
                if (!isEmpty(lastNameEditText)) {
                    colorizeInputInWhite(lastNameEditText);
                } else {
                    colorizeInputInRed(lastNameEditText);
                }
                // check if the password not empty
                if (!isEmpty(passwordEditText)) {
                    colorizeInputInWhite(passwordEditText);
                } else {
                    colorizeInputInRed(passwordEditText);
                }
                // check if the password confirm not empty
                if (!isEmpty(passwordConfirmEditText)) {
                    colorizeInputInWhite(passwordConfirmEditText);
                } else {
                    colorizeInputInRed(passwordConfirmEditText);
                }
            }
        });

        goToSignInButton.setOnClickListener(view -> {
            // Return back to the main activity
            goToMainActivity();
        });
    }


    private void colorizeInputInWhite(EditText text) {
        text.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private boolean isEmpty(EditText text) {
        return text.getText().toString().isEmpty();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.putExtra("user_primary_key", emailAddressEditText.getText().toString());
        startActivity(intent);
    }

    private void colorizeInputInRed(EditText text) {
        text.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void addToDB(EditText emailAddressEditText, EditText firstNameEditText, EditText lastNameEditText, EditText passwordEditText) {

        // Get the values from the edit text fields
        String emailAddress = emailAddressEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Make an object of the user
        User user = new User(emailAddress, firstName, lastName, password);

        // Insert the user to the database
        dataBaseHelper.insertUser(user);

        //  Show a toast message
        showToastMessage(Constants.USER_ADDED);

        // go to the home page
        goToHomeActivity();
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
        return NameEditText.getText().toString().length() >= 5 && NameEditText.getText().toString().length() <= 20;
    }

    private boolean isEmailValid(EditText emailAddressEditText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressEditText.getText().toString()).matches();
    }

    private boolean areEmptyFields(EditText firstNameEditText, EditText lastNameEditText, EditText passwordEditText, EditText passwordConfirmEditText) {
        return firstNameEditText.getText().toString().isEmpty() || lastNameEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()
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