

package com.example.testnavigation;
import android.content.Intent;
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

        Button signUpButton = findViewById(R.id.signUpButton);
        Button goToSignInButton = findViewById(R.id.goToSignInButton);

        try {
            dataBaseHelper = new DataBaseHelper(SignUpActivity.this, "final_project_db", null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        signUpButton.setOnClickListener(view -> {
            if (validateFields()) {
                if (dataBaseHelper.checkUserExists(emailAddressEditText.getText().toString())) {
                    showToastMessage(Constants.USER_ALREADY_EXISTS);
                    colorizeInputInRed(emailAddressEditText);
                } else {
                    addToDB(emailAddressEditText, firstNameEditText, lastNameEditText, passwordEditText);
                }
            }
        });

        goToSignInButton.setOnClickListener(view -> goToMainActivity());
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Validate email
        if (!isEmailValid(emailAddressEditText)) {
            emailAddressEditText.setError("Invalid email format");
            colorizeInputInRed(emailAddressEditText);
            isValid = false;
        } else {
            colorizeInputInWhite(emailAddressEditText);
        }

        // Validate first name
        if (!isNameValid(firstNameEditText)) {
            firstNameEditText.setError("First name must be 5-20 characters");
            colorizeInputInRed(firstNameEditText);
            isValid = false;
        } else {
            colorizeInputInWhite(firstNameEditText);
        }

        // Validate last name
        if (!isNameValid(lastNameEditText)) {
            lastNameEditText.setError("Last name must be 5-20 characters");
            colorizeInputInRed(lastNameEditText);
            isValid = false;
        } else {
            colorizeInputInWhite(lastNameEditText);
        }

        // Validate password
        if (!isValidPassword(passwordEditText)) {
            passwordEditText.setError("Password must be 6-12 characters, include a number, lowercase, and uppercase letter");
            colorizeInputInRed(passwordEditText);
            isValid = false;
        } else {
            colorizeInputInWhite(passwordEditText);
        }

        // Validate confirm password
        if (!areEquals(passwordEditText, passwordConfirmEditText)) {
            passwordConfirmEditText.setError("Passwords do not match");
            colorizeInputInRed(passwordConfirmEditText);
            isValid = false;
        } else {
            colorizeInputInWhite(passwordConfirmEditText);
        }

        return isValid;
    }

    private void addToDB(EditText emailAddressEditText, EditText firstNameEditText, EditText lastNameEditText, EditText passwordEditText) {
        String emailAddress = emailAddressEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        User user = new User(emailAddress, firstName, lastName, password);
        dataBaseHelper.insertUser(user);

        showToastMessage(Constants.USER_ADDED);
        goToHomeActivity();
    }

    private boolean areEquals(EditText password, EditText passwordConfirm) {
        return password.getText().toString().equals(passwordConfirm.getText().toString());
    }

    private boolean isValidPassword(EditText password) {
        String passwordText = password.getText().toString();
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";
        return Pattern.compile(regex).matcher(passwordText).matches();
    }

    private boolean isNameValid(EditText nameEditText) {
        return nameEditText.getText().toString().length() >= 5 && nameEditText.getText().toString().length() <= 20;
    }

    private boolean isEmailValid(EditText emailAddressEditText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressEditText.getText().toString()).matches();
    }

    private void colorizeInputInRed(EditText text) {
        text.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void colorizeInputInWhite(EditText text) {
        text.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.putExtra("user_primary_key", emailAddressEditText.getText().toString());
        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
