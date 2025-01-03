package com.example.testnavigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testnavigation.ui.profile.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testnavigation.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;
    Switch darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // Set up AppBarConfiguration
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_Today, R.id.nav_newT, R.id.nav_allT, R.id.nav_compT, R.id.nav_searchT, R.id.nav_prof)
                .setOpenableLayout(drawer)
                .build();

        // Get the data from the intent
        Intent intent = getIntent();
        String userid = intent.getStringExtra("user_primary_key");
        sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPrefManager.writeString("user_primary_key", userid);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.userNameTV);
        TextView userEmailTextView = headerView.findViewById(R.id.emailTV);

        // Now you can set the text for these TextViews
        userEmailTextView.setText(userid);
        // get the user name from database
        try {
            dataBaseHelper = new DataBaseHelper(this, "final_project_db", null, 1);
            // Use the database helper
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataBaseHelper != null) {
                // Assuming DataBaseHelper has a method to close the database or cleanup
                dataBaseHelper.close();
            }
        }
        Cursor cursor = dataBaseHelper.getUser(userid);

        // Loop through the cursor and add tasks to the ArrayList
        if (cursor.moveToFirst()) {
                String userFirstName = cursor.getString(1);
                String userLastName = cursor.getString(2);
                userNameTextView.setText(userFirstName + " " + userLastName);
        }

        // Set up NavigationController and UI
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Context context = this;

        // Setup Navigation Item Selected Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_logout) {
                // Create an AlertDialog.Builder object
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set dialog title and message
                builder.setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to log out?")  // The message asking the user to confirm

                        // Positive button: Yes
                        .setPositiveButton("Yes", (dialog, idDialog) -> {
                            // Perform the logout action
                            // Handle logout
                            Toast.makeText(HomeActivity.this, "Logged out Successfully...", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(HomeActivity.this, MainActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear stack
                            startActivity(intent1);
                            finish(); // Finish current activity (optional)
                        })

                        // Negative button: No
                        .setNegativeButton("No", (dialog, idDialog) -> {
                            // User canceled, just dismiss the dialog
                            dialog.dismiss();
                        });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }

            // check if the navigation item is the profile item
            if (id == R.id.nav_prof) {
                // send the data to the profile fragment
                Bundle bundle = new Bundle();
                bundle.putString("user_primary_key", userid);
                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setArguments(bundle);
            }


            navController.navigate(id); // This automatically finds the fragment based on the navigation graph
            drawer.closeDrawer(GravityCompat.START); // Close the drawer after selection
            return true;
        });


    }

    private void applyDarkMode(boolean isChecked) {
        if (isChecked) {
            // Apply the dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            sharedPrefManager.writeBoolean("darkTheme", true);
        } else {
            // Apply the light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            sharedPrefManager.writeBoolean("darkTheme", false);
        }

    }

    private void applySavedTheme() {
        // Apply the saved theme
        if (sharedPrefManager.readBoolean("darkTheme", false)) {
            // Apply the dark theme
            darkModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Apply the light theme
            darkModeSwitch.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // suppress lint warning
        // noinspection SuperCallOnBackPressed
        // Do nothing.
        // not return to the previous activity using the back button in phone
    }
}
