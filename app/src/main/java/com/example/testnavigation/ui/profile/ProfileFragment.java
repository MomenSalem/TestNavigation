package com.example.testnavigation.ui.profile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.testnavigation.DataBaseHelper;
import com.example.testnavigation.MainActivity;
import com.example.testnavigation.User;
import com.example.testnavigation.databinding.FragmentProfileBinding;

import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the data from the intent
        Intent intent = getActivity().getIntent();
        String userid = intent.getStringExtra("user_primary_key");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity(), "final_project_db", null, 1);


        // Get this user from the database
        Cursor cursor = dataBaseHelper.getUser(userid);
        cursor.moveToFirst();
        User user = createUserObject(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

        // Get the text fields
        EditText emailAddressEditText = binding.emailAddressEditText;
        EditText firstNameEditText = binding.firstNameEditText;
        firstNameEditText.setEnabled(false);
        EditText lastNameEditText = binding.lastNameEditText;
        lastNameEditText.setEnabled(false);
        EditText passwordEditText = binding.passwordEditText;

        emailAddressEditText.setText(user.getEmailAddress());
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        passwordEditText.setText(user.getPassword());

        Button makeChangesButton = binding.makeChangesButton;

        makeChangesButton.setOnClickListener(v -> {

            // get the data from the text fields
            String newEmail = emailAddressEditText.getText().toString();
            String newPassword = passwordEditText.getText().toString();

            if (!(newEmail.equals(user.getEmailAddress()) && newPassword.equals(user.getPassword()))) {
                if (isEmailValid(emailAddressEditText) && isValidPassword(passwordEditText)) {
                    // update the user in the database
                    dataBaseHelper.updateUser(userid, new User(newEmail, user.getFirstName(), user.getLastName(), newPassword));
                    showToastMessage("Changes made successfully");
                } else {
                    showToastMessage("Invalid email or password");
                }
            } else {
                showToastMessage("No changes made");
            }
        });

        return root;
    }

    private boolean isEmailValid(EditText emailAddressEditText) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressEditText.getText().toString()).matches();
    }

    private void showToastMessage(String s) {
        Toast toast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        toast.show();
    }


    public boolean isValidPassword(EditText password) {

        String passwordText = password.getText().toString();

        // Regular expression to check the password criteria
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";

        // Compile the pattern and match it with the input password
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(passwordText).matches();
    }

    private User createUserObject(String emailAddress, String firstName, String lastName, String password) {
        return new User(emailAddress, firstName, lastName, password);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}